package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Stack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.CustomButton;
import model.*;
import org.msjth.model.*;

@SuppressWarnings("all")

public class UnsortHallController implements Initializable {
    private VCHallSimulator vcHall = (VCHallSimulator) DataHolder.getDataInstance();
    @FXML
    private Label lblVCName;
    @FXML
    private AnchorPane contentArea;
    @FXML
    private DatePicker dpTargetDate;
    @FXML
    private GridPane gpRecipient, gpVaccine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblVCName.setText(vcHall.getVc().getName() + " Hall");
    }

    public void onDateSelected() throws IOException, InterruptedException, SQLException {
        vcHall.setTargetDate(dpTargetDate.getValue());
        ArrayList<Recipient> recipients = vcHall.getRecipients();
        if (recipients == null || recipients.size() == 0)
            createWarningBox("No Recipients on " + dpTargetDate.getValue());
        else {
            Stack<Vaccine> vaccines = new Stack<>();
            int count = 0;
            for (int i = 0; i < recipients.size(); i++) {
                String recepientInfo = recipients.get(i).getName() + "\n" + recipients.get(i).getAge();
                gpRecipient.add(new CustomButton(recepientInfo), i, 0);
                count++;
            }
            // only show the vaccines according to the number of recipient on that day
            Stack<Vaccine> vaccinesLeft = vcHall.getVaccineStack();
            for (int i = 0; i < count; i++) {
                String info = String.valueOf(vaccinesLeft.get(i).getBatchNumber());
                gpVaccine.add(new CustomButton(info), i, 0);
            }
        }
    }

    @FXML
    private Button btnNext;

    public void showNextScene() throws IOException {
        goToNextScene("sort-hall.fxml", btnNext);
    }

    private void createWarningBox(String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.OK);
        alert.getDialogPane().setPrefSize(400, 100);
        alert.getDialogPane().setHeaderText("Warning");
        ;

        Label temp = new Label();
        temp.setFont(new Font("Arial", 25));
        temp.setPrefSize(700, 50);
        temp.setTextFill(Color.RED);
        temp.setAlignment(Pos.CENTER);
        temp.setText(content);
        alert.getDialogPane().setContent(temp);
        alert.show();
    }

    private void goToNextScene(String fxml, Control control) throws IOException {
        if (dpTargetDate.getValue() == null)
            createWarningBox("No Date Selected");
        else if (vcHall.getRecipients().size() == 0 || vcHall.getRecipients() == null)
            createWarningBox("No Recipients Taking Dose");
        else {
            vcHall.splitRecipients();
            FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxml));
            Parent root = loader.load();
            Scene scene = btnNext.getScene();
            scene.setRoot(root);
        }
    }
}
