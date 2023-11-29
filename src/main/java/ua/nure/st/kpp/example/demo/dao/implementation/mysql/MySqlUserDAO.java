package ua.nure.st.kpp.example.demo.dao.implementation.mysql;

import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.UserDAO;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.MySqlConnectionUtils;
import ua.nure.st.kpp.example.demo.entity.Role;
import ua.nure.st.kpp.example.demo.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Stanislav Hlova
 */
public class MySqlUserDAO implements UserDAO {

    private final MySqlConnectionUtils mySqlConnectionUtils;

    public MySqlUserDAO(MySqlConnectionUtils mySqlConnectionUtils) {
        this.mySqlConnectionUtils = mySqlConnectionUtils;
    }

    private static class Query {
        private static final String INSERT_USER = "INSERT INTO users(username, password, role_id) VALUE (?,?,?)";
        private static final String SELECT_USER_BY_USERNAME = "SELECT users.id 'user_id',username,password, role FROM users JOIN warehousecpp.role r on r.id = users.role_id WHERE username = ?";
        private static final String SELECT_ROLE_BY_ID = "SELECT * FROM role WHERE role = ?";
        private static final String SELECT_USER_BY_ID = "SELECT users.id 'user_id',username,password, role FROM users JOIN warehousecpp.role r on r.id = users.role_id WHERE users.id = ?";
    }

    @Override
    public User save(User user) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection(true);
            int roleId = getRoleId(user.getRole(), connection);

            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.INSERT_USER, Statement.RETURN_GENERATED_KEYS);) {
                mapInsertStatement(preparedStatement, user, roleId);
                preparedStatement.execute();
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        throw new DAOException("Can't find inserted user");
                    }
                    int id = generatedKeys.getInt(1);
                    try(PreparedStatement preparedStatement1 = connection.prepareStatement(Query.SELECT_USER_BY_ID)){
                        preparedStatement1.setInt(1, id);
                        try(ResultSet resultSet = preparedStatement1.executeQuery()){
                            if(!resultSet.next()){
                                throw new DAOException("Can't find inserted user");
                            }
                            return mapUser(resultSet);
                        }
                    }
                }
            }
        } catch (SQLException exception) {
            mySqlConnectionUtils.rollback(connection);
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
    }

    private int getRoleId(Role role, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(Query.SELECT_ROLE_BY_ID)) {
            preparedStatement.setString(1, role.name());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }
        throw new DAOException("Can't find role with value: " + role.name());
    }

    private void mapInsertStatement(PreparedStatement preparedStatement, User user, int roleId) throws SQLException {
        int index = 1;
        preparedStatement.setString(index++, user.getUsername());
        preparedStatement.setString(index++, user.getPassword());
        preparedStatement.setInt(index++, roleId);
    }

    @Override
    public User findByUsername(String username) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection(true);
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.SELECT_USER_BY_USERNAME)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapUser(resultSet);
                    }
                }
            }
        } catch (SQLException exception) {
            mySqlConnectionUtils.rollback(connection);
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return null;
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("user_id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String role = resultSet.getString("role");

        return new User(id, username, password, Role.getInstance(role));
    }
}
