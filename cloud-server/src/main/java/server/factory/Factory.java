package server.factory;

import server.service.ServerService;
import server.service.impl.NettyServerService;

public class Factory {

    public static ServerService getServerService() {
        return new NettyServerService();
    }
}
