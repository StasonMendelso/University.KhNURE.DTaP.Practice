package ua.nure.st.kpp.example.demo.dao.observer;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.ItemDaoEvent;

import java.util.List;

/**
 * @author Stanislav Hlova
 */
public abstract class ItemDaoObserver extends DaoObserver<ItemDaoEvent>{
	public ItemDaoObserver(List<DaoEventType> daoEventTypeList) {
		super(daoEventTypeList);
	}
}
