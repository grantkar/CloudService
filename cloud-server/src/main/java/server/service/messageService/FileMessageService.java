package server.service.messageService;

import domain.FileMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileMessageService {

    private Object msg;
    FileMessage fileMessage;

    public FileMessageService(Object msg) {
        this.msg = msg;
        fileMessage = (FileMessage) msg;
        FileMessage fileMessage = (FileMessage) msg;
        Path pathToNewFile = Paths.get("cloud-server/cloudStorage/" +
                fileMessage.getLogin() + File.separator + fileMessage.getFileName());
        if (fileMessage.isDirectory() && fileMessage.isEmpty()) {
            if (Files.exists(pathToNewFile)) {
                System.out.println("Файл с таким именем уже существует");
            } else {
                try {
                    Files.createDirectory(pathToNewFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            if (Files.exists(pathToNewFile)) {
                System.out.println("Файл с таким именем уже существует");
            } else {
                try {
                    Files.write(Paths.get("cloud-server/cloudStorage/" +
                            fileMessage.getLogin() + File.separator + fileMessage.getFileName()),
                            fileMessage.getData(), StandardOpenOption.CREATE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getLogin (){
        return fileMessage.getLogin();
    }
}
