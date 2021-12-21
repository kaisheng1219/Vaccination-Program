package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import model.*;
import org.msjth.model.*;
import org.msjth.model.service.*;

@SuppressWarnings("all")

public class SelectHallController implements Initializable {
    // For switching to next scene without manually invoking any methods in another
    // controller
    private void goToNextScene(String fxml, Control control) throws IOException {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxml));
        Parent root = loader.load();
        Scene scene = control.getScene();
        scene.setRoot(root);
    }

    private final ObservableList<String> availableChoices;

    public SelectHallController() throws IOException, InterruptedException {
        availableChoices = FXCollections.observableArrayList("KLCC", "MMU");
    }

    @FXML
    private ChoiceBox<String> cbVCs;
    @FXML
    private ProgressIndicator piLoading;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbVCs.setItems(availableChoices);
    }

    @FXML
    private void go() throws IOException, InterruptedException, SQLException {
        if (cbVCs.getValue() == null)
            createWarningBox("No VC Selected");
        else {
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    piLoading.setVisible(true);
                    return getVCs();
                }
            };
            task.setOnSucceeded(e -> {
                try {
                    goToNextScene("unsort-hall.fxml", cbVCs);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            new Thread(task).start();
        }
    }

    private boolean getVCs() throws SQLException {
        ArrayList<VC> vcs = VCService.getVCs();
        for (VC vc : vcs) {
            if (vc.getName().equals(cbVCs.getValue())) {
                DataHolder.setDataInstance(new VCHallSimulator(vc));
                return true;
            }
        }
        return false;
    }

    private void createWarningBox(String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.OK);
        alert.getDialogPane().setPrefSize(400, 100);
        alert.getDialogPane().setHeaderText("Warning");;

        Label temp = new Label();
        temp.setFont(new Font("Arial", 25));
        temp.setPrefSize(700, 50);
        temp.setTextFill(Color.RED);
        temp.setAlignment(Pos.CENTER);
        temp.setText(content);
        alert.getDialogPane().setContent(temp);
        alert.show();
    }
}