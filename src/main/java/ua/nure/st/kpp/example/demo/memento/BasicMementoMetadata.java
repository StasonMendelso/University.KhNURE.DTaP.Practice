package ua.nure.st.kpp.example.demo.memento;

/**
 * @author Stanislav Hlova
 */
public class BasicMementoMetadata extends MementoMetadata{
    private int currentIndex;
    private int statesSize;

    public BasicMementoMetadata(int currentIndex, int statesSize) {
        this.currentIndex = currentIndex;
        this.statesSize = statesSize;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public int getStatesSize() {
        return statesSize;
    }

    public void setStatesSize(int statesSize) {
        this.statesSize = statesSize;
    }
}
