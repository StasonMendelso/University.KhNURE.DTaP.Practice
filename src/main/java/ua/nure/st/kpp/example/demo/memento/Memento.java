package ua.nure.st.kpp.example.demo.memento;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Stanislav Hlova
 */
public abstract class Memento<T> {
    protected final List<T> states;
    protected int index;
    protected static final int MAX_HISTORY;

    static {
        try {
            Properties properties = new Properties();
            properties.load(new ClassPathResource("application.properties").getInputStream());
            MAX_HISTORY = Integer.parseInt((String) properties.get("memento.max-history"));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    protected Memento() {
        this.states = new ArrayList<>();
        this.index = -1;
    }
    protected Memento(List<T> states) {
        this.states = states;
        this.index = states.size() - 1;
    }
    public MementoMetadata getMetadata() {
        return new BasicMementoMetadata(index, states.size());
    }
    public void saveState(T state) {
        if (index + 1 == MAX_HISTORY + 1) {
            deleteOldState();
        }

        if (index != states.size() - 1) {
            int count = states.size() - 1;
            while (count > index) {
                states.remove(index + 1);
                count--;
            }
        }
        save(state);
    }
    protected void deleteOldState() {
        for (int i = 0; i < states.size() - 1; i++) {
            states.set(i, states.get(i + 1));
        }
        states.remove(states.size() - 1);
        if (index != 0) {
            index--;
        }
    }
    protected void save(T state) {
        states.add(state);
        index = states.size() - 1;
    }
    public T previous() {
        if (states.isEmpty()) {
            return null;
        }
        if (index <= 0) {
            return states.get(0);
        }

        return states.get(--index);
    }
    public T next() {
        if (states.isEmpty()) {
            return null;
        }
        return states.get(++index);
    }
    public T current() {
        return states.get(index);
    }
    public int getStateSize() {
        return states.size();
    }
    public void clear() {
        index = -1;
        states.clear();
    }

}
