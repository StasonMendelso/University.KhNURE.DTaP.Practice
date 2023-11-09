package ua.nure.st.kpp.example.demo.dao.observable;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.RecordDaoEvent;
import ua.nure.st.kpp.example.demo.dao.observer.OutcomeRecordDaoObserver;

/**
 * @author Stanislav Hlova
 */
public interface OutcomeRecordDaoObservable {

    void notifyAll(RecordDaoEvent recordDaoEvent);

    void subscribe(OutcomeRecordDaoObserver incomeRecordDaoObserver);

    void unsubscribe(OutcomeRecordDaoObserver incomeRecordDaoObserver);
}
