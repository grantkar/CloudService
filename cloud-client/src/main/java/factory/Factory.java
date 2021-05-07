package factory;

import service.NetworkService;
import service.impl.NettyNetworkService;

public class Factory {

    public static NetworkService getNetworkService() {
        return new NettyNetworkService();
    }
}
