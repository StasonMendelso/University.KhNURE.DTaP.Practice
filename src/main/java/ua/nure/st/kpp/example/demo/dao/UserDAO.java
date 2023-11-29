package ua.nure.st.kpp.example.demo.dao;

import ua.nure.st.kpp.example.demo.entity.User;

/**
 * @author Stanislav Hlova
 */
public interface UserDAO {
    User save(User user) throws DAOException;

    User findByUsername(String username) throws DAOException;
}
