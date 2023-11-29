package ua.nure.st.kpp.example.demo.dao.implementation.proxy;

import ua.nure.st.kpp.example.demo.dao.CompanyDAO;
import ua.nure.st.kpp.example.demo.dao.Factory;
import ua.nure.st.kpp.example.demo.dao.IncomeJournalDAO;
import ua.nure.st.kpp.example.demo.dao.ItemDAO;
import ua.nure.st.kpp.example.demo.dao.OutcomeJournalDAO;
import ua.nure.st.kpp.example.demo.dao.UserDAO;

/**
 * @author Stanislav Hlova
 */
public class SecurityProxyDAOFactory implements Factory {

    private final SecurityProxyItemDAO securityProxyItemDAO;
    private final SecurityProxyCompanyDAO securityProxyCompanyDAO;
    private final SecurityProxyIncomeJournalDAO securityProxyIncomeJournalDAO;
    private final SecurityProxyOutcomeJournalDAO securityProxyOutcomeJournalDAO;
    private final UserDAO userDAO;

    public SecurityProxyDAOFactory(Factory factory) {
        securityProxyItemDAO = new SecurityProxyItemDAO(factory.createItemDAO());
        securityProxyCompanyDAO = new SecurityProxyCompanyDAO(factory.createCompanyDAO());
        securityProxyIncomeJournalDAO = new SecurityProxyIncomeJournalDAO(factory.createIncomeJournalDAO());
        securityProxyOutcomeJournalDAO = new SecurityProxyOutcomeJournalDAO(factory.createOutcomeJournalDAO());
        userDAO = factory.createUserDAO();
    }

    @Override
    public ItemDAO createItemDAO() {
        return securityProxyItemDAO;
    }

    @Override
    public CompanyDAO createCompanyDAO() {
        return securityProxyCompanyDAO;
    }

    @Override
    public IncomeJournalDAO createIncomeJournalDAO() {
        return securityProxyIncomeJournalDAO;
    }

    @Override
    public OutcomeJournalDAO createOutcomeJournalDAO() {
        return securityProxyOutcomeJournalDAO;
    }

    @Override
    public UserDAO createUserDAO() {
        return userDAO;
    }
}
