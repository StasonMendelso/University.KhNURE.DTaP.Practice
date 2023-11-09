package ua.nure.st.kpp.example.demo.websocket.message;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Stanislav Hlova
 */
public class ItemMessage {
    @JsonProperty("event_type")
    private EventType eventType;
    @JsonProperty("details")
    private ItemMessageDetails messageDetails;

    public enum EventType {
        CREATE, DELETE, UPDATE
    }

    public ItemMessage(EventType eventType, ItemMessageDetails messageDetails) {
        this.eventType = eventType;
        this.messageDetails = messageDetails;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public ItemMessageDetails getMessageDetails() {
        return messageDetails;
    }

    public void setMessageDetails(ItemMessageDetails messageDetails) {
        this.messageDetails = messageDetails;
    }

    @Override
    public String toString() {
        return "ItemMessage{" +
                "eventType=" + eventType +
                ", messageDetails=" + messageDetails +
                '}';
    }
}
