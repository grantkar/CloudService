package domain;

import java.io.Serializable;


public class DownloadMessage implements Serializable {

    private String fileName;
    private String login;

    public DownloadMessage(String fileName, String login) {
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
