package ua.nure.st.kpp.example.demo.dao.implementation.mysql;

import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.OutcomeJournalDAO;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.MySqlConnectionUtils;
import ua.nure.st.kpp.example.demo.entity.Company;
import ua.nure.st.kpp.example.demo.entity.Item;
import ua.nure.st.kpp.example.demo.entity.Journal;
import ua.nure.st.kpp.example.demo.entity.Record;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MySqlOutcomeJournalDAO implements OutcomeJournalDAO {
    private final MySqlConnectionUtils mySqlConnectionUtils;

    public MySqlOutcomeJournalDAO(MySqlConnectionUtils mySqlConnectionUtils) {

        this.mySqlConnectionUtils = mySqlConnectionUtils;
    }
    private static class Query {
        public static final String INSERT_RECORD = "INSERT INTO outcome_journal(document_number, items_id, companies_id, date, price, amount) VALUES (?,?,?,?,?,?);";
        public static final String UPDATE_ITEM_QUANTITY_ADDING_VALUE_BY_ID = "UPDATE items SET amount = amount + ? WHERE id = ?;";
        public static final String UPDATE_ITEM_QUANTITY_SUBTRACTING_VALUE_BY_ID = "UPDATE items SET amount = amount - ? WHERE id = ?;";
        public static final String GET_ALL_RECORDS = "SELECT * FROM outcome_journal JOIN items ON outcome_journal.items_id = items.id JOIN units ON items.unit_id = units.id JOIN companies ON outcome_journal.companies_id = companies.id ORDER BY outcome_journal.id;";
        public static final String UPDATE_RECORD = "UPDATE outcome_journal SET document_number = ?, items_id = ?, companies_id = ?, price = ?, amount = ? WHERE id = ?";
        public static final String DELETE_RECORD = "DELETE FROM outcome_journal WHERE id = ?";
        public static final String GET_RECORD = "SELECT * FROM outcome_journal JOIN items ON outcome_journal.items_id = items.id JOIN units ON items.unit_id = units.id JOIN companies ON outcome_journal.companies_id = companies.id WHERE outcome_journal.id = ?";
    }

    @Override
    public boolean createRecord(Record record) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection(true);
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(Query.INSERT_RECORD);
                 PreparedStatement preparedStatement2 = connection.prepareStatement(Query.UPDATE_ITEM_QUANTITY_SUBTRACTING_VALUE_BY_ID)) {
                mapStatement(preparedStatement1, record);

                preparedStatement2.setInt(1, record.getAmount());
                preparedStatement2.setInt(2, record.getItem().getId());

                preparedStatement1.execute();
                preparedStatement2.executeUpdate();
                connection.commit();
                return true;
            }
        } catch (SQLException exception) {
            mySqlConnectionUtils.rollback(connection);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return false;
    }

    @Override
    public Journal readAll() throws DAOException {
        Journal.Builder builder = new Journal.Builder<>();
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(Query.GET_ALL_RECORDS)) {
                while (resultSet.next()) {
                    Item item = mapItem(resultSet);
                    Company company = mapCompany(resultSet);
                    Record record = mapRecord(resultSet);
                    record.setItem(item);
                    record.setCompany(company);
                    builder.addRecord(record);
                }
            }

        } catch (SQLException exception) {
            throw new DAOException(exception);
        }finally {
            mySqlConnectionUtils.close(connection);
        }
        return builder.build();
    }


    private Item mapItem(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("items.id");
        String vendor = resultSet.getString("items.vendor");
        String name = resultSet.getString("items.name");
        String unit = resultSet.getString("units.unit");
        BigDecimal weight = resultSet.getBigDecimal("items.weight");
        int amount = resultSet.getInt("items.amount");
        int reserveRate = resultSet.getInt("items.reserve_rate");
        return new Item.Builder<>()
                .id(id)
                .name(name)
                .vendor(vendor)
                .weight(weight)
                .unit(unit)
                .amount(amount)
                .reserveRate(reserveRate)
                .build();
    }

    private Company mapCompany(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("companies.id");
        String name = resultSet.getString("companies.name");
        String email = resultSet.getString("companies.email");
        String address = resultSet.getString("companies.address");
        return new Company.Builder<>()
                .id(id)
                .name(name)
                .email(email)
                .address(address)
                .build();
    }

    private Record mapRecord(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("outcome_journal.id");
        String documentNumber = resultSet.getString("outcome_journal.document_number");
        LocalDateTime date = resultSet.getTimestamp("outcome_journal.date").toLocalDateTime();
        BigDecimal price = resultSet.getBigDecimal("outcome_journal.price");
        int amount = resultSet.getInt("amount");
        return new Record.Builder<>()
                .id(id)
                .documentNumber(documentNumber)
                .date(date)
                .price(price)
                .amount(amount)
                .build();
    }

    @Override
    public boolean updateRecord(int id, Record record) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection(true);
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(Query.GET_RECORD);
                 PreparedStatement preparedStatement2 = connection.prepareStatement(Query.UPDATE_ITEM_QUANTITY_ADDING_VALUE_BY_ID);
                 PreparedStatement preparedStatement3 = connection.prepareStatement(Query.UPDATE_RECORD);
                 PreparedStatement preparedStatement4 = connection.prepareStatement(Query.UPDATE_ITEM_QUANTITY_SUBTRACTING_VALUE_BY_ID)) {
                preparedStatement1.setInt(1,id);
                try (ResultSet resultSet = preparedStatement1.executeQuery()) {
                    if (resultSet.next()) {
                        int oldRecordAmount = resultSet.getInt("amount");
                        int itemId = resultSet.getInt("items_id");
                        preparedStatement2.setInt(1, oldRecordAmount);
                        preparedStatement2.setInt(2, itemId);
                        preparedStatement2.executeUpdate();

                        mapUpdateStatement(preparedStatement3, id, record);
                        preparedStatement3.executeUpdate();

                        preparedStatement4.setInt(1,record.getAmount());
                        preparedStatement4.setInt(2,record.getItem().getId());
                        preparedStatement4.executeUpdate();
                        connection.commit();
                        return true;
                    }
                    return false;
                }

            }
        } catch (SQLException exception) {
            mySqlConnectionUtils.rollback(connection);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return false;
    }

    private void mapUpdateStatement(PreparedStatement preparedStatement, int id, Record record) throws SQLException {
        int index = 1;
        preparedStatement.setString(index++, record.getDocumentNumber());
        preparedStatement.setInt(index++, record.getItem().getId());
        preparedStatement.setInt(index++, record.getCompany().getId());
        preparedStatement.setBigDecimal(index++, record.getPrice());
        preparedStatement.setInt(index++, record.getAmount());
        preparedStatement.setInt(index, id);
    }

    private void mapStatement(PreparedStatement preparedStatement, Record record) throws SQLException {
        int index = 1;
        preparedStatement.setString(index++, record.getDocumentNumber());
        preparedStatement.setInt(index++, record.getItem().getId());
        preparedStatement.setInt(index++, record.getCompany().getId());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        preparedStatement.setTimestamp(index++, Timestamp.valueOf(record.getDate().format(dateTimeFormatter)));
        preparedStatement.setBigDecimal(index++, record.getPrice());
        preparedStatement.setInt(index, record.getAmount());
    }


    @Override
    public boolean deleteRecord(int id) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection(true);
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(Query.GET_RECORD);
                 PreparedStatement preparedStatement2 = connection.prepareStatement(Query.UPDATE_ITEM_QUANTITY_ADDING_VALUE_BY_ID);
                 PreparedStatement preparedStatement3 = connection.prepareStatement(Query.DELETE_RECORD)) {
                preparedStatement1.setInt(1, id);
                try (ResultSet resultSet = preparedStatement1.executeQuery()) {
                    if (resultSet.next()) {
                        int amount = resultSet.getInt("amount");
                        int itemId = resultSet.getInt("items_id");
                        preparedStatement2.setInt(1, amount);
                        preparedStatement2.setInt(2, itemId);
                        preparedStatement2.executeUpdate();
                        preparedStatement3.setInt(1, id);

                        preparedStatement3.executeUpdate();
                        connection.commit();
                        return true;
                    }
                    return false;
                }


            }
        }catch (SQLException exception) {
            mySqlConnectionUtils.rollback(connection);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return false;
    }

    @Override
    public Record read(int id) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try( PreparedStatement preparedStatement = connection.prepareStatement(Query.GET_RECORD)){
                preparedStatement.setInt(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        Record record = mapRecord(resultSet);
                        Company company = mapCompany(resultSet);
                        Item item = mapItem(resultSet);
                        record.setItem(item);
                        record.setCompany(company);
                        return record;
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        }finally {
            mySqlConnectionUtils.close(connection);
        }
        return null;
    }
}
