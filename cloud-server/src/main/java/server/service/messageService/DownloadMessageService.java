package server.service.messageService;

import domain.DownloadMessage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DownloadMessageService {

    private Object msg;
    private String login;
    private Path fileToDownload;
    DownloadMessage downloadMessage;

    public DownloadMessageService(Object msg) {
        this.msg = msg;
        downloadMessage = (DownloadMessage) msg;
        login = downloadMessage.getLogin();
        fileToDownload = Paths.get("cloud-server/cloudStorage/" + downloadMessage.getLogin()
                + File.separator + downloadMessage.getFileName());
    }

    public Path getFileToDownload() {
        return fileToDownload;
    }

    public String getLogin() {
        return login;
    }
}
