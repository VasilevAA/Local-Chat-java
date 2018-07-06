package server.network.room;

import server.messages.ResponseToClient;
import server.network.handler.ClientHandler;

import java.util.ArrayList;

public class Room {
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    private String roomId;

    public ArrayList<ClientHandler> getClients() {
        return clients;
    }

    public Room(String id) {
        roomId = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void addRoommate(ClientHandler client) {
        clients.add(client);
    }

    public void removeRoommate(ClientHandler client) {
        clients.remove(client);
    }

    public int numberOfChatters() {
        return clients.size();
    }

    public void sendMessage(ResponseToClient message) {
        for (ClientHandler roommate : clients) {
            roommate.sendMessage(message);
        }
    }

    public void sendPrivateMessage(ResponseToClient message, String receiver) {
        for (ClientHandler roommate : clients) {
            if (roommate.getNickname().equals(receiver)) {
                roommate.sendMessage(message);
            }
        }
    }

    public boolean isEmpty(){
        return clients.isEmpty();
    }

}
