package controller;

import factory.Factory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.NetworkService;
import service.impl.NettyNetworkService;
import supportClass.CurrentLogin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainAuthController implements Initializable {



    NettyNetworkService networkService = new NettyNetworkService();

    @FXML
    Button finalRegistrationButton;
    @FXML
    VBox registrationBlock;
    @FXML
    TextField registrationLoginForm;
    @FXML
    PasswordField registrationPassForm;
    @FXML
    PasswordField repeatPassForm;
    @FXML
    TextField sendLogin;
    @FXML
    PasswordField sendPassword;
    @FXML
    Button registration;
    @FXML
    Button enter;
    @FXML
    Button cancelRegistrationButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        networkService.startConnection();
        OpeningPanelServerListener.setDaemon(true);
        OpeningPanelServerListener.start();
    }

    public void switchScenes(String login) throws IOException {
        Stage stage;
        Parent root;
        stage = (Stage)enter.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("/view/mainWindow.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Cloud Service"+ File.separator +""+login);
        stage.show();
    }
    public void authorizeAndSwitchToMainPanel(String login){
        Platform.runLater(() -> {
            try {
                switchScenes(login);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    Thread OpeningPanelServerListener = new Thread(() -> {
        for (;;){
            Object serverMessage = null;
            try {
                serverMessage = networkService.readIncomingObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (serverMessage.toString().startsWith("userIsValid/")){
                String[] receivedWords = serverMessage.toString().split("/");
                String login = receivedWords[1];
                CurrentLogin.setCurrentLogin(login);
                authorizeAndSwitchToMainPanel(login);
            }else if (serverMessage.toString().startsWith("wrongPassword")){

                    System.out.println("Wrong password");
            }else if (serverMessage.toString().startsWith("userDoesNotExist")){

                    System.out.println("Such user does not exist");


            }else if (serverMessage.toString().equals("userAlreadyExists")){

                    System.out.println("Such user already exists");


            } else if (serverMessage.toString().equals("registrationIsSuccessful")){
                System.out.println("Registration OK");
            }
        }
    });

    public void sendAuthMessage(ActionEvent event) {
        if (!sendLogin.getText().isEmpty() && !sendPassword.getText().isEmpty()){
            networkService.sendAuthMessageToServer(sendLogin.getText(),sendPassword.getText());
            sendLogin.clear();
            sendPassword.clear();
        }
    }

    public void showRegistrationForms(ActionEvent event) {
        registrationBlock.setVisible(true);
        finalRegistrationButton.setVisible(true);
        registration.setVisible(false);
        cancelRegistrationButton.setVisible(true);
    }

    public void cancelRegistration(ActionEvent event) {
        registration.setVisible(true);
        cancelRegistrationButton.setVisible(false);
        registrationLoginForm.clear();
        registrationPassForm.clear();
        repeatPassForm.clear();
        registrationBlock.setVisible(false);
        finalRegistrationButton.setVisible(false);
    }

    public void sendRegMessageToServer(ActionEvent event) {
        if (!registrationLoginForm.getText().isEmpty() && !registrationPassForm.getText().isEmpty() && !repeatPassForm.getText().isEmpty()){
            if (registrationPassForm.getText().equals(repeatPassForm.getText())){
                networkService.sendRegMessageToServer(registrationLoginForm.getText(),repeatPassForm.getText());
            }else {
                System.out.println("You've entered unequal passwords");
                registrationPassForm.clear();
                repeatPassForm.clear();
            }
        }
    }
}
