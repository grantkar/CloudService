package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import message.UpdateMessage;
import model.FileInfo;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import service.impl.NettyNetworkService;
import supportClass.CurrentLogin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class MainController implements Initializable {


    private HashMap<Integer, LinkedList<File>> folderCloudStorageListViews;
    private String myFirstDirectory = "cloud-client" + File.separator + "storage";


    private NettyNetworkService networkService = new NettyNetworkService();

    @FXML
    private TextField pathField;

    @FXML
    private ListView <FileInfo> filesListOnServer;


    @FXML
    ListView <FileInfo> filesOnLocalList;

    Path root;
    Path selectedCopyFile;


    public void exitMainWindow(ActionEvent actionEvent) {
        networkService.stopConnection();
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileToLocalStorage();

    }

    public void fileToLocalStorage () {

        filesOnLocalList.setCellFactory(new Callback<ListView<FileInfo>, ListCell<FileInfo>>() {
            @Override
            public ListCell<FileInfo> call(ListView<FileInfo> param) {
                return new ListCell<FileInfo>(){
                    @Override
                    protected void updateItem(FileInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty){
                            setText(null);
                            setStyle("");
                        } else {
                            String formattedFileName = String.format("%-20s", item.getFilename());
                            String formattedFileLength = String.format("%d%s", item.getFileSize(item.getLength()), item.getFileType());
                            if (item.getLength() == -1L){
                                formattedFileLength = String.format("%s", "[ DIR ]");
                            }
                            if (item.getLength() == -2L){
                                formattedFileLength = "";
                            }
                            String text = String.format("%s %-20s", formattedFileName, formattedFileLength);
                            setText(text);
                        }
                    }
                };
            }
        });
        goToPath(Paths.get(myFirstDirectory));

        filesListOnServer.setCellFactory(new Callback<ListView<FileInfo>, ListCell<FileInfo>>() {
            @Override
            public ListCell<FileInfo> call(ListView<FileInfo> param) {
                return new ListCell<FileInfo>(){
                    @Override
                    protected void updateItem(FileInfo item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty){
                            setText(null);
                            setStyle("");
                        } else {
                            String formattedFileName = String.format("%-20s", item.getFilename());
                            String formattedFileLength = String.format("%d%s", item.getFileSize(item.getLength()), item.getFileType());
                            if (item.getLength() == -1L){
                                formattedFileLength = String.format("%s", "[ DIR ]");
                            }
                            if (item.getLength() == -2L){
                                formattedFileLength = "";
                            }
                            String text = String.format("%s %-20s", formattedFileName, formattedFileLength);
                            setText(text);
                        }
                    }
                };
            }
        });
    }

    public void goToPath(Path path) {
        root = path;
        pathField.setText(root.toAbsolutePath().toString());
        filesOnLocalList.getItems().clear();
        filesOnLocalList.getItems().add(new FileInfo(FileInfo.UP_TOKEN, -2L));
        filesOnLocalList.getItems().addAll(scanFiles(path));
        filesOnLocalList.getItems().sort((o1, o2) -> {
            if (o1.getFilename().equals(FileInfo.UP_TOKEN)) {
                return -1;
            }
            if ((int) Math.signum(o1.getLength()) == (int) Math.signum(o2.getLength())){
                return o1.getFilename().compareTo(o2.getFilename());
            }
            return new Long(o1.getLength()-o2.getLength()).intValue();
        });
    }

    public List<FileInfo> scanFiles (Path root) {
        try {
          return Files.list(root).map(FileInfo::new).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Files scan exception: " +root);
        }
    }
    public void filesListClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            FileInfo fileInfo = filesOnLocalList.getSelectionModel().getSelectedItem();
            if (fileInfo != null) {
                if (fileInfo.isDirectory()) {
                    Path pathTo = root.resolve(fileInfo.getFilename());
                    goToPath(pathTo);
                }
                if (fileInfo.isUpElement()) {
                    Path pathTo = root.toAbsolutePath().getParent();
                    goToPath(pathTo);
                }
            }
        }
    }

    public void refresh() {
        goToPath(root);
    }

    public void copyAction(ActionEvent actionEvent) {
        FileInfo fileInfo = filesOnLocalList.getSelectionModel().getSelectedItem();
        if (selectedCopyFile ==null && (fileInfo == null || fileInfo.isDirectory() || fileInfo.isUpElement())) {
            return;
        }
        if (selectedCopyFile == null) {
            selectedCopyFile = root.resolve(fileInfo.getFilename());
            pathField.setText("Перейдите в папку, куда нужно скопировать выбранный файл, и снова нажмите File -> Copy");
            return;
        }
       if (selectedCopyFile != null) {
           try {
               Files.copy(selectedCopyFile,root.resolve(selectedCopyFile.getFileName()), StandardCopyOption.REPLACE_EXISTING);
               selectedCopyFile = null;
               refresh();
           } catch (IOException e) {
               Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible copy file");
               alert.showAndWait();
           }
       }
    }

    public void deleteAction(ActionEvent actionEvent) {
        FileInfo fileInfo = filesOnLocalList.getSelectionModel().getSelectedItem();
        if (fileInfo == null || fileInfo.isDirectory() || fileInfo.isUpElement()) {
            return;
        }
        try {
            Files.delete(root.resolve(fileInfo.getFilename()));
            refresh();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Impossible delete file");
            alert.showAndWait();
        }
    }

    public void sendFileToServer(ActionEvent actionEvent) {
        FileInfo fileInfo = filesOnLocalList.getSelectionModel().getSelectedItem();
        if (fileInfo == null || fileInfo.isDirectory() || fileInfo.isUpElement()) {
            return;
        } else {
            Path path = root.toAbsolutePath().resolve(fileInfo.getFilename());
            System.out.println(path);
            networkService.transferFilesToCloudStorage(CurrentLogin.getCurrentLogin(),path);
        }
    }

    public void downloadFileFromServer(ActionEvent actionEvent) {

    }

    public void renameFileAction(ActionEvent actionEvent) {

    }

    public void sendUpdateFileToServer(ActionEvent actionEvent) {
        networkService.sendUpdateMessageToServer(CurrentLogin.getCurrentLogin());



        initializeListOfCloudStorageItems(folderCloudStorageListViews);
    }

    Thread mainPanelServerListener = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true){
                try {
                    Object object = null;
                    object = networkService.readIncomingObject();
                    UpdateMessage message = (UpdateMessage) object;
                    folderCloudStorageListViews = new HashMap<>();
                    folderCloudStorageListViews.putAll(message.getCloudStorageContents());
                    Platform.runLater(() -> initializeListOfCloudStorageItems(folderCloudStorageListViews));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    });


    public void sendConnectToServer(ActionEvent event) {
        networkService.startConnection();
        mainPanelServerListener.setDaemon(true);
        mainPanelServerListener.start();
    }

    public void initializeListOfCloudStorageItems(HashMap<Integer, LinkedList<File>> listOfCloudStorageFiles) {

        try {
            ObservableList<FileInfo> listOfCloudItems = FXCollections.observableArrayList();
            if (!listOfCloudStorageFiles.isEmpty()) {
                for (int i = 0; i < listOfCloudStorageFiles.get(0).size(); i++) {
                    long initialSizeOfCloudFileOrDir = 0;
                    String nameOfCloudFileOrDir = listOfCloudStorageFiles.get(0).get(i).getName();
                    if (listOfCloudStorageFiles.get(0).get(i).isDirectory()) {
                        try {
                            initialSizeOfCloudFileOrDir = getActualSizeOfFolder(listOfCloudStorageFiles.get(0).get(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        initialSizeOfCloudFileOrDir = listOfCloudStorageFiles.get(0).get(i).length();
                    }

                    File pathOfFileInCloudStorage = new File(listOfCloudStorageFiles.get(0).get(i).getAbsolutePath());
                    listOfCloudItems.addAll(new FileInfo(nameOfCloudFileOrDir, initialSizeOfCloudFileOrDir, pathOfFileInCloudStorage));
                }
                filesListOnServer.setItems(listOfCloudItems);

            } else {
                filesListOnServer.setItems(listOfCloudItems);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public static long getActualSizeOfFolder(File file) throws Exception {
        long actualSizeOfFolder = 0;
        if (file.isDirectory()){
            for (File f: file.listFiles()){
                if (f.isFile()){
                    actualSizeOfFolder += f.length();
                }else if (f.isDirectory()){
                    actualSizeOfFolder += getActualSizeOfFolder(f);
                }
            }
        }
        return actualSizeOfFolder;
    }
}
