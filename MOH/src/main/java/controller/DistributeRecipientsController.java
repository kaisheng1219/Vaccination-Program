package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import model.RecipientV;
import org.msjth.model.*;
import org.msjth.model.service.MOHService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

@SuppressWarnings("all")

public class DistributeRecipientsController implements Initializable {
    private MOH moh = (MOH) DataHolder.getDataInstance();
    private ArrayList<RecipientV> undistributedRecipientVs = new ArrayList<>();
    private ObservableList<RecipientV> data;
    private ObservableList<String> availableChoices;

    // convert recipient into recipientv which is as a wrapper of recipient
    private void selectUndistributedRecipients() {
        ArrayList<Recipient> recipients = moh.getRecipients();

        for (Recipient recipient : recipients)
            if (recipient.getAllocatedVaccineCenter() == null || recipient.getAllocatedVaccineCenter().equals("NA"))
                undistributedRecipientVs.add(new RecipientV(recipient));

        data = FXCollections.observableArrayList(undistributedRecipientVs);
    }

    @FXML
    private ChoiceBox<String> cbVCs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        availableChoices = FXCollections.observableArrayList("KLCC", "MMU");
        selectUndistributedRecipients();
        cbVCs.setItems(availableChoices);
        distributeRecipientsTable();
    }

    @FXML
    private TableView<RecipientV> tbViewRecipients;
    @FXML
    private TableColumn<RecipientV, String> colName, colPhone, colStatus, colAllocatedVaccineCenter;
    @FXML
    private TableColumn<RecipientV, CheckBox> colSelect;

    @FXML
    private void distributeRecipientsTable() {
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRecipient().getName()));
        colPhone.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getRecipient().getPhoneNo()));
        colStatus.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getRecipient().getStatusDescription()));
        colAllocatedVaccineCenter.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getRecipient().getAllocatedVaccineCenter()));
        colSelect.setCellValueFactory(new PropertyValueFactory<>("selected"));
        tbViewRecipients.setItems(data);
    }

    @FXML
    private CheckBox cbSelect50Recipients;

    @FXML
    private void selectRecipients() {
        for (RecipientV recipientv: tbViewRecipients.getItems())
            recipientv.getSelected().setSelected(false);
        int prefixedSize = 50;
        if (undistributedRecipientVs.size() < 50)
            prefixedSize = undistributedRecipientVs.size();
        for (int i = 0; i < prefixedSize; i++)
            tbViewRecipients.getItems().get(i).getSelected().setSelected(cbSelect50Recipients.isSelected());
    }

    private int getNumberOfCheckboxes() {
        int count = 0;
        for (RecipientV recipient : data)
            if (recipient.getSelected().isSelected())
                count++;
        return count;
    }

    @FXML
    private Label lblMessage;
    @FXML
    private ProgressIndicator piDistRecipients;

    @FXML
    private void distribute() throws IOException, InterruptedException, SQLException {
        if (cbVCs.getValue() == null) {
            lblMessage.setTextFill(Color.RED);
            lblMessage.setText("Please select a VC");
        } else if (getNumberOfCheckboxes() < 1) {
            lblMessage.setTextFill(Color.RED);
            lblMessage.setText("No Checkbox Selected.");
        } else if (cbVCs.getValue() != null) {
            ArrayList<Recipient> distributedRecipients = new ArrayList<>();
            ArrayList<RecipientV> distributedRecipientVs = new ArrayList<>();

            for (RecipientV recipientV : data) {
                if (recipientV.getSelected().isSelected()) {
                    distributedRecipients.add(recipientV.getRecipient());
                    distributedRecipientVs.add(recipientV);
                }
            }
            tbViewRecipients.getItems().removeAll(distributedRecipientVs);
            tbViewRecipients.refresh();

            VC vc = moh.getVcs().stream().filter(e -> cbVCs.getValue().equals(e.getName())).findAny().orElse(null);
            MOHService.distributeRecipientsToVC(vc, distributedRecipients);
            lblMessage.setTextFill(Color.GREEN);
            lblMessage.setText("Successfully Distributed Recipients");
//            Task<Boolean> task = new Task<>() {
//                @Override
//                protected Boolean call() throws Exception {
//                    piDistRecipients.setVisible(true);
//                    return isDistributed(distributedRecipients);
//                }
//            };
//            task.setOnSucceeded(e -> {
//                piDistRecipients.setVisible(false);
//                cbVCs.setValue(null);
//                cbSelect50Recipients.setSelected(false);

//            });
//            new Thread(task).start();
        } else {
            lblMessage.setTextFill(Color.RED);
            lblMessage.setText("Action Not Allowed");
        }
    }

//    private boolean isDistributed(ArrayList<Recipient> distributedRecipients) throws IOException, InterruptedException {
//        //moh.distributeRecipientsToVC(cbVCs.getValue(), distributedRecipients);
//        return true;
//    }
}