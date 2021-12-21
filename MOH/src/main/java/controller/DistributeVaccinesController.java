package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.msjth.model.*;
import org.msjth.model.service.MOHService;


public class DistributeVaccinesController implements Initializable {
    private MOH moh = (MOH) DataHolder.getDataInstance();
    private ArrayList<VC> VCs = moh.getVcs();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            viewVCsDatas();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Data Fields for KLCC VC
    @FXML
    private Label lblKLCCRecipients, lblKLCCCapacity, lblKLCCMessage;
    @FXML
    private TextField tfAmountForKLCC;

    // Data Fields for MMU VC
    @FXML
    private Label lblMMURecipients, lblMMUCapacity, lblMMUMessage;
    @FXML
    private TextField tfAmountForMMU;

    @FXML
    private void viewVCsDatas() throws IOException, InterruptedException {
        for (VC vc : VCs) {
            String numOfRecipients = String.valueOf(vc.getRecipients().size());
            String capacityOfVC = String.valueOf(vc.getCapPerDay());
            if (vc.getName().equals("KLCC")) {
                lblKLCCRecipients.setText(numOfRecipients);
                lblKLCCCapacity.setText(capacityOfVC);
            } else {
                lblMMURecipients.setText(numOfRecipients);
                lblMMUCapacity.setText(capacityOfVC);
            }
        }
    }

    private boolean isNumericInput(String input) {
        for (int i = 0; i < input.length(); i++)
            if (!Character.isDigit(input.charAt(i)))
                return false;
        return true;
    }

    @FXML
    private ProgressIndicator piDistVaxKLCC, piDistVaxMMU;

    @FXML
    private void distributeVaccinesToKLCC() throws SQLException {
        String input = tfAmountForKLCC.getText();
        if (input.isBlank()) {
            lblKLCCMessage.setTextFill(Color.RED);
            lblKLCCMessage.setText("Please enter amount to distribute.");
        } else if (!input.isBlank() && isNumericInput(input)) {
            MOHService.distributeVaccines(moh.getVcs().get(0), Integer.parseInt(input));
            lblKLCCMessage.setTextFill(Color.GREEN);
            lblKLCCMessage.setText("Successfully Distributed");
//            Task<Boolean> task = new Task<>() {
//                @Override
//                protected Boolean call() throws Exception {
//                    piDistVaxKLCC.setVisible(true);
//                    return isDistributed(VCs.get(0), input);
//                }
//            };
//            task.setOnSucceeded(e -> {
//                piDistVaxKLCC.setVisible(false);

//            });
//            new Thread(task).start();
        } else {
            lblKLCCMessage.setTextFill(Color.RED);
            lblKLCCMessage.setText("Please enter only numeric character.");
        }
        tfAmountForKLCC.clear();
    }

    @FXML
    private void distributeVaccinesToMMU() throws SQLException{
        String input = tfAmountForMMU.getText();
        if (input.isBlank()) {
            lblMMUMessage.setTextFill(Color.RED);
            lblMMUMessage.setText("Please enter amount to distribute.");
        } else if (!input.isBlank() && isNumericInput(input)) {
            MOHService.distributeVaccines(moh.getVcs().get(0), Integer.parseInt(input));
            lblMMUMessage.setTextFill(Color.GREEN);
            lblMMUMessage.setText("Successfully Distributed");
//            Task<Boolean> task = new Task<>() {
//                @Override
//                protected Boolean call() throws Exception {
//                    piDistVaxMMU.setVisible(true);
//                    return isDistributed(VCs.get(1), input);
//                }
//            };
//            task.setOnSucceeded(e -> {
//                piDistVaxMMU.setVisible(false);
//                lblMMUMessage.setTextFill(Color.GREEN);
//                lblMMUMessage.setText("Successfully Distributed");
//            });
//            new Thread(task).start();
        } else {
            lblMMUMessage.setTextFill(Color.RED);
            lblMMUMessage.setText("Please enter only numeric character.");
        }
        tfAmountForMMU.clear();
    }

//    private boolean isDistributed(VC vc, String input) throws IOException, InterruptedException {
//        //moh.distributeVaccines(vc, Integer.parseInt(input));
//        return true;
//    }
}