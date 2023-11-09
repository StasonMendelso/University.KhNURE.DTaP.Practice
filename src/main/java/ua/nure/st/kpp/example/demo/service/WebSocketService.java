package ua.nure.st.kpp.example.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import ua.nure.st.kpp.example.demo.websocket.message.ItemMessage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Stanislav Hlova
 */
@Service
public class WebSocketService {
    private static final Logger LOGGER = Logger.getLogger(WebSocketService.class.getName());

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebSocketService(SimpMessagingTemplate simpMessagingTemplate, ObjectMapper objectMapper) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(ItemMessage itemMessage) {
        try {
            LOGGER.log(Level.INFO,"Sending message: " + itemMessage);
            byte[] message = objectMapper.writeValueAsBytes(itemMessage);
            simpMessagingTemplate.send("/topic/items", new GenericMessage<>(message));
            LOGGER.log(Level.INFO,"Message was sent: " + itemMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
