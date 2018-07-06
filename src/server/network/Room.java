package server.network;

import server.messages.MessageToClient;

import java.util.ArrayList;

class Room {
    private ArrayList<ClientHandler> clients = new ArrayList<>();

    private String roomId;

    ArrayList<ClientHandler> getClients() {
        return clients;
    }

    Room(String id) {
        roomId = id;
    }

    String getRoomId() {
        return roomId;
    }

    void addRoommate(ClientHandler client) {
        clients.add(client);
    }

    void removeRoommate(ClientHandler client) {
        clients.remove(client);
    }

    int numberOfChatters() {
        return clients.size();
    }

    void sendMessage(MessageToClient message) {
        for (ClientHandler roommate : clients) {
            roommate.sendMessage(message);
        }
    }

    void sendPrivateMessage(MessageToClient message, String receiver) {
        for (ClientHandler roommate : clients) {
            if (roommate.getNickname().equals(receiver)) {
                roommate.sendMessage(message);
            }
        }
    }

    boolean isEmpty(){
        return clients.isEmpty();
    }

}
