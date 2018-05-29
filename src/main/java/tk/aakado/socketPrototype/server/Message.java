package tk.aakado.socketPrototype.server;

import com.google.gson.JsonObject;

public class Message {
    private ServerConnector server;
    private Connection sender;
    private JsonObject params;

    public Message(ServerConnector server, Connection sender, JsonObject params) {
        this.server = server;
        this.sender = sender;
        this.params = params;
    }

    public ServerConnector getServer() {
        return server;
    }

    public Connection getSender() {
        return sender;
    }

    public JsonObject getParams() {
        return params;
    }
}
