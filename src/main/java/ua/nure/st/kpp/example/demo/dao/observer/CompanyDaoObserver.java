package ua.nure.st.kpp.example.demo.dao.observer;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.CompanyDaoEvent;

import java.util.List;

/**
 * @author Stanislav Hlova
 */
public abstract class CompanyDaoObserver extends DaoObserver<CompanyDaoEvent>{
	public CompanyDaoObserver(List<DaoEventType> daoEventTypeList) {
		super(daoEventTypeList);
	}
}
