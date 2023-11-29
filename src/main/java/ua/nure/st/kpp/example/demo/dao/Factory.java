package ua.nure.st.kpp.example.demo.dao;


public interface Factory {
    ItemDAO createItemDAO();
    CompanyDAO createCompanyDAO();
    IncomeJournalDAO createIncomeJournalDAO();
    OutcomeJournalDAO createOutcomeJournalDAO();

    UserDAO createUserDAO();
}
