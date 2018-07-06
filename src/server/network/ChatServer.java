package server.network;


import server.messages.MessageToClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

public class ChatServer {

    private static final int PORT = 3443;

    private ArrayList<Room> roomPull = new ArrayList<>();

    private static final Room systemRoom = new Room("$$system##room&&"); //temporary room (to rid of null room)

    public void start() {
        Socket clientSocket = null;

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(PORT);

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
                assert clientSocket != null;
                clientSocket.close();
                System.out.println("Server is off!");
                serverSocket.close();

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    void removeEmptyRooms(){ // TODO: 06.07.2018 insert this somewhere
        Iterator<Room> it= roomPull.iterator();
        while (it.hasNext()){
            Room r = it.next();
            if(r.isEmpty()){
                roomPull.remove(r);
            }
        }
    }

    Room getRoomById(String id) {
        for (Room room : roomPull) {
            if (room.getRoomId().equals(id)) {
                return room;
            }
        }
        return systemRoom;
    }

    void sendMessageToAllRoom(MessageToClient message, String roomId) {
        getRoomById(roomId).sendMessage(message);
    }

    void sendPrivateMessage(MessageToClient message, String roomId, String receiverName) {
        getRoomById(roomId).sendPrivateMessage(message,receiverName);
    }


    void addNewClient(ClientHandler client) {
        Room room = getRoomById(client.getRoomId());

        if (room == systemRoom) {
            room = new Room(client.getRoomId());
            roomPull.add(room);
        }
        room.addRoommate(client);
    }

    void removeClient(ClientHandler client) {
            getRoomById(client.getRoomId()).removeRoommate(client);
    }
}

