package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.*;
import org.msjth.model.*;
import org.msjth.model.service.VCService;

@SuppressWarnings("all")

public class ExecHallController implements Initializable {

    private VCHallSimulator vcHall = (VCHallSimulator) DataHolder.getDataInstance();
    private Map<Recipient, Vaccine> vaccinated = new LinkedHashMap<>();
    private Queue<Recipient> seniorQueue;
    private Queue<Recipient> normalQueue;
    private Stack<Vaccine> vaccines;
    private int counter;

    @FXML
    private Label lblDate, lblVCName;

    @FXML
    private GridPane gpSeniorQueue, gpNormalQueue, gpVaccine, gpVaccinatedRecipient;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        lblVCName.setText(vcHall.getVc().getName() + " Hall");
        seniorQueue = vcHall.getSeniorQueue();
        normalQueue = vcHall.getNormalQueue();
        setVaccines();
        lblDate.setText(vcHall.getTargetDate().toString());
        try {
            setGridElements();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setVaccines() {
        vaccines = new Stack<>();
        Stack<Vaccine> vaccinesLeft = vcHall.getVaccineStack();
        int count = 0;
        if (seniorQueue != null)
            count += seniorQueue.size();
        if (normalQueue != null)
            count += normalQueue.size();

        for (int i = 0; i < count; i++)
            vaccines.add(vaccinesLeft.get(i));
    }

    private void drawSeniorQueue() {
        gpSeniorQueue.getChildren().clear();
        int count = 0;
        for (Recipient recipient : seniorQueue) {
            String recipientInfo = recipient.getName() + "\n" + recipient.getAge();
            gpSeniorQueue.add(new CustomButton(recipientInfo), count++, 0);
        }
    }

    private void drawNormalQueue() {
        gpNormalQueue.getChildren().clear();
        int count = 0;
        for (Recipient recipient : normalQueue) {
            String recipientInfo = recipient.getName() + "\n" + recipient.getAge();
            gpNormalQueue.add(new CustomButton(recipientInfo), count++, 0);
        }
    }

    private void drawVaccineStack() {
        gpVaccine.getChildren().clear();
        for (int i = 0; i < vaccines.size(); i++) {
            String info = String.valueOf(vaccines.get(i).getBatchNumber());
            gpVaccine.add(new CustomButton(info), i, 0);
        }
    }

    private void drawVaccinated() {
        int count = 0;
        for (var recipient : vaccinated.keySet()) {
            String recipientInfo = recipient.getName() + "\n" + recipient.getAge() + "\n"
                    + vaccinated.get(recipient).getBatchNumber();
            gpVaccinatedRecipient.add(new CustomButton(recipientInfo), count++, 0);
        }
    }

    private void setGridElements() throws IOException, InterruptedException {
        drawSeniorQueue();
        drawNormalQueue();
        drawVaccineStack();
        drawVaccinated();
    }

    @FXML
    private Button btnNext;
    @FXML
    private Label lblSaving;
    @FXML
    private ProgressIndicator piSaving;

    public void simulate() throws IOException, InterruptedException, SQLException {
        if (seniorQueue.isEmpty() && normalQueue.isEmpty()) {
            if (counter == 0)
                informDone();
            else
                restart();
            return;
        }
        if (seniorQueue.isEmpty()) {
            VCService.updateRecipientAppointment(normalQueue.peek(), vaccines.peek());
            vaccinated.put(normalQueue.remove(), vaccines.pop());
        } else if (normalQueue.isEmpty()) {
            VCService.updateRecipientAppointment(seniorQueue.peek(), vaccines.peek());
            vaccinated.put(seniorQueue.remove(), vaccines.pop());
        } else {
            VCService.updateRecipientAppointment(seniorQueue.peek(), vaccines.peek());
            vaccinated.put(seniorQueue.remove(), vaccines.pop());
            VCService.updateRecipientAppointment(normalQueue.peek(), vaccines.peek());
            vaccinated.put(normalQueue.remove(), vaccines.pop());
        }
        setGridElements();
    }

    private void informDone() throws IOException {
        createInfoBox();
        counter++;
    }

    private void restart() throws IOException {
        goToNextScene("select-hall.fxml", btnNext);
    }

    private void createInfoBox() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        alert.getDialogPane().setPrefSize(400, 100);
        alert.getDialogPane().setHeaderText("Program Status");

        Label temp = new Label();
        temp.setFont(new Font("Arial", 25));
        temp.setPrefSize(400, 50);
        temp.setTextFill(Color.rgb(22, 3, 133));
        temp.setAlignment(Pos.CENTER);
        temp.setText("Successfully Saved!");
        alert.getDialogPane().setContent(temp);
        alert.show();
    }

    private void goToNextScene(String fxml, Control control) throws IOException {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxml));
        Parent root = loader.load();
        Scene scene = control.getScene();
        scene.setRoot(root);
    }
}
