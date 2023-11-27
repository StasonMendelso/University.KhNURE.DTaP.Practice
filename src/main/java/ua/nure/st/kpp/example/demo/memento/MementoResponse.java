package ua.nure.st.kpp.example.demo.memento;

/**
 * @author Stanislav Hlova
 */
public class MementoResponse<T> {
    private MementoMetadata metadata;
    private T mementoState;

    public MementoResponse(MementoMetadata metadata, T mementoState) {
        this.metadata = metadata;
        this.mementoState = mementoState;
    }

    public MementoMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(MementoMetadata metadata) {
        this.metadata = metadata;
    }

    public T getMementoState() {
        return mementoState;
    }

    public void setMementoState(T mementoState) {
        this.mementoState = mementoState;
    }
}
