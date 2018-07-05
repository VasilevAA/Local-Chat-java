package server.network;


import server.messages.MessageToClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {

    private static final int PORT = 3443;

    private ArrayList<ClientHandler> clients = new ArrayList<>();

    ArrayList<ClientHandler> getClients() {
        return clients;
    }

    public void start() {
        Socket clientSocket = null;

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);

            System.out.println("Сервер запущен!");

            while (true) {
                clientSocket = serverSocket.accept();

                ClientHandler client = new ClientHandler(clientSocket, this);

                clients.add(client);

                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert clientSocket != null;
                clientSocket.close();
                System.out.println("Сервер остановлен");
                serverSocket.close();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    void sendMessageToRoommates(MessageToClient message, String roomId) {
        for (ClientHandler client : clients) {
            if (client.getRoomId().equals(roomId)) {
                client.sendMessage(message);
            }
        }
    }
    void sendPrivateMessage(MessageToClient message, String roomId, String receiverName){
        for (ClientHandler client : clients) {
            if(client.getRoomId().equals(roomId) && client.getNickname().equals(receiverName)){
                client.sendMessage(message);
            }
        }
    }

}

class Room{
    private ArrayList<ClientHandler> roommates = new ArrayList<>();

    private String roomId;


    public ArrayList<ClientHandler> getRoommates() {
        return roommates;
    }

    Room(String id){
        roomId = id;
    }

    public String getRoomId() {
        return roomId;
    }

    boolean isEmty(){
        return roommates.isEmpty();
    }

    public void addRoommate(ClientHandler client){
        roommates.add(client);
    }

    public  int numberOfChatters(){
        return roommates.size();
    }

    public void sendMessage(MessageToClient message){
        for (ClientHandler roommate : roommates) {
            roommate.sendMessage(message);
        }
    }

    public void sendPrivateMessage(MessageToClient message, String receiver){
        for (ClientHandler roommate : roommates) {
            if(roommate.getNickname().equals(receiver)){
                roommate.sendMessage(message);
            }
        }
    }

}
