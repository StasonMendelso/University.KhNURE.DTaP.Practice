package ua.nure.st.kpp.example.demo.dao.implementation.proxy;

import ua.nure.st.kpp.example.demo.dao.DAOException;
import ua.nure.st.kpp.example.demo.dao.OutcomeJournalDAO;
import ua.nure.st.kpp.example.demo.entity.Journal;
import ua.nure.st.kpp.example.demo.entity.Record;

/**
 * @author Stanislav Hlova
 */
public class SecurityProxyOutcomeJournalDAO extends SecurityProxy implements OutcomeJournalDAO {

    private final OutcomeJournalDAO outcomeJournalDAO;

    public SecurityProxyOutcomeJournalDAO(OutcomeJournalDAO outcomeJournalDAO) {
        this.outcomeJournalDAO = outcomeJournalDAO;
    }

    @Override
    public boolean createRecord(Record record) throws DAOException {
        checkAccessForModification(getUserDetails());
        return outcomeJournalDAO.createRecord(record);
    }

    @Override
    public Journal readAll() throws DAOException {
        return outcomeJournalDAO.readAll();
    }

    @Override
    public boolean updateRecord(int id, Record record) throws DAOException {
        checkAccessForModification(getUserDetails());
        return outcomeJournalDAO.updateRecord(id,record);
    }

    @Override
    public boolean deleteRecord(int id) throws DAOException {
        checkAccessForModification(getUserDetails());
        return outcomeJournalDAO.deleteRecord(id);
    }

    @Override
    public Record read(int id) throws DAOException {
        return outcomeJournalDAO.read(id);
    }

}
