package server.network.handler;

import server.messages.ResponseToClient;
import server.network.server.ChatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;


public class ClientHandler implements Runnable {

    private ChatServer chatServer = null;

    private DataOutputStream outMessage = null;
    private DataInputStream inMessage = null;

    private String nickname = "";
    private String roomId = "";


    public String getRoomId() {
        return roomId;
    }

    public ClientHandler(Socket clientSocket, ChatServer chatServer) {
        try {
            this.chatServer = chatServer;
            this.outMessage = new DataOutputStream(clientSocket.getOutputStream());
            this.inMessage = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Fail to open data streams!");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String clientMessage = inMessage.readUTF().trim();

                if (clientMessage.startsWith("/")) {
                    processIncomingCommand(clientMessage);
                } else {
                    chatServer.sendMessageToRoom(ResponseToClient.PUBLIC_MESSAGE(nickname, clientMessage), roomId);
                }
            }
        } catch (IOException e) {
            System.err.println("Fail to process incoming message; client: " + nickname + ", room: " + roomId);
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }

    private void processIncomingCommand(String message) {
        String[] commands = message.split("\\s+");
        String mainCommand = commands[0];
        switch (mainCommand) {
            case "/exit":
                handleExitCommand();
                return;
            case "/leave":
                handleLeaveCommand();
                return;
            case "/nick":
                handleNickCommand(commands);
                return;
            case "/room":
                handleRoomCommand(commands);
                return;
            case "/help":
                handleHelpCommand(commands);
                return;
            case "/count":
                handleCountCommand();
                return;
            case "/list":
                handleListCommand();
                return;
            case "/w":
                handlePMessage(commands, message);
                return;
            case "/clear":
                handleClearCommand(commands);
                return;
            default:
                sendMessage(ResponseToClient.WARNING("No such command!\n" +
                        "Type \"/help\" for information!"));
        }
    }

    private void handleClearCommand(String[] commands) { // TODO: 06.07.2018 diff types of clear
        sendMessage(ResponseToClient.COMMAND("clear"));
    }

    private void handlePMessage(String[] commands, String message) {
        if (commands.length < 2) {
            sendMessage(ResponseToClient.WARNING("Missing argument (receiver)"));
            return;
        }

        String receiver = commands[1];

        message = message.replaceFirst("/w", "").
                replaceFirst(receiver, "").
                trim();

        chatServer.sendPrivateMessage(ResponseToClient.PRIVATE_MESSAGE(nickname, message, receiver), roomId, receiver);
        if (!nickname.equals(receiver)) {
            chatServer.sendPrivateMessage(ResponseToClient.PRIVATE_MESSAGE(nickname, message, receiver), roomId, nickname);
        }

    }

    private void handleExitCommand() {
        handleLeaveCommand();
        sendMessage(ResponseToClient.COMMAND("exit"));
        chatServer.removeClient(this);

    }

    private void handleCountCommand() {
        sendMessage(ResponseToClient.NOTE("Current number of chatters: "
                + chatServer.getRoomById(roomId).size()));
    }

    private void handleListCommand() {
        StringBuilder str = new StringBuilder();
        for (ClientHandler clientHandler : chatServer.getRoomById(roomId)) {
            str.append(clientHandler.toString()).append('\n');
        }
        sendMessage(ResponseToClient.NOTE("People in this room:\n" + str.toString()));
    }

    private void handleHelpCommand(String[] commands) {
        chatServer.sendGlobalMessage();
        if (commands.length == 1) {
            sendMessage(ResponseToClient.NOTE(
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
        if (commands.length > 2) {
            sendMessage(ResponseToClient.WARNING("Too many arguments!"));
            return;
        }

        String argument = commands[1];
        switch (argument) {
            case "nick":
                sendMessage(ResponseToClient.NOTE("Nick should contain only numbers, letters or '_'.\n" +
                        "Nick's length can't be less than 3.\n" +
                        "Any whitespace characters are forbidden."));
                break;

            case "room":
                sendMessage(ResponseToClient.NOTE("Room id should contain only numbers, letters or '_'.\n" +
                        "Id's length can't be less than 3.\n" +
                        "Any whitespace characters are forbidden."));
                break;

            case "leave":
                sendMessage(ResponseToClient.NOTE("After this command you go to empty room with unreachable for\n" +
                        "other users id."));
                break;

            case "w":
                sendMessage(ResponseToClient.NOTE("Nickname should be wrote correctly (see /help nick).\n" +
                        "Also you can PM to yourself or to non-existing users (for notes maybe)"));
                break;

            default:
                sendMessage(ResponseToClient.NOTE("CommandMessage is not correct"));
                break;
        }
    }

    private void handleRoomCommand(String[] command) {
        if (command.length == 1) {
            sendMessage(ResponseToClient.NOTE("Current room is: " + roomId));
            return;
        }

        String newRoom = command[1];

        if (command.length > 2 || newRoom.length() < 3 || !newRoom.matches("((?U)\\w+)|")) {
            sendMessage(ResponseToClient.WARNING("Room id is not correct!\nSee /help room"));
            return;
        }

        if (roomId.equals(newRoom)) {
            sendMessage(ResponseToClient.WARNING("You are already in this room!"));
            return;
        }

        String tempRoom = roomId;
        chatServer.removeClient(this);

        roomId = newRoom;
        chatServer.addNewClient(this);
        sendMessage(ResponseToClient.COMMAND("room_change", roomId));

        nickname = getUniqueNicknameInRoom(nickname);
        sendMessage(ResponseToClient.COMMAND("nick_change", nickname));

        chatServer.sendMessageToRoom(ResponseToClient.NOTE(nickname + " joined the room!"), roomId);
        chatServer.sendMessageToRoom(ResponseToClient.NOTE(nickname + " left the room"), tempRoom);
    }

    private boolean nameIsNotUniqueInRoom(String nick) {
        for (ClientHandler client : chatServer.getRoomById(roomId)) {
            if (client != this && client.getNickname().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    private String getUniqueNicknameInRoom(String nick) {
        String newNick = nick;
        int count = 1;
        while (nameIsNotUniqueInRoom(newNick)) {
            newNick = nick + '_' + (count++);
        }
        return newNick;
    }

    private void handleNickCommand(String[] commands) {
        if (commands.length == 1) {
            sendMessage(ResponseToClient.NOTE("Your nickname is: " + nickname));
            return;
        }

        String newNickname = commands[1];

        if (commands.length > 2 || newNickname.length() < 3 || !newNickname.matches("((?U)\\w+)|")) {
            sendMessage(ResponseToClient.WARNING("New nickname is not correct!\nSee /help nick"));
            return;
        }

        if (newNickname.equals(nickname) || nameIsNotUniqueInRoom(newNickname)) {
            sendMessage(ResponseToClient.WARNING("Nickname \"" + newNickname + "\" is already occupied in this room!\n" +
                    "/list - to see all chatters."));
            return;
        }

        if (!nickname.isEmpty()) {
            chatServer.sendMessageToRoom(ResponseToClient.NOTE(nickname + " changed nick to: " + commands[1]), roomId);
        }

        nickname = newNickname;
        sendMessage(ResponseToClient.COMMAND("nick_change", nickname));
    }


    private void handleLeaveCommand() {
        chatServer.removeClient(this);

        String tempRoom = roomId;

        roomId = randomRoom();
        chatServer.addNewClient(this);

        chatServer.sendMessageToRoom(ResponseToClient.NOTE(nickname + " left the room"), tempRoom);
        sendMessage(ResponseToClient.NOTE("You are in empty/private room now"));
    }

    private String randomRoom() {
        StringBuilder str = new StringBuilder( "$&_" + nickname + "_&$");
        for (int i = 1; i <= 50; i++) {
            str.append(new Random().nextInt(10)).append(nickname.charAt(new Random().nextInt(nickname.length())));
        }
        return str.toString();
    }

    public void sendMessage(ResponseToClient msg) {
        try {
            outMessage.writeUTF(msg.toString());
            outMessage.flush();
        } catch (IOException e) {
            System.err.println("Fail to send message; client: " + nickname + ", room: " + roomId + ", message: " + msg.toString());
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return nickname;
    }
}
