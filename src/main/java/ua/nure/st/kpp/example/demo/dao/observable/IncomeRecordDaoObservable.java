package ua.nure.st.kpp.example.demo.dao.observable;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.RecordDaoEvent;
import ua.nure.st.kpp.example.demo.dao.observer.IncomeRecordDaoObserver;

/**
 * @author Stanislav Hlova
 */
public interface IncomeRecordDaoObservable{

    void notifyAll(RecordDaoEvent recordDaoEvent);

    void subscribe(IncomeRecordDaoObserver incomeRecordDaoObserver);

    void unsubscribe(IncomeRecordDaoObserver incomeRecordDaoObserver);
}
