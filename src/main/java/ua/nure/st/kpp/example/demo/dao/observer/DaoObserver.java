package ua.nure.st.kpp.example.demo.dao.observer;

import ua.nure.st.kpp.example.demo.dao.observable.DaoEvent;

import java.util.List;

/**
 * @author Stanislav Hlova
 */
public abstract class DaoObserver<E extends DaoEvent> {
	private final List<DaoEventType> daoEventTypeList;

	DaoObserver(List<DaoEventType> daoEventTypeList) {
		this.daoEventTypeList = daoEventTypeList;
	}

	public abstract void notify(E daoEvent);

	public boolean containsAtLeastOneType(DaoEventType daoEventType) {
		return this.daoEventTypeList.contains(daoEventType);
	}
}
