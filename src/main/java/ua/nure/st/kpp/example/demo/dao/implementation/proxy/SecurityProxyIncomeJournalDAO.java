package ua.nure.st.kpp.example.demo.dao.implementation.proxy;

import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.IncomeJournalDAO;
import ua.nure.st.kpp.example.demo.entity.Journal;
import ua.nure.st.kpp.example.demo.entity.Record;

/**
 * @author Stanislav Hlova
 */
public class SecurityProxyIncomeJournalDAO extends SecurityProxy implements IncomeJournalDAO {

    private final IncomeJournalDAO incomeJournalDAO;

    public SecurityProxyIncomeJournalDAO(IncomeJournalDAO incomeJournalDAO) {
        this.incomeJournalDAO = incomeJournalDAO;
    }

    @Override
    public boolean createRecord(Record record) throws DAOException {
        checkAccessForModification(getUserDetails());
        return incomeJournalDAO.createRecord(record);
    }

    @Override
    public Journal readAll() throws DAOException {
        return incomeJournalDAO.readAll();
    }

    @Override
    public boolean updateRecord(int id, Record record) throws DAOException {
        checkAccessForModification(getUserDetails());
        return incomeJournalDAO.updateRecord(id,record);
    }

    @Override
    public boolean deleteRecord(int id) throws DAOException {
        checkAccessForModification(getUserDetails());
        return incomeJournalDAO.deleteRecord(id);
    }

    @Override
    public Record read(int id) throws DAOException {
        return incomeJournalDAO.read(id);
    }
}
