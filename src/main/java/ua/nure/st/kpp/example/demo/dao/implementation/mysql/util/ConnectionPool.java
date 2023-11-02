package ua.nure.st.kpp.example.demo.dao.implementation.mysql.util;

import java.sql.Connection;

/**
 * @author Stanislav Hlova
 */
public interface ConnectionPool {
	Connection getConnection();
	boolean releaseConnection(Connection connection);
}
