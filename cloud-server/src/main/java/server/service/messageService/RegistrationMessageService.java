package server.service.messageService;

import domain.RegistrationMessage;
import server.service.impl.DBRequestService;

import java.io.File;

public class RegistrationMessageService {

    private Object msg;
    private String message;

    RegistrationMessage regMessage;
    DBRequestService dbRequestService = new DBRequestService();

    public RegistrationMessageService(Object msg) {
        this.msg = msg;
        regMessage = (RegistrationMessage) msg;

        dbRequestService.getConnectionWithDB();
        if (dbRequestService.checkIfUserExistsForAuthorization(regMessage.getLogin())) {
            message = "userAlreadyExists";
        } else {
            if (dbRequestService.registerNewUser(regMessage.getLogin(), regMessage.getPassword())) {
                File newDirectory = new File("cloud-server/cloudStorage/" + regMessage.getLogin());
                newDirectory.mkdir();
                message = "registrationIsSuccessful";
            }
        }
        dbRequestService.disconnectDB();
    }

    public String getMessage() {
        return message;
    }
}
