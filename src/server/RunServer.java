package server;

import server.network.ChatServer;

public class RunServer {
    public static void main(String[] args) {
        new ChatServer().start();
    }
}
