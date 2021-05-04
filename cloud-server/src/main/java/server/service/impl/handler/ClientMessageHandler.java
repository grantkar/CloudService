package server.service.impl.handler;

import domain.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import server.service.DBService;
import server.service.messageService.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class ClientMessageHandler extends ChannelInboundHandlerAdapter {

    DBService dbService;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        } else {
            if (msg instanceof UpdateMessage) {
                ctx.writeAndFlush(new UpdateMessage(getContentsOfCloudStorage(new UpdateMessageService(msg).update())));
            } else
                if (msg instanceof FileMessage) {
                    FileMessageService fileMessageService =  new FileMessageService(msg);
                    ctx.writeAndFlush(new UpdateMessage(getContentsOfCloudStorage(fileMessageService.getLogin())));
            } else
                if (msg instanceof DownloadMessage){
                    DownloadMessageService downloadFile = new DownloadMessageService(msg);
                    try {
                        ctx.writeAndFlush(new FileMessage(downloadFile.getFileToDownload()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            } else if (msg instanceof DeletionMessage){
                    DeletionMessageService delete = new DeletionMessageService(msg);
                    ctx.writeAndFlush(new UpdateMessage(getContentsOfCloudStorage(delete.getLogin())));
                } else
                    if (msg instanceof AuthMessage) {
                       AuthMessageService authMessageService = new AuthMessageService(msg);
                    ctx.writeAndFlush(authMessageService.getMessage());
            } else if (msg instanceof RegistrationMessage) {
                        RegistrationMessageService regMessage = new RegistrationMessageService(msg);
                        ctx.writeAndFlush(regMessage.getMessage());
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        dbService.disconnectDB();
        ctx.flush();
        System.out.println("client disconnected");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        dbService.disconnectDB();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client connected");
    }

    public static HashMap<Integer, LinkedList<File>> getContentsOfCloudStorage(String login) {
        HashMap<Integer, LinkedList<File>> cloudStorageContents;
        File path = new File("cloud-server/CloudStorage/" + login);
        File[] files = path.listFiles();
        cloudStorageContents = new HashMap<>();
        if (files.length == 0) {
            return cloudStorageContents;
        } else {
            LinkedList<File> listCloudStorageFiles = new LinkedList<>(Arrays.asList(files));
            cloudStorageContents.put(0, listCloudStorageFiles);
        }
        return cloudStorageContents;
    }
}

