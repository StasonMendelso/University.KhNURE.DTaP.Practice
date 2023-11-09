package ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.RecordDaoEvent;
import ua.nure.st.kpp.example.demo.dao.observer.DaoEventType;
import ua.nure.st.kpp.example.demo.dao.observer.IncomeRecordDaoObserver;
import ua.nure.st.kpp.example.demo.entity.Record;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Stanislav Hlova
 */
public class IncomeRecordLoggerDaoObserver extends IncomeRecordDaoObserver {
    private static final Logger LOGGER = Logger.getLogger(IncomeRecordLoggerDaoObserver.class.getName());
    private final Map<DaoEventType, Function<RecordDaoEvent, String>> messageMap;

    public IncomeRecordLoggerDaoObserver(List<DaoEventType> daoEventTypeList) {
        super(daoEventTypeList);
        this.messageMap = new EnumMap<>(DaoEventType.class);
        initMap();
    }

    public IncomeRecordLoggerDaoObserver() {
        super(List.of(DaoEventType.UPDATE, DaoEventType.CREATE, DaoEventType.DELETE));
        this.messageMap = new EnumMap<>(DaoEventType.class);
        initMap();
    }

    private void initMap() {
        this.messageMap.put(DaoEventType.UPDATE, recordDaoEvent -> createLogMessage(recordDaoEvent, "була оновлена", "Дані оновленої сутності", "були оновлені", "Дані оновлених сутностей"));
        this.messageMap.put(DaoEventType.CREATE, recordDaoEvent -> createLogMessage(recordDaoEvent, "була створена", "Дані нової сутності", "були створені", "Дані нових сутностей"));
        this.messageMap.put(DaoEventType.DELETE, recordDaoEvent -> createLogMessage(recordDaoEvent, "була видалена", "Дані видаленої сутності", "були видалені", "Дані видалених сутностей"));

    }

    private static String createLogMessage(RecordDaoEvent recordDaoEvent, String singleMessage, String singleDetailsMessage,
                                           String pluralMessage, String pluralDetailsMessage) {
        List<Record> recordList = recordDaoEvent.getRecordList();
        if (recordList.size() == 1) {
            return String.format("Сутність Record(IncomeJournal) з id = %d %s. %s: %s", recordList.get(0).getId(), singleMessage, singleDetailsMessage, recordList.get(0).toString());
        }
        StringBuilder builder = new StringBuilder(new String("Сутності Record(IncomeJournal) "));
        Iterator<Record> iterator = recordList.iterator();

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
        iterator = recordList.iterator();
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
    public void notify(RecordDaoEvent daoEvent) {
        LOGGER.log(Level.INFO, getMessage(daoEvent));
    }

    protected String getMessage(RecordDaoEvent daoEvent) {
        Function<RecordDaoEvent, String> function = this.messageMap.get(daoEvent.getDaoEventType());
        if (function == null) {
            throw new UnsupportedOperationException("Can't handle event with type: " + daoEvent.getDaoEventType());
        }
        return function.apply(daoEvent);
    }
}
