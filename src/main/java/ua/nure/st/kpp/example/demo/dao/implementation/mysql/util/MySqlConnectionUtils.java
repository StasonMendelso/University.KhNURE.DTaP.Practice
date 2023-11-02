package ua.nure.st.kpp.example.demo.dao.implementation.mysql.util;

import ua.nure.st.kpp.example.demo.dao.DAOException;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlConnectionUtils {
    private final ConnectionPool connectionPool;
    public MySqlConnectionUtils(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public Connection getConnection() throws SQLException {
        return getConnection(false);
    }

    public Connection getConnection(boolean transaction) throws SQLException {
        Connection connection = connectionPool.getConnection();
        if (transaction) {
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        }
        return connection;
    }
    public void rollback(Connection connection) throws DAOException {
        if(connection!=null){
            try {
                connection.rollback();
            } catch (SQLException exception) {
                throw new DAOException(exception);
            }
        }
    }
    public void close(Connection connection) throws DAOException {
        if(connection!=null){
            try {
                connection.setAutoCommit(true);
                connectionPool.releaseConnection(connection);
            } catch (SQLException exception) {
                throw new DAOException(exception);
            }
        }
    }
}
