package ua.nure.st.kpp.example.demo.configuration;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author Stanislav Hlova
 */

@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            Class.forName("ua.nure.st.kpp.example.demo.memento.Memento");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
