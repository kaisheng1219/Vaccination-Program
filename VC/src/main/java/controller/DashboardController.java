package controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import org.msjth.model.*;
import org.msjth.model.service.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private VC vc;

    @FXML
    private StackPane contentArea;

    private void changeContentDisplayed(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxml));
        Parent root = loader.load();
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(root);
    }

    @FXML
    private Label lblVCName;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vc = (VC) DataHolder.getDataInstance();
        lblVCName.setText("Vaccination Centre " + vc.getName());
        try {
            viewRecipients();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // View Recipients
    @FXML
    private void viewRecipients() throws IOException {
        changeContentDisplayed("view-recipient.fxml");
    }

    // Set Appointment Date
    @FXML
    private void setAppointmentDate() throws IOException {
        changeContentDisplayed("set-appointment.fxml");
    }

    // View Statistics
    @FXML
    private void viewStatistics() throws IOException {
        changeContentDisplayed("view-statistics.fxml");
    }

    // Log Out
    @FXML
    private Button btnLogOut;

    @FXML
    private void logOut() {
        DataHolder.clearDataInstance();
        try {
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("login.fxml"));
            Parent root = loader.load();
            Scene scene = btnLogOut.getScene();
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Refresh
    @FXML
    private ProgressIndicator piRefresh;

    @FXML
    private void refresh() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                piRefresh.setVisible(true);
                vc = VCService.getVCByUsernameAndPassword(vc.getUsername(), vc.getPassword());
                DataHolder.setDataInstance(vc);
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            piRefresh.setVisible(false);
            try {
                createInfoBox();
                viewRecipients();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        new Thread(task).start();
    }

    private void createInfoBox() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        alert.getDialogPane().setPrefSize(400, 100);
        alert.getDialogPane().setHeaderText("Refresh Status");

        Label temp = new Label();
        temp.setFont(new Font("Arial", 25));
        temp.setPrefSize(400, 50);
        temp.setTextFill(Color.rgb(22, 3, 133));
        temp.setAlignment(Pos.CENTER);
        temp.setText("Successfully Refreshed");
        alert.getDialogPane().setContent(temp);
        alert.show();
    }
}