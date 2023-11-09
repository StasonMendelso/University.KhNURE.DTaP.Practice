package ua.nure.st.kpp.example.demo.websocket.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Stanislav Hlova
 */
public class DeleteItemMessageDetails extends ItemMessageDetails{
    @JsonProperty("id")
    private List<Integer> idList;

    public DeleteItemMessageDetails(List<Integer> idList) {
        this.idList = idList;
    }

    public List<Integer> getIdList() {
        return idList;
    }

    public void setIdList(List<Integer> idList) {
        this.idList = idList;
    }

    @Override
    public String toString() {
        return "DeleteItemMessageDetails{" +
                "idList=" + idList +
                '}';
    }
}
