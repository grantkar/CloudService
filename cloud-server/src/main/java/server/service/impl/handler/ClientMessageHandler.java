package server.service.impl.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import message.*;
import model.FileInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ClientMessageHandler extends ChannelInboundHandlerAdapter {

    private static LinkedList<File> list = new LinkedList<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        } else {
            if (msg instanceof UpdateMessage) {
                UpdateMessage updateMessage = (UpdateMessage) msg;
                String receivedLogin = updateMessage.getLogin();
                ctx.writeAndFlush(new UpdateMessage(getContentsOfCloudStorage(receivedLogin)));
            } else
                if (msg instanceof FileMessage) {
                FileMessage fileMessage = (FileMessage) msg;
                Path pathToNewFile = Paths.get("cloud-server/cloudStorage/" + fileMessage.getLogin() + File.separator + fileMessage.getFileName());
                if (fileMessage.isDirectory() && fileMessage.isEmpty()) {
                    if (Files.exists(pathToNewFile)) {
                        System.out.println("Файл с таким именем уже существует");
                    } else {
                        Files.createDirectory(pathToNewFile);
                    }
                } else {
                    if (Files.exists(pathToNewFile)) {
                        System.out.println("Файл с таким именем уже существует");
                    } else {
                        Files.write(Paths.get("cloud-server/cloudStorage/" + fileMessage.getLogin() + File.separator + fileMessage.getFileName()), fileMessage.getData(), StandardOpenOption.CREATE);
                    }
                }
                ctx.writeAndFlush(new UpdateMessage(getContentsOfCloudStorage(fileMessage.getLogin())));
            } else
                if (msg instanceof DownloadMessage){
                DownloadMessage downloadMessage = (DownloadMessage) msg;
                    Path fileToDownload = Paths.get("cloud-server/cloudStorage/" + downloadMessage.getLogin() + File.separator + downloadMessage.getFileName());
                    ctx.writeAndFlush(new FileMessage(fileToDownload));
                    try {
                        ctx.writeAndFlush(new FileMessage(fileToDownload));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            } else if (msg instanceof DeletionMessage){
                    DeletionMessage delete = (DeletionMessage) msg;
                    Path fileToDelete = Paths.get("cloud-server/cloudStorage/" + delete.getLogin() + File.separator + delete.getFileName());
                    try {
                        Files.delete(fileToDelete);
                    } catch (IOException e){
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible delete file");
                        alert.showAndWait();
                    }
                } else
                    if (msg instanceof AuthMessage) {
                        AuthMessage authMessage = (AuthMessage) msg;
                        DBRequestHandler.getConnectionWithDB();
                if (DBRequestHandler.checkIfUserExistsForAuthorization(authMessage.getLogin())) {
                    if (DBRequestHandler.checkIfPasswordIsRight(authMessage.getLogin(), authMessage.getPassword())) {
                        ctx.writeAndFlush("userIsValid/" + authMessage.getLogin());
                    } else {
                        ctx.writeAndFlush("wrongPassword");
                    }
                } else {
                    ctx.writeAndFlush("userDoesNotExist");
                }
                DBRequestHandler.disconnectDB();
            } else if (msg instanceof RegistrationMessage) {
                RegistrationMessage registrationMessage = (RegistrationMessage) msg;
                DBRequestHandler.getConnectionWithDB();
                if (DBRequestHandler.checkIfUserExistsForAuthorization(registrationMessage.getLogin())) {
                    ctx.writeAndFlush("userAlreadyExists");
                } else {
                    if (DBRequestHandler.registerNewUser(registrationMessage.getLogin(), registrationMessage.getPassword())) {
                        File newDirectory = new File("cloud-server/cloudStorage/" + registrationMessage.getLogin());
                        newDirectory.mkdir();
                        ctx.writeAndFlush("registrationIsSuccessful");
                    }
                }
                DBRequestHandler.disconnectDB();
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        DBRequestHandler.disconnectDB();
        ctx.flush();
        System.out.println("client disconnected");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        DBRequestHandler.disconnectDB();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client connected");
    }

    public static HashMap<Integer, LinkedList<File>> getContentsOfCloudStorage(String login) {
        HashMap<Integer, LinkedList<File>> cloudStorageContents;
        LinkedList<File> listCloudStorageFiles = new LinkedList<>();
        File path = new File("cloud-server/CloudStorage/" + login);
        File[] files = path.listFiles();
        cloudStorageContents = new HashMap<>();
        if (files.length == 0) {
            cloudStorageContents.clear();
        } else {
            listCloudStorageFiles.clear();
            for (int i = 0; i < files.length; i++) {
                listCloudStorageFiles.add(files[i]);
            }
            cloudStorageContents.clear();
            cloudStorageContents.put(0, listCloudStorageFiles);
        }
        return cloudStorageContents;
    }

}

