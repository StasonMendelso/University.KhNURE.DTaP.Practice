package ua.nure.st.kpp.example.demo.dao.observable;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.ItemDaoEvent;
import ua.nure.st.kpp.example.demo.dao.observer.ItemDaoObserver;

/**
 * @author Stanislav Hlova
 */
public interface ItemDaoObservable{
    void notifyAll(ItemDaoEvent itemDaoEvent);

    void subscribe(ItemDaoObserver itemDaoObserver);

    void unsubscribe(ItemDaoObserver itemDaoObserver);
}
