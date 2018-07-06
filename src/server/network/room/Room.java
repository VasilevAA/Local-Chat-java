package server.network.room;

import server.messages.ResponseToClient;
import server.network.handler.ClientHandler;

import java.util.HashSet;

public class Room extends HashSet<ClientHandler> {

    private String roomId;

    public Room(String id) {
        roomId = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void sendMessage(ResponseToClient message) {
        forEach(clientHandler -> clientHandler.sendMessage(message));
    }

    public void sendPrivateMessage(ResponseToClient message, String receiver) {
        forEach(clientHandler -> {
            if (clientHandler.getNickname().equals(receiver)) {
                clientHandler.sendMessage(message);
            }
        });
    }

    @Override
    public boolean equals(Object o) {
            return o == this;
    }
}
