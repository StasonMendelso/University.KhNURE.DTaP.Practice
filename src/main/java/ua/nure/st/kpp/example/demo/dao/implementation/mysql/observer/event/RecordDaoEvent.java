package ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event;

import ua.nure.st.kpp.example.demo.dao.observable.DaoEvent;
import ua.nure.st.kpp.example.demo.dao.observer.DaoEventType;
import ua.nure.st.kpp.example.demo.entity.Record;

import java.util.Arrays;
import java.util.List;

/**
 * @author Stanislav Hlova
 */
public class RecordDaoEvent extends DaoEvent {
    private List<Record> recordList;

    public RecordDaoEvent(DaoEventType daoEventType, Record... records) {
        super(daoEventType);
        this.recordList = Arrays.asList(records);
    }

    public RecordDaoEvent(DaoEventType daoEventType, List<Record> recordList) {
        super(daoEventType);
        this.recordList = recordList;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }
}
