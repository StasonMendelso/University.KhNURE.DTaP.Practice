package ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.ItemDaoEvent;
import ua.nure.st.kpp.example.demo.dao.observer.DaoEventType;
import ua.nure.st.kpp.example.demo.dao.observer.ItemDaoObserver;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Stanislav Hlova
 */
public class ItemLoggerDaoObserver extends ItemDaoObserver {
    private static final Logger LOGGER = Logger.getLogger(ItemLoggerDaoObserver.class.getName());
    private final Map<DaoEventType, Function<ItemDaoEvent, String>> messageMap;

    public ItemLoggerDaoObserver(List<DaoEventType> daoEventTypeList) {
        super(daoEventTypeList);
        this.messageMap = new EnumMap<>(DaoEventType.class);
        initMap();
    }

    public ItemLoggerDaoObserver() {
        super(List.of(DaoEventType.UPDATE, DaoEventType.CREATE, DaoEventType.DELETE));
        this.messageMap = new EnumMap<>(DaoEventType.class);
        initMap();
    }

    private void initMap() {
        this.messageMap.put(DaoEventType.UPDATE, itemDaoEvent -> createLogMessage(itemDaoEvent, "була оновлена", "Дані оновленої сутності", "були оновлені", "Дані оновлених сутностей"));
        this.messageMap.put(DaoEventType.CREATE, itemDaoEvent -> createLogMessage(itemDaoEvent, "була створена", "Дані нової сутності", "були створені", "Дані нових сутностей"));
        this.messageMap.put(DaoEventType.DELETE, itemDaoEvent -> createLogMessage(itemDaoEvent, "була видалена(схована)", "Дані видаленої сутності", "були видалені(сховані)", "Дані видалених сутностей"));

    }

    private static String createLogMessage(ItemDaoEvent itemDaoEvent, String singleMessage, String singleDetailsMessage,
                                           String pluralMessage, String pluralDetailsMessage) {
        List<Item> itemList = itemDaoEvent.getItemList();
        if (itemList.size() == 1) {
            return String.format("Сутність Item з id = %d %s. %s: %s", itemList.get(0).getId(), singleMessage, singleDetailsMessage, itemList.get(0).toString());
        }
        StringBuilder builder = new StringBuilder(new String("Сутності Item з id = {"));
        Iterator<Item> iterator = itemList.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next().getId());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append("} ")
                .append(pluralMessage)
                .append(". ")
                .append(pluralDetailsMessage)
                .append("{ ");
        iterator = itemList.iterator();
        while (iterator.hasNext()) {
            builder.append(iterator.next());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public void notify(ItemDaoEvent daoEvent) {
        LOGGER.log(Level.INFO, getMessage(daoEvent));
    }

    protected String getMessage(ItemDaoEvent daoEvent) {
        Function<ItemDaoEvent, String> function = this.messageMap.get(daoEvent.getDaoEventType());
        if (function == null) {
            throw new UnsupportedOperationException("Can't handle event with type: " + daoEvent.getDaoEventType());
        }
        return function.apply(daoEvent);
    }
}
