package ua.nure.st.kpp.example.demo.dao.observable;

import ua.nure.st.kpp.example.demo.dao.observer.DaoEventType;

/**
 * @author Stanislav Hlova
 */
public abstract class DaoEvent {
    private DaoEventType daoEventType;

    public DaoEvent(DaoEventType daoEventType) {
        this.daoEventType = daoEventType;
    }

    public DaoEventType getDaoEventType() {
        return daoEventType;
    }

    public void setDaoEventType(DaoEventType daoEventType) {
        this.daoEventType = daoEventType;
    }
}
