package message;

import java.io.Serializable;

public class DeletionMessage implements Serializable {
    private String fileName;
    private String login;

    public DeletionMessage(String fileName, String login) {
        this.fileName = fileName;
        this.login = login;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLogin() {
        return login;
    }
}
