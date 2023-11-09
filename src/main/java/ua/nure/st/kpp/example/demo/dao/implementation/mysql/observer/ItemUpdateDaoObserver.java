package ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer;

import ua.nure.st.kpp.example.demo.dao.implementation.mysql.observer.event.ItemDaoEvent;
import ua.nure.st.kpp.example.demo.dao.observer.DaoEventType;
import ua.nure.st.kpp.example.demo.dao.observer.ItemDaoObserver;
import ua.nure.st.kpp.example.demo.service.WebSocketService;
import ua.nure.st.kpp.example.demo.websocket.message.ItemMessage;
import ua.nure.st.kpp.example.demo.websocket.message.ItemMessageDetails;
import ua.nure.st.kpp.example.demo.websocket.message.UpdateItemMessageDetails;

import java.util.Collections;
import java.util.List;

/**
 * @author Stanislav Hlova
 */
public class ItemUpdateDaoObserver extends ItemDaoObserver {
	private final WebSocketService webSocketService;

	//constructor for marking this observer triggered on passed dao event type
	public ItemUpdateDaoObserver(List<DaoEventType> daoEventTypeList,WebSocketService webSocketService) {
		super(daoEventTypeList);
		this.webSocketService = webSocketService;
	}

	//default constructor for marked this observer triggered only on UPDATE event
	public ItemUpdateDaoObserver(WebSocketService webSocketService) {
		super(Collections.singletonList(DaoEventType.UPDATE));
		this.webSocketService = webSocketService;
	}
	@Override
	public void notify(ItemDaoEvent daoEvent) {
		ItemMessageDetails itemMessageDetails = new UpdateItemMessageDetails(daoEvent.getItemList());
		webSocketService.sendMessage(new ItemMessage(ItemMessage.EventType.UPDATE, itemMessageDetails));
	}


}
