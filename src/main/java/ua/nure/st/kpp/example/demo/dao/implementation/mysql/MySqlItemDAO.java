package ua.nure.st.kpp.example.demo.dao.implementation.mysql;

import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.ItemDaoEvent;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.MySqlConnectionUtils;
import ua.nure.st.kpp.example.demo.dao.observable.ItemDaoObservable;
import ua.nure.st.kpp.example.demo.dao.observer.DaoEventType;
import ua.nure.st.kpp.example.demo.dao.observer.ItemDaoObserver;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

public class MySqlItemDAO implements ItemDAO, ItemDaoObservable {
    private final MySqlConnectionUtils mySqlConnectionUtils;
    private final List<ItemDaoObserver> itemDaoObservers;

    public MySqlItemDAO(MySqlConnectionUtils mySqlConnectionUtils) {
        this.mySqlConnectionUtils = mySqlConnectionUtils;
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
    public void subscribe(ItemDaoObserver itemDaoObserver) {
        this.itemDaoObservers.add(itemDaoObserver);
    }

    @Override
    public void unsubscribe(ItemDaoObserver itemDaoObserver) {
        this.itemDaoObservers.add(itemDaoObserver);
    }

    private static class Query {
        public static final String GET_ALL_ITEMS = "SELECT items.id, vendor, name, unit, weight, amount, reserve_rate FROM items JOIN units ON items.unit_id = units.id WHERE amount IS NOT NULL  ORDER BY id;";
        public static final String GET_ITEM_BY_VENDOR = "SELECT items.id, vendor, name, unit, weight, amount, reserve_rate FROM items JOIN units ON items.unit_id = units.id WHERE vendor = ?";
        public static final String GET_ITEM_BY_ID = "SELECT items.id, vendor, name, unit, weight, amount, reserve_rate FROM items JOIN units ON items.unit_id = units.id WHERE items.id = ?";

        public static final String INSERT_ITEM = "INSERT INTO items(vendor, name, unit_id, weight, amount, reserve_rate) VALUES(?,?,?,?,?,?);";
        public static final String GET_UNIT_ID_BY_VALUE = "SELECT id FROM units WHERE unit=?;";
        public static final String INSERT_UNIT = "INSERT INTO units(unit) VALUES(?);";
        public static final String UPDATE_ITEM_QUANTITY_BY_ID = "UPDATE items SET amount = ? WHERE id = ?;";
        public static final String UPDATE_ITEM = "UPDATE items SET vendor = ?, name = ?,unit_id = ?, weight = ?, reserve_rate = ? WHERE id = ?;";
        public static final String SET_AMOUNT_NULL = "UPDATE items SET amount = NULL WHERE id = ?;";
        public static final String GET_ALL_ITEMS_ID = "SELECT id FROM items WHERE amount IS NOT NULL ORDER BY id";
    }

    private static class Procedure {
        public static final String GET_ITEM_BY_NAME = "CALL get_all_items_by_name(?);";
    }

    @Override
    public boolean create(Item item) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            int unitId = getUnitId(item, connection);

            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.INSERT_ITEM, Statement.RETURN_GENERATED_KEYS);) {
                mapInsertStatement(preparedStatement, item, unitId);
                preparedStatement.execute();
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        notifyAll(new ItemDaoEvent(DaoEventType.CREATE, read(id)));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException exception) {
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
    }

    private void mapInsertStatement(PreparedStatement preparedStatement, Item item, int unitId) throws SQLException {
        int index = 1;
        preparedStatement.setString(index++, item.getVendor());
        preparedStatement.setString(index++, item.getName());
        preparedStatement.setInt(index++, unitId);
        preparedStatement.setBigDecimal(index++, item.getWeight());
        preparedStatement.setInt(index++, item.getAmount());
        preparedStatement.setInt(index, item.getReserveRate());
    }

    @Override
    public boolean updateQuantity(int id, int quantity) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.UPDATE_ITEM_QUANTITY_BY_ID)) {

                int index = 1;
                preparedStatement.setInt(index++, quantity);
                preparedStatement.setInt(index, id);

                if(preparedStatement.executeUpdate() > 0){
                    notifyAll(new ItemDaoEvent(DaoEventType.UPDATE,read(id)));
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

    @Override
    public boolean update(int id, Item item) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.UPDATE_ITEM)) {
                int unitId = getUnitId(item, connection);

                mapUpdateStatement(preparedStatement, id, item, unitId);

                if(preparedStatement.executeUpdate() > 0){
                    notifyAll(new ItemDaoEvent(DaoEventType.UPDATE, read(id)));
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

    private void mapUpdateStatement(PreparedStatement preparedStatement, int id, Item item, int unitId) throws SQLException {
        int index = 1;
        preparedStatement.setString(index++, item.getVendor());
        preparedStatement.setString(index++, item.getName());
        preparedStatement.setInt(index++, unitId);
        preparedStatement.setBigDecimal(index++, item.getWeight());
        preparedStatement.setInt(index++, item.getReserveRate());
        preparedStatement.setInt(index, id);
    }

    private int getUnitId(Item item, Connection connection) throws SQLException {
        int unitId = -1;
        try (PreparedStatement preparedStatement = connection.prepareStatement(Query.GET_UNIT_ID_BY_VALUE)) {
            preparedStatement.setString(1, item.getUnit());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    unitId = resultSet.getInt("id");
                } else {
                    try (PreparedStatement preparedStatement1 = connection.prepareStatement(Query.INSERT_UNIT, Statement.RETURN_GENERATED_KEYS)) {
                        preparedStatement1.setString(1, item.getUnit());
                        preparedStatement1.execute();
                        try (ResultSet resultSet1 = preparedStatement1.getGeneratedKeys()) {
                            if (resultSet1.next()) {
                                unitId = resultSet1.getInt(1);
                            }
                        }
                    }
                }
            }
        }
        return unitId;
    }

    @Override
    public List<Item> readAll() throws DAOException {
        List<Item> items = new LinkedList<>();
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(Query.GET_ALL_ITEMS)) {
                while (resultSet.next()) {
                    Item item = mapItem(resultSet);
                    items.add(item);
                }
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return items;
    }

    @Override
    public Item read(String vendor) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.GET_ITEM_BY_VENDOR)) {
                preparedStatement.setString(1, vendor);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapItem(resultSet);
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
    public Item read(int id) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.GET_ITEM_BY_ID)) {
                preparedStatement.setInt(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapItem(resultSet);
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
    public boolean delete(int id) throws DAOException {
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(Query.SET_AMOUNT_NULL)) {
                preparedStatement.setInt(1, id);
                Item beforeDelete = read(id);
                if (preparedStatement.executeUpdate() > 0) {
                    notifyAll(new ItemDaoEvent(DaoEventType.DELETE, beforeDelete));
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

    @Override
    public List<Integer> readAllAvailableId() throws DAOException {
        List<Integer> idList = new LinkedList<>();
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(Query.GET_ALL_ITEMS_ID)) {
                while (resultSet.next()) {
                    idList.add(resultSet.getInt("id"));
                }
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return idList;

    }

    @Override
    public List<Item> readAllByName(String name) throws DAOException {
        List<Item> items = new LinkedList<>();
        Connection connection = null;
        try {
            connection = mySqlConnectionUtils.getConnection();
            try (CallableStatement callableStatement = connection.prepareCall(Procedure.GET_ITEM_BY_NAME)) {
                callableStatement.setString("name", name);
                try (ResultSet resultSet = callableStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Item item = mapItem(resultSet);
                        items.add(item);
                    }
                }
            }
        } catch (SQLException exception) {
            throw new DAOException(exception);
        } finally {
            mySqlConnectionUtils.close(connection);
        }
        return items;
    }

    private Item mapItem(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String vendor = resultSet.getString("vendor");
        String name = resultSet.getString("name");
        String unit = resultSet.getString("unit");
        BigDecimal weight = resultSet.getBigDecimal("weight");
        int amount = resultSet.getInt("amount");
        int reserveRate = resultSet.getInt("reserve_rate");
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
}
