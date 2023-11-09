package ua.nure.st.kpp.example.demo.websocket.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import ua.nure.st.kpp.example.demo.entity.Item;

import java.util.List;

/**
 * @author Stanislav Hlova
 */
public class UpdateItemMessageDetails extends ItemMessageDetails{
    @JsonProperty("items")

    private List<Item> itemList;

    public UpdateItemMessageDetails(List<Item> itemList) {
        this.itemList = itemList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return "UpdateItemMessageDetails{" +
                "itemList=" + itemList +
                '}';
    }
}
