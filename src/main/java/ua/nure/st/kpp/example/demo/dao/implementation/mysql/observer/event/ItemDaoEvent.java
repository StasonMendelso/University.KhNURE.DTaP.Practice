package ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event;

import ua.nure.st.kpp.example.demo.dao.observable.DaoEvent;
import ua.nure.st.kpp.example.demo.dao.observer.DaoEventType;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.util.Arrays;
import java.util.List;

/**
 * @author Stanislav Hlova
 */
public class ItemDaoEvent extends DaoEvent {
    private List<Item> itemList;


    public ItemDaoEvent(DaoEventType daoEventType, Item... items) {
        super(daoEventType);
        this.itemList = Arrays.asList(items);
    }

    public ItemDaoEvent(DaoEventType daoEventType, List<Item> itemIdList) {
        super(daoEventType);
        this.itemList = itemIdList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
