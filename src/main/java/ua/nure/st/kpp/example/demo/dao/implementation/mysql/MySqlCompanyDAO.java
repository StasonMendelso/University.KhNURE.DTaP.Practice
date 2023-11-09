package ua.nure.st.kpp.example.demo.dao.implementation.mysql;

import ua.nure.st.kpp.example.demo.dao.CompanyDAO;
import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.CompanyDaoEvent;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.MySqlConnectionUtils;
import ua.nure.st.kpp.example.demo.dao.observable.CompanyDaoObservable;
import ua.nure.st.kpp.example.demo.dao.observer.CompanyDaoObserver;
import ua.nure.st.kpp.example.demo.dao.observer.DaoEventType;
import ua.nure.st.kpp.example.demo.entity.Company;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MySqlCompanyDAO implements CompanyDAO, CompanyDaoObservable {
    private final MySqlConnectionUtils mySqlConnectionUtils;
    private final List<CompanyDaoObserver> companyDaoObservers;

    public MySqlCompanyDAO(MySqlConnectionUtils mySqlConnectionUtils) {
        this.mySqlConnectionUtils = mySqlConnectionUtils;
        this.companyDaoObservers = new LinkedList<>();
    }

    @Override
    public void notifyAll(CompanyDaoEvent companyDaoEvent) {
        Set<CompanyDaoObserver> observersToNotify = companyDaoObservers.stream().filter(observer -> observer.containsAtLeastOneType(companyDaoEvent.getDaoEventType())).collect(Collectors.toSet());

        observersToNotify.forEach(observer -> observer.notify(companyDaoEvent));
    }

    @Override
    public void subscribe(CompanyDaoObserver companyDaoObserver) {
        companyDaoObservers.add(companyDaoObserver);
    }

    @Override
    public void unsubscribe(CompanyDaoObserver companyDaoObserver) {
        companyDaoObservers.remove(companyDaoObserver);
    }

    private static class Query {
        public static final String INSERT_COMPANY = "INSERT INTO companies(name,email,address) VALUES (?,?,?);";
        public static final String UPDATE_COMPANY_BY_ID = "UPDATE companies SET name = ?, email = ?, address = ? WHERE id = ?;";
        public static final String GET_ALL_COMPANIES = "SELECT * FROM companies ORDER BY id;";
        public static final String GET_COMPANY_BY_ID = "SELECT * FROM companies WHERE id = ?;";
        public static final String GET_ALL_COMPANIES_ID = "SELECT id FROM companies ORDER BY id";
    }

    @Override
    public boolean create(Company company) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.INSERT_COMPANY, Statement.RETURN_GENERATED_KEYS)) {
                int index = 1;
                preparedStatement.setString(index++, company.getName());
                preparedStatement.setString(index++, company.getEmail());
                preparedStatement.setString(index, company.getAddress());

                preparedStatement.execute();
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys();) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        notifyAll(new CompanyDaoEvent(DaoEventType.CREATE, read(id)));
                        return true;
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return false;
    }

    @Override
    public List<Company> readAll() throws DAOException {
        List<Company> companies = new LinkedList<>();
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(Query.GET_ALL_COMPANIES)) {
                    while (resultSet.next()) {
                        Company company = mapCompany(resultSet);
                        companies.add(company);
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return companies;
    }

    @Override
    public Company read(int id) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.GET_COMPANY_BY_ID)) {
                preparedStatement.setInt(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapCompany(resultSet);
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return null;
    }

    @Override
    public List<Integer> readAllAvailableId() throws DAOException {
        List<Integer> idList = new LinkedList<>();
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(Query.GET_ALL_COMPANIES_ID)) {
                    while (resultSet.next()) {
                        idList.add(resultSet.getInt("id"));
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return idList;
    }

    private Company mapCompany(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String address = resultSet.getString("address");
        return new Company.Builder<>().id(id).name(name).email(email).address(address).build();
    }

    @Override
    public boolean update(int id, Company company) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.UPDATE_COMPANY_BY_ID)) {
                int index = 1;
                preparedStatement.setString(index++, company.getName());
                preparedStatement.setString(index++, company.getEmail());
                preparedStatement.setString(index++, company.getAddress());
                preparedStatement.setInt(index, id);

                if (preparedStatement.executeUpdate() > 0) {
                    notifyAll(new CompanyDaoEvent(DaoEventType.UPDATE, read(id)));
                    return true;
                }
                return false;
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
    }


}
