package ua.nure.st.kpp.example.demo.dao.observable;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.CompanyDaoEvent;
import ua.nure.st.kpp.example.demo.dao.observer.CompanyDaoObserver;

/**
 * @author Stanislav Hlova
 */
public interface CompanyDaoObservable{
    void notifyAll(CompanyDaoEvent companyDaoEvent);

    void subscribe(CompanyDaoObserver companyDaoObserver);

    void unsubscribe(CompanyDaoObserver companyDaoObserver);
}
