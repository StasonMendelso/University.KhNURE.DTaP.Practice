package ua.nure.st.kpp.example.demo.dao.implementation.mysql;

import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.IncomeJournalDAO;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.ItemDaoEvent;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.RecordDaoEvent;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.MySqlConnectionUtils;
import ua.nure.st.kpp.example.demo.dao.observable.IncomeRecordDaoObservable;
import ua.nure.st.kpp.example.demo.dao.observable.ItemDaoObservable;
import ua.nure.st.kpp.example.demo.dao.observer.DaoEventType;
import ua.nure.st.kpp.example.demo.dao.observer.IncomeRecordDaoObserver;
import ua.nure.st.kpp.example.demo.dao.observer.ItemDaoObserver;
import ua.nure.st.kpp.example.demo.entity.Company;
import ua.nure.st.kpp.example.demo.entity.Item;
import ua.nure.st.kpp.example.demo.entity.Journal;
import ua.nure.st.kpp.example.demo.entity.Record;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class MySqlIncomeJournalDAO implements IncomeJournalDAO, IncomeRecordDaoObservable, ItemDaoObservable {
    private final MySqlConnectionUtils mySqlConnectionUtils;
    private final List<IncomeRecordDaoObserver> incomeRecordDaoObservers;
    private final List<ItemDaoObserver> itemDaoObservers;
    private final ItemDAO itemDAO;

    public MySqlIncomeJournalDAO(MySqlConnectionUtils mySqlConnectionUtils, ItemDAO itemDAO) {
        this.mySqlConnectionUtils = mySqlConnectionUtils;
        this.itemDAO = itemDAO;
        this.incomeRecordDaoObservers = new LinkedList<>();
        this.itemDaoObservers = new LinkedList<>();
    }

    @Override
    public void notifyAll(ItemDaoEvent itemDaoEvent) {
        Set<ItemDaoObserver> observersToNotify = itemDaoObservers.stream()
                .filter(observer -> observer.containsAtLeastOneType(itemDaoEvent.getDaoEventType()))
                .collect(Collectors.toSet());

        observersToNotify.forEach(observer -> observer.notify(itemDaoEvent));
    }

    @Override
    public void notifyAll(RecordDaoEvent recordDaoEvent) {
        Set<IncomeRecordDaoObserver> observersToNotify = incomeRecordDaoObservers.stream()
                .filter(observer -> observer.containsAtLeastOneType(recordDaoEvent.getDaoEventType()))
                .collect(Collectors.toSet());

        observersToNotify.forEach(observer -> observer.notify(recordDaoEvent));
    }

    @Override
    public void subscribe(ItemDaoObserver itemDaoObserver) {
        this.itemDaoObservers.add(itemDaoObserver);
    }

    @Override
    public void subscribe(IncomeRecordDaoObserver incomeRecordDaoObserver) {
        this.incomeRecordDaoObservers.add(incomeRecordDaoObserver);
    }

    @Override
    public void unsubscribe(ItemDaoObserver itemDaoObserver) {
        this.itemDaoObservers.remove(itemDaoObserver);
    }

    @Override
    public void unsubscribe(IncomeRecordDaoObserver incomeRecordDaoObserver) {
        this.incomeRecordDaoObservers.remove(incomeRecordDaoObserver);
    }


    private static class Query {
        public static final String INSERT_RECORD = "INSERT INTO income_journal(document_number, items_id, companies_id, date, price, amount) VALUES (?,?,?,?,?,?);";
        public static final String UPDATE_ITEM_QUANTITY_ADDING_VALUE_BY_ID = "UPDATE items SET amount = amount + ? WHERE id = ?;";
        public static final String UPDATE_ITEM_QUANTITY_SUBTRACTING_VALUE_BY_ID = "UPDATE items SET amount = amount - ? WHERE id = ?;";
        public static final String GET_ALL_RECORDS = "SELECT * FROM income_journal JOIN items ON income_journal.items_id = items.id JOIN units ON items.unit_id = units.id JOIN companies ON income_journal.companies_id = companies.id ORDER BY income_journal.id;";
        public static final String UPDATE_RECORD = "UPDATE income_journal SET document_number = ?, items_id = ?, companies_id = ?, price = ?, amount = ? WHERE id = ?";
        public static final String DELETE_RECORD = "DELETE FROM income_journal WHERE id = ?";
        public static final String GET_RECORD = "SELECT * FROM income_journal JOIN items ON income_journal.items_id = items.id JOIN units ON items.unit_id = units.id JOIN companies ON income_journal.companies_id = companies.id WHERE income_journal.id = ?";
    }

    @Override
    public boolean createRecord(Record record) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection(true);
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(Query.INSERT_RECORD, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement preparedStatement2 = connection.prepareStatement(Query.UPDATE_ITEM_QUANTITY_ADDING_VALUE_BY_ID)) {
                mapStatement(preparedStatement1, record);

                preparedStatement2.setInt(1, record.getAmount());
                preparedStatement2.setInt(2, record.getItem().getId());

                preparedStatement1.execute();
                try (ResultSet generatedKeys = preparedStatement1.getGeneratedKeys()) {
                    if (!generatedKeys.next()) {
                        connection.rollback();
                        return false;
                    }
                    int recordId = generatedKeys.getInt(1);
                    if (preparedStatement2.executeUpdate() > 0) {

                        connection.commit();
                        notifyAll(new RecordDaoEvent(DaoEventType.CREATE, read(recordId)));
                        notifyAll(new ItemDaoEvent(DaoEventType.UPDATE, itemDAO.read(record.getItem().getId())));
                        return true;
                    }
                }
            }
        } catch (SQLException exception) {
            mySqlConnectionUtils.rollback(connection);
            throw new DAOException(exception);
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
            try (Statement statement = connection.createStatement()) {
                try (ResultSet resultSet = statement.executeQuery(Query.GET_ALL_RECORDS)) {
                    while (resultSet.next()) {
                        Item item = mapItem(resultSet);
                        Company company = mapCompany(resultSet);
                        Record record = mapRecord(resultSet);
                        record.setItem(item);
                        record.setCompany(company);
                        builder.addRecord(record);
                    }
                }
            }

        } catch (SQLException exception) {
            throw new DAOException(exception);
        } finally {
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
        int id = resultSet.getInt("income_journal.id");
        String documentNumber = resultSet.getString("income_journal.document_number");
        LocalDateTime date = resultSet.getTimestamp("income_journal.date").toLocalDateTime();
        BigDecimal price = resultSet.getBigDecimal("income_journal.price");
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
                 PreparedStatement preparedStatement2 = connection.prepareStatement(Query.UPDATE_ITEM_QUANTITY_SUBTRACTING_VALUE_BY_ID);
                 PreparedStatement preparedStatement3 = connection.prepareStatement(Query.UPDATE_RECORD);
                 PreparedStatement preparedStatement4 = connection.prepareStatement(Query.UPDATE_ITEM_QUANTITY_ADDING_VALUE_BY_ID)) {
                preparedStatement1.setInt(1, id);
                try (ResultSet resultSet = preparedStatement1.executeQuery()) {
                    if (resultSet.next()) {
                        int oldRecordAmount = resultSet.getInt("amount");
                        int itemId = resultSet.getInt("items_id");
                        preparedStatement2.setInt(1, oldRecordAmount);
                        preparedStatement2.setInt(2, itemId);
                        preparedStatement2.executeUpdate();

                        mapUpdateStatement(preparedStatement3, id, record);
                        preparedStatement3.executeUpdate();

                        preparedStatement4.setInt(1, record.getAmount());
                        preparedStatement4.setInt(2, record.getItem().getId());
                        preparedStatement4.executeUpdate();

                        connection.commit();
                        notifyAll(new RecordDaoEvent(DaoEventType.UPDATE, read(id)));
                        if(record.getItem().getId() == itemId){
                            notifyAll(new ItemDaoEvent(DaoEventType.UPDATE, itemDAO.read(itemId)));
                            return true;
                        }
                        notifyAll(new ItemDaoEvent(DaoEventType.UPDATE, itemDAO.read(record.getItem().getId())));
                        notifyAll(new ItemDaoEvent(DaoEventType.UPDATE, itemDAO.read(itemId)));
                        return true;
                    }
                    return false;
                }

            }
        } catch (SQLException exception) {
            mySqlConnectionUtils.rollback(connection);
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
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
                 PreparedStatement preparedStatement2 = connection.prepareStatement(Query.UPDATE_ITEM_QUANTITY_SUBTRACTING_VALUE_BY_ID);
                 PreparedStatement preparedStatement3 = connection.prepareStatement(Query.DELETE_RECORD)) {
                preparedStatement1.setInt(1, id);
                try (ResultSet resultSet = preparedStatement1.executeQuery()) {
                    if (resultSet.next()) {
                        int amount = resultSet.getInt("amount");
                        int itemId = resultSet.getInt("items_id");
                        preparedStatement2.setInt(1, amount);
                        preparedStatement2.setInt(2, itemId);
                        preparedStatement2.executeUpdate();
                        Record beforeDelete = read(id);

                        preparedStatement3.setInt(1, id);

                        preparedStatement3.executeUpdate();
                        connection.commit();

                        notifyAll(new RecordDaoEvent(DaoEventType.DELETE, beforeDelete));
                        notifyAll(new ItemDaoEvent(DaoEventType.UPDATE, itemDAO.read(itemId)));
                        return true;
                    }
                }
            }
        } catch (SQLException exception) {
            mySqlConnectionUtils.rollback(connection);
            throw new DAOException(exception);
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
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.GET_RECORD)) {
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
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return null;
    }
}
