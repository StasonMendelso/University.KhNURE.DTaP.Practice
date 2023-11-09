package ua.nure.st.kpp.example.demo.dao.implementation.mysql;

import ua.nure.st.kpp.example.demo.dao.*;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.*;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.ConnectionPool;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.MySqlConnectionUtils;

public class MySqlDAOFactory implements Factory {
    private final MySqlConnectionUtils mySqlConnectionUtils;
    private final MySqlItemDAO mySqlItemDAO;
    private final MySqlCompanyDAO mySqlCompanyDAO;
    private final MySqlIncomeJournalDAO mySqlIncomeJournalDAO;
    private final MySqlOutcomeJournalDAO mySqlOutcomeJournalDAO;
    public MySqlDAOFactory(ConnectionPool connectionPool) {
        this.mySqlConnectionUtils = new MySqlConnectionUtils(connectionPool);

        ItemLoggerDaoObserver itemLoggerDaoObserver = new ItemLoggerDaoObserver();

        this.mySqlItemDAO = new MySqlItemDAO(mySqlConnectionUtils);
        this.mySqlItemDAO.subscribe(new ItemUpdateDaoObserver());
        this.mySqlItemDAO.subscribe(itemLoggerDaoObserver);

        this.mySqlCompanyDAO = new MySqlCompanyDAO(mySqlConnectionUtils);
        this.mySqlCompanyDAO.subscribe(new CompanyLoggerDaoObserver());

        this.mySqlIncomeJournalDAO = new MySqlIncomeJournalDAO(mySqlConnectionUtils, mySqlItemDAO);
        this.mySqlIncomeJournalDAO.subscribe(new IncomeRecordLoggerDaoObserver());
        this.mySqlIncomeJournalDAO.subscribe(itemLoggerDaoObserver);


        this.mySqlOutcomeJournalDAO = new MySqlOutcomeJournalDAO(mySqlConnectionUtils, mySqlItemDAO);
        this.mySqlOutcomeJournalDAO.subscribe(new OutcomeRecordLoggerDaoObserver());
        this.mySqlOutcomeJournalDAO.subscribe(itemLoggerDaoObserver);

    }

    @Override
    public ItemDAO createItemDAO() {
        return mySqlItemDAO;
    }

    @Override
    public CompanyDAO createCompanyDAO() {
        return mySqlCompanyDAO;
    }

    @Override
    public IncomeJournalDAO createIncomeJournalDAO() {
        return mySqlIncomeJournalDAO;
    }

    @Override
    public OutcomeJournalDAO createOutcomeJournalDAO() {
        return mySqlOutcomeJournalDAO;
    }

}
