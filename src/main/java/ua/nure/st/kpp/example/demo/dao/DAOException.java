package ua.nure.st.kpp.example.demo.dao;

import java.sql.SQLException;
public class DAOException extends SQLException {
    public DAOException(Throwable exception){
        super(exception);
    }

    public DAOException(String reason) {
        super(reason);
    }

    @Override
    public String toString() {
        return "DBException{" +
                super.toString()+
                "}";
    }
}
