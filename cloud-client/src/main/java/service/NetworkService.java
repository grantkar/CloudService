package service;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedList;

public interface NetworkService {


    // метод установки соединения с сервером
    void startConnection();

    // метод отключения соединения с сервером
    void stopConnection();

    // метод отправки файла из локального хранилища в серверное хранилище
    void transferFilesToCloudStorage(String login, Path path);

    //метод скачивания выбранного файла
    boolean sendDownloadMessage (String fileName, String login);

    //Метод обновления списка файлов на серверном хранилище
    boolean sendUpdateMessageToServer(String login);

    //Метод удаления выбранного файла из серверного хранилища
    boolean sendDeletionMessage(String fileName, String login);

    //метод отправки сообщения на авторизацию
    boolean sendAuthMessageToServer(String login, String password);

    //метод отправки сообщения на регистрацию
    boolean sendRegMessageToServer(String login, String password);

    //Метод считывания входящих сообщений
    Object readIncomingObject();



}
