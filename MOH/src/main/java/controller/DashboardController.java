package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.msjth.model.*;
import org.msjth.model.service.MOHService;

public class DashboardController implements Initializable {
    @FXML
    private StackPane contentArea;
    private void changeContentDisplayed(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxml));
        Parent root = loader.load();
        contentArea.getChildren().removeAll();
        contentArea.getChildren().setAll(root);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            DataHolder.setDataInstance(MOHService.getMOH());
            viewAllRecipients();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    // View All Recipients
    public void viewAllRecipients() throws IOException {
        changeContentDisplayed("view-all-recipients.fxml");
    }

    // Distribute Vaccines
    public void distributeVaccines() throws IOException {
        changeContentDisplayed("distribute-vaccines.fxml");
    }

    // Distribute Recipients
    public void distributeRecipientsToVC() throws IOException {
        changeContentDisplayed("distribute-recipients.fxml");
    }

    // View Statistics
    public void viewStatistics() throws IOException {
        changeContentDisplayed("view-statistics.fxml");
    }

    // Refresh
    @FXML
    private ProgressIndicator piRefresh;

    @FXML
    private void refresh() throws SQLException, IOException {
        viewAllRecipients();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                piRefresh.setVisible(true);
                DataHolder.setDataInstance(MOHService.getMOH());
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            piRefresh.setVisible(false);
            try {
                createInfoBox();
                viewAllRecipients();
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