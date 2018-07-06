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
        for (ClientHandler roommate : this) {
            roommate.sendMessage(message);
        }
    }

    public void sendPrivateMessage(ResponseToClient message, String receiver) {
        for (ClientHandler roommate : this) {
            if (roommate.getNickname().equals(receiver)) {
                roommate.sendMessage(message);
            }
        }
    }

}
