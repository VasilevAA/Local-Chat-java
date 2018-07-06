package server.network;

import server.messages.MessageToClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;


public class ClientHandler implements Runnable {

    private ChatServer chatServer;

    private DataOutputStream outMessage;
    private DataInputStream inMessage;

    private String nickname = "";
    private String roomId = "";



    String getRoomId() {
        return roomId;
    }

    ClientHandler(Socket clientSocket, ChatServer chatServer) {
        try {
            this.chatServer = chatServer;
            this.outMessage = new DataOutputStream(clientSocket.getOutputStream());
            this.inMessage = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        while (true) {

            try {
                if (inMessage.available() > 0) {
                    String clientMessage = inMessage.readUTF();

                    if (clientMessage.startsWith("/")) {

                        if (processIncomingCommand(clientMessage)) {
                            continue;
                        } else {
                            return;
                        }
                    }

                    chatServer.sendMessageToAllRoom(MessageToClient.PUBLIC_MESSAGE(nickname, clientMessage), roomId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    String getNickname() {
        return nickname;
    }

    private boolean processIncomingCommand(String message) {
        String[] parse = message.split("\\s+");
        String command = parse[0];

        switch (command) {
            case "/exit":
                handleExitCommand();
                return false;

            case "/leave":
                handleLeaveCommand();
                return true;

            case "/nick":
                handleNickCommand(parse);
                return true;

            case "/room":
                handleRoomCommand(parse);
                return true;

            case "/help":
                handleHelpCommand(parse);
                return true;

            case "/count":
                handleCountCommand();

                return true;

            case "/list":
                handleListCommand();
                return true;

            case "/w":
                handlePMessage(parse,message);
                return true;

            case "/clear":
                    handleClearCommand(parse);
                return true;

            default:
                sendMessage(MessageToClient.WARNING("No such command!\n" +
                        "Type \"/help\" for information!"));
                return true;

        }
    }

    private void handleClearCommand(String[] parse) { // TODO: 06.07.2018 diff types of clear
        sendMessage(MessageToClient.COMMAND("clear"));
    }

    private void handlePMessage(String[] parse, String message) {
        if (parse.length < 2) {
            sendMessage(MessageToClient.WARNING("Missing argument (reciever)"));
            return;
        }

        String reciever = parse[1];

        message = message.replaceFirst("/w","").
                replaceFirst(reciever,"").
                trim();

        chatServer.sendPrivateMessage(MessageToClient.PRIVATE_MESSAGE(nickname, message, reciever), roomId, reciever);
        if(!nickname.equals(reciever)) {
            chatServer.sendPrivateMessage(MessageToClient.PRIVATE_MESSAGE(nickname, message, reciever), roomId, nickname);
        }

    }

    private void handleExitCommand() {
        handleLeaveCommand();
        sendMessage(MessageToClient.COMMAND("exit"));
        chatServer.removeClient(this);
    }

    private void handleCountCommand() {
        sendMessage(MessageToClient.NOTE("Current number of chatters: "
                + chatServer.getRoomById(roomId).numberOfChatters()));
    }

    private void handleListCommand() {
        StringBuilder str = new StringBuilder();
        for (ClientHandler clientHandler : chatServer.getRoomById(roomId).getClients()) {
                str.append(clientHandler.toString()).append('\n');
        }
        sendMessage(MessageToClient.NOTE("People in this room:\n" + str.toString()));
    }

    private void handleHelpCommand(String[] parse) {
        if(parse.length == 1) {
            sendMessage(MessageToClient.NOTE(
                    "commands:\n" +
                            "/help <command> - detail help on given command\n" +
                            "/nick - show your current nickname\n" +
                            "/nick <name> - change your current nickname\n" +
                            "/room - your current room\n" +
                            "/room <id> - change your current room\n" +
                            "/count - number of people in current room\n" +
                            "/list - list of people in current room\n" +
                            "/leave - leave current room\n" +
                            "/w <receiver> # <message> - private message\n " +
                            "/clear - remove all messages\n" +
                            "/exit - exit chat\n"
            ));
            return;
        }
        if(parse.length > 2){
            return;
        }
        switch (parse[1]){
            case "nick":
                sendMessage(MessageToClient.NOTE("Nick should contain only numbers, letters or '_'.\n" +
                        "Nick's length can't be less than 3.\n" +
                        "Any whitespace characters are forbidden."));
                break;

            case "room":
                sendMessage(MessageToClient.NOTE("Room id should contain only numbers, letters or '_'.\n"+
                        "Id's length can't be less than 3.\n"+
                        "Any whitespace characters are forbidden."));
                break;

            case "leave":
                sendMessage(MessageToClient.NOTE("After this command you go to empty room with unreachable for\n" +
                        "other users id."));
                break;

            case "w":
                sendMessage(MessageToClient.NOTE("Nickname should be wrote correctly (see /help nick).\n" +
                        "Also you can PM to yourself or to non-existing users (for notes maybe)"));
                break;

                default:
                    sendMessage(MessageToClient.NOTE("CommandMessage is not correct"));
                    break;
        }


    }

    private void handleRoomCommand(String[] parse) {
        if (parse.length == 1) {
            sendMessage(MessageToClient.NOTE("Current room is: " + roomId));
            return;
        }

        if(parse.length > 2 || parse[1].length() < 3 || !parse[1].matches("((?U)\\w+)|")){
            sendMessage(MessageToClient.WARNING("Room id is not correct!\nSee /help room"));
            return;
        }

        if (roomId.equals(parse[1])) {
            sendMessage(MessageToClient.WARNING("You are already in this room!"));
            return;
        }

        String tempRoom = roomId;
        chatServer.removeClient(this);

        roomId = parse[1];
        chatServer.addNewClient(this);

        sendMessage(MessageToClient.COMMAND("room_change",roomId));

        nickname = getUniqueNicknameInRoom(nickname);
        sendMessage(MessageToClient.COMMAND("nick_change",nickname));

        chatServer.sendMessageToAllRoom(MessageToClient.NOTE(nickname + " joined the room!"), roomId);
        chatServer.sendMessageToAllRoom(MessageToClient.NOTE(nickname + " left the room"), tempRoom);

    }

    private boolean nameIsNotUniqueInRoom(String nick){
        for (ClientHandler client : chatServer.getRoomById(roomId).getClients()) {
            if(client != this && client.getNickname().equals(nick)){
                return true;
            }
        }
        return false;
    }

    private String getUniqueNicknameInRoom(String nick){
        String newNick = nick;
        int count = 1;

        while(nameIsNotUniqueInRoom(newNick)){
            newNick = nick + (count++);
        }

        return newNick;
    }

    private void handleNickCommand(String[] parse) {
        if (parse.length == 1) {
            sendMessage(MessageToClient.NOTE("Your nickname is: " + nickname));
            return;
        }

        if(parse.length > 2 || parse[1].length() < 3 || !parse[1].matches("((?U)\\w+)|")){
            sendMessage(MessageToClient.WARNING("New nickname is not correct!\nSee /help nick"));
            return;
        }

        if( parse[1].equals(nickname) || nameIsNotUniqueInRoom(parse[1])){
            sendMessage(MessageToClient.WARNING("Nickname \""+parse[1]+"\" is already occupied in this room!\n" +
                    "/list - to see all chatters."));
            return;
        }

        if (!nickname.isEmpty()) {
            chatServer.sendMessageToAllRoom(MessageToClient.NOTE(nickname + " changed nick to: " + parse[1]), roomId);
        }

        nickname = parse[1];
        sendMessage(MessageToClient.COMMAND("nick_change",nickname));
    }


    private void handleLeaveCommand() {
        chatServer.removeClient(this);

        String tempRoom = roomId;

        roomId = nickname + randomRoom();
        chatServer.addNewClient(this);

        chatServer.sendMessageToAllRoom(MessageToClient.NOTE(nickname + " left the room"), tempRoom);
        sendMessage(MessageToClient.NOTE("You are in empty/private room now"));
    }

    private String randomRoom() {
        StringBuilder str = new StringBuilder();
        for (int i = 1; i <= 50; i++) {
            str.append(new Random().nextInt(i * 7)).append(Character.valueOf((char) new Random().nextInt(i * 5)));
        }
        return str.toString();
    }

    void sendMessage(MessageToClient msg) {

        try {
            outMessage.writeUTF(msg.toString());
            outMessage.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return nickname;
    }
}
