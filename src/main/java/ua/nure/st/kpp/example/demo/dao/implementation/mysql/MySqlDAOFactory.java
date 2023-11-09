package ua.nure.st.kpp.example.demo.dao.implementation.mysql;

import ua.nure.st.kpp.example.demo.dao.*;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.*;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.ConnectionPool;
import ua.nure.st.kpp.example.demo.dao.implementation.mysql.util.MySqlConnectionUtils;
import ua.nure.st.kpp.example.demo.service.WebSocketService;

public class MySqlDAOFactory implements Factory {
    private final MySqlConnectionUtils mySqlConnectionUtils;
    private final MySqlItemDAO mySqlItemDAO;
    private final MySqlCompanyDAO mySqlCompanyDAO;
    private final MySqlIncomeJournalDAO mySqlIncomeJournalDAO;
    private final MySqlOutcomeJournalDAO mySqlOutcomeJournalDAO;



    public MySqlDAOFactory(ConnectionPool connectionPool, WebSocketService webSocketService) {
        this.mySqlConnectionUtils = new MySqlConnectionUtils(connectionPool);

        ItemLoggerDaoObserver itemLoggerDaoObserver = new ItemLoggerDaoObserver();
        ItemUpdateDaoObserver itemUpdateDaoObserver = new ItemUpdateDaoObserver(webSocketService);
        ItemDeleteDaoObserver itemDeleteDaoObserver = new ItemDeleteDaoObserver(webSocketService);
        ItemCreateDaoObserver itemCreateDaoObserver = new ItemCreateDaoObserver(webSocketService);

        this.mySqlItemDAO = new MySqlItemDAO(mySqlConnectionUtils);
        this.mySqlItemDAO.subscribe(itemLoggerDaoObserver);
        this.mySqlItemDAO.subscribe(itemUpdateDaoObserver);
        this.mySqlItemDAO.subscribe(itemCreateDaoObserver);
        this.mySqlItemDAO.subscribe(itemDeleteDaoObserver);

        this.mySqlCompanyDAO = new MySqlCompanyDAO(mySqlConnectionUtils);
        this.mySqlCompanyDAO.subscribe(new CompanyLoggerDaoObserver());

        this.mySqlIncomeJournalDAO = new MySqlIncomeJournalDAO(mySqlConnectionUtils, mySqlItemDAO);
        this.mySqlIncomeJournalDAO.subscribe(new IncomeRecordLoggerDaoObserver());
        this.mySqlIncomeJournalDAO.subscribe(itemLoggerDaoObserver);
        this.mySqlIncomeJournalDAO.subscribe(itemUpdateDaoObserver);
        this.mySqlIncomeJournalDAO.subscribe(itemCreateDaoObserver);
        this.mySqlIncomeJournalDAO.subscribe(itemDeleteDaoObserver);

        this.mySqlOutcomeJournalDAO = new MySqlOutcomeJournalDAO(mySqlConnectionUtils, mySqlItemDAO);
        this.mySqlOutcomeJournalDAO.subscribe(new OutcomeRecordLoggerDaoObserver());
        this.mySqlOutcomeJournalDAO.subscribe(itemLoggerDaoObserver);
        this.mySqlOutcomeJournalDAO.subscribe(itemUpdateDaoObserver);
        this.mySqlOutcomeJournalDAO.subscribe(itemCreateDaoObserver);
        this.mySqlOutcomeJournalDAO.subscribe(itemDeleteDaoObserver);

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
