package ua.nure.st.kpp.example.demo.dao.implementation.proxy;

import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.util.List;

/**
 * @author Stanislav Hlova
 */
public class SecurityProxyItemDAO extends SecurityProxy implements ItemDAO{

    private final ItemDAO itemDAO;

    public SecurityProxyItemDAO(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public boolean create(Item item) throws DAOException {
        checkAccessForModification(getUserDetails());
        return itemDAO.create(item);
    }

    @Override
    public boolean updateQuantity(int id, int quantity) throws DAOException {
        checkAccessForModification(getUserDetails());
        return itemDAO.updateQuantity(id, quantity);
    }

    @Override
    public boolean update(int id, Item item) throws DAOException {
        checkAccessForModification(getUserDetails());
        return itemDAO.update(id, item);
    }

    @Override
    public List<Item> readAll() throws DAOException {
        return itemDAO.readAll();
    }

    @Override
    public Item read(String vendor) throws DAOException {
        return itemDAO.read(vendor);
    }

    @Override
    public Item read(int id) throws DAOException {
        return itemDAO.read(id);
    }

    @Override
    public boolean delete(int id) throws DAOException {
        checkAccessForModification(getUserDetails());
        return itemDAO.delete(id);
    }

    @Override
    public List<Integer> readAllAvailableId() throws DAOException {
        return itemDAO.readAllAvailableId();
    }

    @Override
    public List<Item> readAllByName(String name) throws DAOException {
        return itemDAO.readAllByName(name);
    }
}
