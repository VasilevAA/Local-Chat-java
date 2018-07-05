package client.chat.logic;

import client.chat.gui.ChatWindow;
import client.messages.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatLogic {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3443;

    private Socket clientSocket;
    private DataInputStream incomingMessage;
    private DataOutputStream messageToSend;
    private volatile boolean shouldProcessIncomingMessages = true;

    private String nickname;
    private String roomId;
    private ChatWindow chatWindow;

    public String getNickname() {
        return nickname;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public ChatLogic(String nick, String roomId, ChatWindow chatView) {
        nickname = (nick.isEmpty() ? "Anon" : nick.trim());
        this.roomId = roomId.trim().isEmpty() ? "public" : roomId.trim();
        chatWindow = chatView;

        connectToChatServer();
        setupProcessingOfIncomingMessages();
    }

    private void connectToChatServer() {
        try {
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            incomingMessage = new DataInputStream(clientSocket.getInputStream());
            messageToSend = new DataOutputStream(clientSocket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupProcessingOfIncomingMessages() {
        new Thread(() -> {
            try {

                while (shouldProcessIncomingMessages) {
                    if (incomingMessage.available() > 0) {

                        Response response = Response.getCorrectResponse(incomingMessage.readUTF(),nickname);
                        processResponse(response);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
        sendRequest("/help");
        sendRequest("/nick " + nickname);
        sendRequest("/room " + roomId);
    }


    public void sendRequest(String message) {
        try {
            messageToSend.writeUTF(message);
            messageToSend.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void processResponse(Response response) {
        chatWindow.receiveMessage(response);
    }


    public void freeResources() {
        try {
            shouldProcessIncomingMessages = false;

            messageToSend.writeUTF("/exit");
            messageToSend.flush();

            incomingMessage.close();
            messageToSend.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
