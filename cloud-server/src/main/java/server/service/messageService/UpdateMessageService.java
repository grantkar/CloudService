package server.service.messageService;

import domain.UpdateMessage;

public class UpdateMessageService {

    private Object msg;
    UpdateMessage updateMessage;

    public UpdateMessageService(Object msg) {
        this.msg = msg;
        updateMessage = (UpdateMessage) msg;
    }

    public String update() {
        String receivedLogin = updateMessage.getLogin();
        return receivedLogin;
    }

}
