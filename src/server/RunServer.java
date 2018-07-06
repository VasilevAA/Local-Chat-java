package server;

import server.network.server.ChatServer;

public class RunServer {
    public static void main(String[] args) {
        new ChatServer().start();
    }
}
