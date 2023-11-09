package ua.nure.st.kpp.example.demo.dao.observer;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.RecordDaoEvent;

import java.util.List;

/**
 * @author Stanislav Hlova
 */
public abstract class IncomeRecordDaoObserver extends DaoObserver<RecordDaoEvent>{
	public IncomeRecordDaoObserver(List<DaoEventType> daoEventTypeList) {
		super(daoEventTypeList);
	}
}
