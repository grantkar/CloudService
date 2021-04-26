package server;

import io.netty.channel.EventLoopGroup;
import server.factory.Factory;

public class StartServer {
    public static void main(String[] args) {
        Factory.getServerService().startServer();
    }
}
