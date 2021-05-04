package server;

import server.factory.Factory;

public class StartServer {
    public static void main(String[] args) {
        Factory.getServerService().startServer();
    }
}
