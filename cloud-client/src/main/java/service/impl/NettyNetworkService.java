package service.impl;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import message.*;
import service.NetworkService;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;



public class NettyNetworkService implements NetworkService {

    private static final String HOST = "localHost";
    private static final int PORT = 8189;

    private static Socket socket;
    private static ObjectEncoderOutputStream outStream;
    private static ObjectDecoderInputStream inStream;


    @Override
    public void startConnection() {
        try {
            socket = new Socket(HOST, PORT);
            outStream = new ObjectEncoderOutputStream(socket.getOutputStream());
            inStream = new ObjectDecoderInputStream(socket.getInputStream(),150*1024*1024);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to connect");
        }
    }

    @Override
    public void stopConnection() {
        try {
            outStream.close();
            inStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transferFilesToCloudStorage(String login, Path path) {
        try {
            System.out.println(login);
            System.out.println(path);
                outStream.writeObject(new FileMessage(login, path));
                outStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean sendDownloadMessage(String fileName, String login) {
        try {
                outStream.writeObject(new DownloadMessage(fileName,login));
                outStream.flush();
                return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean sendUpdateMessageToServer(String login){
        try {
            outStream.writeObject(new UpdateMessage(login));
            outStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean sendDeletionMessage(String fileName, String login){
        try {
            outStream.writeObject(new DeletionMessage(fileName, login));
            outStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean sendAuthMessageToServer(String login, String password) {
        try {
            outStream.writeObject(new AuthMessage(login,password));
            outStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean sendRegMessageToServer(String login, String password) {
        try {
            outStream.writeObject(new RegistrationMessage(login,password));
            outStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Object readIncomingObject() throws IOException, ClassNotFoundException {
        return inStream.readObject();
    }


}
