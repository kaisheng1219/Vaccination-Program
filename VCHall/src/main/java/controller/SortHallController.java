package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Stack;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import model.CustomButton;
import model.*;
import org.msjth.model.*;

@SuppressWarnings("all")

public class SortHallController implements Initializable {
    private VCHallSimulator vcHall = (VCHallSimulator) DataHolder.getDataInstance();
    private Queue<Recipient> seniorQueue;
    private Queue<Recipient> normalQueue;
    private Stack<Vaccine> vaccines;

    @FXML
    private Label lblDate, lblVCName;

    @FXML
    private GridPane gpSeniorQueue, gpNormalQueue, gpVaccine;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        lblVCName.setText(vcHall.getVc().getName() + " Hall");
        lblDate.setText(vcHall.getTargetDate().toString());
        seniorQueue = vcHall.getSeniorQueue();
        normalQueue = vcHall.getNormalQueue();
        vaccines = vcHall.getVaccineStack();
        setGridElements();
    }

    private void drawSeniorQueue() {
        gpSeniorQueue.getChildren().clear();
        int count = 0;
        for (Recipient recipient : seniorQueue) {
            String recepientInfo = recipient.getName() + "\n" + recipient.getAge();
            gpSeniorQueue.add(new CustomButton(recepientInfo), count++, 0);
        }
    }

    private void drawNormalQueue() {
        gpNormalQueue.getChildren().clear();
        int count = 0;
        for (Recipient recipient : normalQueue) {
            String recepientInfo = recipient.getName() + "\n" + recipient.getAge();
            gpNormalQueue.add(new CustomButton(recepientInfo), count++, 0);
        }
    }

    private void drawVaccineStack() {
        gpVaccine.getChildren().clear();
        for (int i = 0; i < vaccines.size(); i++) {
            String info = String.valueOf(vaccines.get(i).getBatchNumber());
            gpVaccine.add(new CustomButton(info), i, 0);
        }
    }

    private void setGridElements() {
        drawSeniorQueue();
        drawNormalQueue();
        drawVaccineStack();
    }

    @FXML
    private Button btnNext;

    public void showNextScene() throws IOException {
        vcHall.splitRecipients();
        goToNextScene("exec-hall.fxml", btnNext);
    }

    private void goToNextScene(String fxml, Control control) throws IOException {
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxml));
        Parent root = loader.load();

        Scene scene = control.getScene();
        scene.setRoot(root);
    }
}
