package ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.CompanyDaoEvent;
import ua.nure.st.kpp.example.demo.dao.observer.CompanyDaoObserver;
import ua.nure.st.kpp.example.demo.dao.observer.DaoEventType;
import ua.nure.st.kpp.example.demo.entity.Company;

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
public class CompanyLoggerDaoObserver extends CompanyDaoObserver {
    private static final Logger LOGGER = Logger.getLogger(CompanyLoggerDaoObserver.class.getName());
    private final Map<DaoEventType, Function<CompanyDaoEvent, String>> messageMap;

    public CompanyLoggerDaoObserver(List<DaoEventType> daoEventTypeList) {
        super(daoEventTypeList);
        this.messageMap = new EnumMap<>(DaoEventType.class);
        initMap();
    }

    public CompanyLoggerDaoObserver() {
        super(List.of(DaoEventType.UPDATE, DaoEventType.CREATE));
        this.messageMap = new EnumMap<>(DaoEventType.class);
        initMap();
    }

    private void initMap() {
        this.messageMap.put(DaoEventType.UPDATE, companyDaoEvent -> createLogMessage(companyDaoEvent, "була оновлена", "Дані оновленої сутності", "були оновлені", "Дані оновлених сутностей"));
        this.messageMap.put(DaoEventType.CREATE, companyDaoEvent -> createLogMessage(companyDaoEvent, "була створена", "Дані нової сутності", "були створені", "Дані нових сутностей"));
    }

    private static String createLogMessage(CompanyDaoEvent companyDaoEvent, String singleMessage, String singleDetailsMessage,
                                           String pluralMessage, String pluralDetailsMessage) {
        List<Company> companyList = companyDaoEvent.getCompanyList();
        if (companyList.size() == 1) {
            return String.format("Сутність Company з id = %d %s. %s: %s", companyList.get(0).getId(), singleMessage, singleDetailsMessage, companyList.get(0).toString());
        }
        StringBuilder builder = new StringBuilder(new String("Сутності Company з id = {"));
        Iterator<Company> iterator = companyList.iterator();
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
        iterator = companyList.iterator();
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
    public void notify(CompanyDaoEvent daoEvent) {
        LOGGER.log(Level.INFO, getMessage(daoEvent));
    }

    protected String getMessage(CompanyDaoEvent daoEvent) {
        Function<CompanyDaoEvent, String> function = this.messageMap.get(daoEvent.getDaoEventType());
        if (function == null) {
            throw new UnsupportedOperationException("Can't handle event with type: " + daoEvent.getDaoEventType());
        }
        return function.apply(daoEvent);
    }
}
