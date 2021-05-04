package server.service.messageService;

import domain.DeletionMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DeletionMessageService {

    private Object msg;
    private String login;
    Path fileToDelete;
    DeletionMessage deletionMessage;


    public DeletionMessageService(Object msg) {
        this.msg = msg;
        deletionMessage = (DeletionMessage) msg;
        fileToDelete = Paths.get("cloud-server/cloudStorage/" +
                deletionMessage.getLogin() + File.separator + deletionMessage.getFileName());
        login = deletionMessage.getLogin();
        try {
            System.out.println(fileToDelete);
            Files.delete(fileToDelete);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Impossible delete file");
        }
    }

    public String getLogin() {
        return login;
    }
}
