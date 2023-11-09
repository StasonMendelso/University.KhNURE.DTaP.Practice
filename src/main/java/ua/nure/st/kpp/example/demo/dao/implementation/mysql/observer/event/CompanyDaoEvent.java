package ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event;

import ua.nure.st.kpp.example.demo.dao.observable.DaoEvent;
import ua.nure.st.kpp.example.demo.dao.observer.DaoEventType;
import ua.nure.st.kpp.example.demo.entity.Company;

import java.util.Arrays;
import java.util.List;

/**
 * @author Stanislav Hlova
 */
public class CompanyDaoEvent extends DaoEvent {
    private List<Company> companyList;

    public CompanyDaoEvent(DaoEventType daoEventType, Company... companies) {
        super(daoEventType);
        this.companyList = Arrays.asList(companies);
    }

    public CompanyDaoEvent(DaoEventType daoEventType, List<Company> companyList) {
        super(daoEventType);
        this.companyList = companyList;
    }

    public List<Company> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }
}
