package ua.nure.st.kpp.example.demo.dao.implementation.mysql.util;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.MySqlDAOConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Stanislav Hlova
 */
public class BasicConnectionPool implements ConnectionPool {
	private final String url;
	private final Properties databaseProperties = new Properties();

	private final List<Connection> connectionPool;
	private int currentPoolSize;
	private final int initialPoolSize;
	private final int maxPoolSize;

	public BasicConnectionPool(MySqlDAOConfig config) {
		this.url = config.getUrl();
		databaseProperties.setProperty("user", config.getUser());
		databaseProperties.setProperty("password", config.getPassword());
		this.initialPoolSize = config.getPoolSize();
		this.maxPoolSize = config.getMaxPoolSize();
		this.connectionPool = new ArrayList<>(initialPoolSize);
		init();
	}

	private void init() {
		for (int i = 0; i < initialPoolSize; i++) {
			connectionPool.add(createConnection());
		}
		this.currentPoolSize = connectionPool.size();
	}

	//here we need a synchronization, because each connection must be taken only by ONE thread.
	// If we don't make it, then one connection, will be used for 1 or more threads.
	// There can be a big logical errors, because one thread can write a lot of data in connection, and another thread
	// will be confused, from where these new data came. Moreover, even we use logger, there can be very confused logs
	// from different threads, that use common connection.
	// So, one thread = one connection

	// Also, without synchronizing this method, we can get an error when we have one connection in the pool,
	// and two threads have already passed the connectionPool.isEmpty() check, but the first thread took the connection from the pool,
	// making it empty, and the other thread will get an ArrayIndexOutOfBound error, even though the checking is passed successfully.

	// Unfortunately, there isn't sufficient for thread-safe class. There is another situation. Connection pool is empty and reached max size.
	// First thread enters in getConnection() method, blocks it, passes connectionPool.isEmpty() checks,  and the second thread at this time
	// releaseConnection(). As a result, we have got a connectionPool with one available connection, but the first thread
	// goes to second checking and throw exception, that maximum pool size reached. But we have here a synchronized keyword! Yes, but
	// it doesn't help us, because synchronized keyword makes a block for the method, not on the class variables, which are used in
	// method body.
	// So, how to fix this problem? I have only one solution for this, we need to somehow to synchronize on the collection, not method, because
	// the collection is used across two methods. We can make it using a synchronized keyword. So, when the first thread get connection, it blocks
	// not only the method, in addition a collection. Thanks to it, another thread can't perform releaseConnection, when first thread getConnection from
	// collection.
	@Override
	public Connection getConnection() {
		synchronized (connectionPool) {
			if (connectionPool.isEmpty()) {
				if (this.currentPoolSize >= maxPoolSize) {
					throw new RuntimeException("Maximum pool size reached, no available connections!");
				}
				connectionPool.add(createConnection());
				this.currentPoolSize++;
			}
			return connectionPool.remove(connectionPool.size() - 1);
		}
	}

	//Here we also have a problem, when a lot of threads releaseConnection. As we have got a not thread safe implementation of a List,
	// there can be a situation, when two connections are added at the same Node (for LinkedList) or the same cell (for ArrayList).
	// As a result, we lost a one connection in the pool, but the size of the connection pool is wrong. this won't happen very often,
	// but if the server is running for a long time, new connections will be added to the pool, at some point the pool
	// will actually become empty, even though all connections have returned and after the maximum pool size is reached,
	// each thread will be got an error.
	// So we need make a collection synchronized (for example wraps it using Collections.synchronizedList() or
	// thread-safe implementation CopyOnWriteList) or we can synchronize the method. Let's make the easiest way
	@Override
	public synchronized boolean releaseConnection(Connection connection) {
		return connectionPool.add(connection);
	}

	private Connection createConnection() {
		try {
			return DriverManager.getConnection(this.url, this.databaseProperties);
		} catch (SQLException exception) {
			throw new RuntimeException("Can't create a new connection", exception);
		}
	}
}
