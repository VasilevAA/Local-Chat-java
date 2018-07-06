package server.network.server;


import server.messages.ResponseToClient;
import server.network.handler.ClientHandler;
import server.network.room.Room;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Iterator;

public class ChatServer {

    private static final int PORT = 3443;

    private HashSet<Room> roomPull = new HashSet<>();

    private static final Room systemRoom = new Room("$$system##room&&"); //temporary room (to rid of null room)

    public void start() {
        Socket clientSocket = null;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is on!");

            while (true) {

                clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket, this);
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                System.out.println("Server is off!");

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    void removeEmptyRooms() { // TODO: 06.07.2018 insert this somewhere
        Iterator<Room> it = roomPull.iterator();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.isEmpty()) {
                roomPull.remove(r);
            }
        }
    }

    public Room getRoomById(String id) {
        for (Room room : roomPull) {
            if (room.getRoomId().equals(id)) {
                return room;
            }
        }
        return systemRoom;
    }

    public void sendMessageToRoom(ResponseToClient message, String roomId) {
        getRoomById(roomId).sendMessage(message);
    }

    public void sendPrivateMessage(ResponseToClient message, String roomId, String receiverName) {
        getRoomById(roomId).sendPrivateMessage(message, receiverName);
    }


    public void addNewClient(ClientHandler client) {
        Room room = getRoomById(client.getRoomId());

        if (room == systemRoom) {
            room = new Room(client.getRoomId());
            roomPull.add(room);
        }
        room.add(client);
    }

    public void removeClient(ClientHandler client) {
        getRoomById(client.getRoomId()).remove(client);
    }

    public void sendGlobalMessage(){
        StringBuilder str = new StringBuilder("Room pull:\n");
        for (Room room : roomPull) {
            str.append(room.getRoomId()).append('\n');
        }
        for (Room room : roomPull) {

            room.sendMessage(ResponseToClient.NOTE(str.toString()));
        }

    }
}

