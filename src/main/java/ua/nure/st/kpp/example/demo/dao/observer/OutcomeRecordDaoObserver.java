package ua.nure.st.kpp.example.demo.dao.observer;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.RecordDaoEvent;

import java.util.List;

/**
 * @author Stanislav Hlova
 */
public abstract class OutcomeRecordDaoObserver extends DaoObserver<RecordDaoEvent>{
	public OutcomeRecordDaoObserver(List<DaoEventType> daoEventTypeList) {
		super(daoEventTypeList);
	}
}
