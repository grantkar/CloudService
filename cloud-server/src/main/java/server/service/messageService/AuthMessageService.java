package server.service.messageService;

import domain.AuthMessage;
import server.service.impl.DBRequestService;

public class AuthMessageService {

    private Object msg;
    private String message;

    AuthMessage authMessage;
    DBRequestService dbRequestService = new DBRequestService();

    public AuthMessageService(Object msg) {
        this.msg = msg;
        authMessage = (AuthMessage) msg;

        dbRequestService.getConnectionWithDB();
        if (dbRequestService.checkIfUserExistsForAuthorization(authMessage.getLogin())) {
            if (dbRequestService.checkIfPasswordIsRight(authMessage.getLogin(), authMessage.getPassword())) {
               message ="userIsValid/" + authMessage.getLogin();
            } else {
                message ="wrongPassword";
            }
        } else {
            message = "userDoesNotExist";
        }
        dbRequestService.disconnectDB();
    }

    public String getMessage() {
        return message;
    }
}
