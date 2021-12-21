package controller;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import model.RecipientV;
import org.msjth.model.*;
import org.msjth.model.service.VCService;

public class SetAppointmentController implements Initializable {
    private VC vc = (VC) DataHolder.getDataInstance();
    private ArrayList<RecipientV> recipientVs = new ArrayList<>();
    private ObservableList<RecipientV> data;

    public SetAppointmentController() {
        selectRecipients();
    }

    // convert recipient into recipientv which is as a wrapper of recipient
    private void selectRecipients() {
        ArrayList<Recipient> recipients = vc.getRecipients();
        for (Recipient recipient : recipients)
            recipientVs.add(new RecipientV(recipient));
        data = FXCollections.observableArrayList(recipientVs);
    }

    @FXML
    private Label lblCapPerDay, lblNoOfVaccines;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblCapPerDay.setText(String.valueOf(vc.getCapPerDay()));
        lblNoOfVaccines.setText(String.valueOf(calculateReservedVaccinesLeft()));
        setAppointmentTable();
        setDatePicker();
    }

    @FXML
    private TableView<RecipientV> tbAppointment;
    @FXML
    private TableColumn<RecipientV, String> colName, colStatus, colFirstDose, colSecondDose;
    @FXML
    private TableColumn<RecipientV, CheckBox> colSelect;

    @FXML
    private void setAppointmentTable() {
        // name col
        colName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRecipient().getName()));

        // status col
        colStatus.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getRecipient().getStatusDescription()));

        // 1st dose
        colFirstDose.setCellValueFactory(cellData -> {
            String dateString = cellData.getValue().getRecipient().getDoseXAppointmentDateString(1);
            if (dateString.equals(""))
                dateString = "NA";
            return new SimpleStringProperty(dateString);
        });

        // 2nd dose
        colSecondDose.setCellValueFactory(cellData -> {
            String dateString = cellData.getValue().getRecipient().getDoseXAppointmentDateString(2);
            if (dateString.equals(""))
                dateString = "NA";
            return new SimpleStringProperty(dateString);
        });

        // check box
        colSelect.setCellValueFactory(new PropertyValueFactory<>("selected"));

        tbAppointment.setItems(data);
    }

    @FXML
    private DatePicker dpFirstDose, dpSecondDose;
    @FXML
    private Label lblMessage;

    private void setDatePicker() {
        // Disable selection for date before tomorrow
        Callback<DatePicker, DateCell> dayCellCallback = new Callback<DatePicker, DateCell>() {
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isBefore(LocalDate.now().plusDays(1))) {
                            setDisable(true);
                        }
                    }
                };
            }
        };
        dpFirstDose.setDayCellFactory(dayCellCallback);
        dpSecondDose.setDayCellFactory(dayCellCallback);
    }

    private int getNumberOfCheckboxes() {
        int count = 0;
        for (RecipientV recipientV : data)
            if (recipientV.getSelected().isSelected())
                count++;
        return count;
    }

    private int calculateReservedVaccinesLeft() {
        int count = 0;
        for (RecipientV recipientv: recipientVs) {
            if (recipientv.getRecipient().getAppointments() != null)
                if (recipientv.getRecipient().getStatus() == 2 || recipientv.getRecipient().getStatus() == 4)
                    count++;
        }
        return vc.getVaccines().size() - count;
    }

    private void setAppointment(LocalDate date) throws SQLException {
        int totalSelectedRecipients = getNumberOfCheckboxes();
        String errorMessage = "";
        if (date == null)
            errorMessage = "Please select a date";
        else if (totalSelectedRecipients == 0)
            errorMessage = "No Checkbox selected";
        else if (totalSelectedRecipients > VCService.getCapacityLeftByVCByDate(vc, date))
            errorMessage = "Too many recipients on same day, please distribute some to another day.";
        else if (totalSelectedRecipients > calculateReservedVaccinesLeft())
            errorMessage = "Insufficient amount of vaccines to be given to recipient";
        else {
            try {
                for (RecipientV recipientV : data)
                    if (recipientV.getSelected().isSelected()) {
                        recipientV.getSelected().setSelected(false);
                        VCService.updateRecipientAppointment(recipientV.getRecipient(), date);
                    }
                DataHolder.setDataInstance(VCService.getVCByUsernameAndPassword(vc.getUsername(), vc.getPassword()));
                tbAppointment.refresh();
                lblNoOfVaccines.setText(String.valueOf(calculateReservedVaccinesLeft()));
                lblMessage.setTextFill(Color.GREEN);
                lblMessage.setText("Successfully set!");
                dpFirstDose.setValue(null);
                dpSecondDose.setValue(null);
            } catch (IllegalArgumentException e) {
                errorMessage = e.getMessage();
            }
        }
        if (!errorMessage.equals("")) {
            lblMessage.setTextFill(Color.RED);
            lblMessage.setText(errorMessage);
        }
    }

    @FXML
    private void setFirstAppointment() throws SQLException {
        setAppointment(dpFirstDose.getValue());
    }

    @FXML
    private void setSecondAppointment() throws SQLException {
        setAppointment(dpSecondDose.getValue());
    }
}