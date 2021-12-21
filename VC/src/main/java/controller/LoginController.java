package controller;

import java.io.IOException;
import java.sql.SQLException;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import org.msjth.model.*;
import org.msjth.model.service.*;

public class LoginController {
    // For switching to next scene without manually invoking any methods in another
    // controller
    private void goToNextScene(String fxml, Button btn) throws IOException {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxml));
        Parent root = loader.load();
        Scene scene = btn.getScene();
        scene.setRoot(root);
    }

    @FXML
    private TextField tfUsername, tfPassword;

    @FXML
    private Label lblWarning;

    @FXML
    private Button btnLogin;

    @FXML
    private ProgressIndicator piLogin;

    @FXML
    private void login() throws SQLException, IOException {
        String username = tfUsername.getText();
        String password = tfPassword.getText();
        lblWarning.setText("");
        if (username.isEmpty() || password.isEmpty())
            lblWarning.setText("Please enter your username and password!");
        else {
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    piLogin.setVisible(true);
                    return checkLogin(username, password);
                }
            };
            task.setOnSucceeded(e -> {
                if(!task.getValue()){
                    piLogin.setVisible(false);
                    lblWarning.setText("Incorrect username and password!");
                }
                else {
                    try {
                        goToNextScene("dashboard.fxml", btnLogin);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
            new Thread(task).start();
            piLogin.progressProperty().bind(task.progressProperty());
        }
    }

    private boolean checkLogin(String username, String password) throws SQLException, IOException {
        VC vc = VCService.getVCByUsernameAndPassword(username, password);
        if (vc != null) {
            DataHolder.setDataInstance(vc);
            return true;
        }
        return false;
    }
}