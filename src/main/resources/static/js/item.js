const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/warehouse-socket-endpoint' // websocket Stomp endpoint
});

stompClient.onConnect = (frame) => {
    console.log('Connected: ' + frame);
    //subscribe on Message Queue on destination '/topic/items'
    stompClient.subscribe('/topic/items', (itemMessage) => {
        console.log(JSON.parse(itemMessage.body));
        handleResponse(itemMessage);
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

function connect() {
    stompClient.activate();
}

const map = new Map();
map.set('DELETE', (jsonItemMessage) => {
    idArrayToDelete = jsonItemMessage.details.id;
    idArrayToDelete.forEach(id => {
        $(`#items-table-body tr[item-id='${id}']`).remove();
    });
});

function setItemRowValues(element, item) {
    element.find('td:eq(1)').text(item.vendor);
    element.find('td:eq(2)').text(item.name);
    element.find('td:eq(3)').text(item.unit);
    element.find('td:eq(4)').text(item.weight);
    element.find('td:eq(5)').text(item.amount);
    element.find('td:eq(6)').text(item.reserveRate);
}

map.set('UPDATE', (jsonItemMessage) => {
    itemsToUpdate = jsonItemMessage.details.items;
    itemsToUpdate.forEach(item => {
        let element = $(`#items-table-body tr[item-id='${item.id}']`);
        setItemRowValues(element, item);
    });
});
map.set('CREATE', (jsonItemMessage) => {
    itemsToCreate = jsonItemMessage.details.items;
    itemsToCreate.forEach(item => {
        let table = $(`#items-table-body`);
        let row = "<tr item-id=\"" + item.id + "\">" +
            "<td scope=\"row\">"+ item.id +"</td>" +
            "<td></td>" +
            "<td></td>" +
            "<td></td>" +
            "<td></td>" +
            "<td></td>" +
            "<td></td>" +
            "<td class=\"text-center\"><a href=\"/items/" + item.id + "/edit\" class=\"btn btn-primary\">Edit</a></td>" +
            "<td class=\"text-center\">" +
            "<form action=\"/items/" + item.id + "\" method=\"post\"><input type=\"hidden\" name=\"_method\" value=\"DELETE\">" +
            "<input type=\"submit\" class=\"btn btn-danger container\" value=\"Remove\">" +
            "</form>" +
            "</td>" +
            "</tr>";
        table.append(row);
        setItemRowValues(table.children('tr:last-child'), item);
    });
});

function handleResponse(itemMessage) {
    const jsonItemMessage = JSON.parse(itemMessage.body);
    console.log(jsonItemMessage.event_type);
    const handler = map.get(jsonItemMessage.event_type);
    if (handler == null) {
        console.log("Unsupported to handle response from server: " + jsonItemMessage);
        return;
    }

    handler(jsonItemMessage);
}


connect();