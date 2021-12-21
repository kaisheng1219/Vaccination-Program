package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.msjth.model.*;

public class ViewAllRecipientsController implements Initializable {
    private MOH moh = (MOH) DataHolder.getDataInstance();
    private ArrayList<Recipient> recipients = moh.getRecipients();
    private final ObservableList<Recipient> data = FXCollections.observableArrayList(recipients);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewRecipients();
    }

    @FXML
    private TableView<Recipient> tbViewRecipients;
    @FXML
    private TableColumn<Recipient, String> colName, colPhone, colStatus;
    @FXML
    private TableColumn<Recipient, Integer> colAge;
    @FXML
    private TableColumn<Recipient, String> colVaccineCentre;
    @FXML
    private TableColumn<Recipient, String> colFirstDoseAppointment, colSecondDoseAppointment;

    private void viewRecipients() {
        // name col
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        // phone col
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNo"));

        // age col
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));

        // status col
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusDescription()));

        // 1st dose
        colFirstDoseAppointment.setCellValueFactory(cellData -> {
            String dateString = cellData.getValue().getDoseXAppointmentDateString(1);
            return new SimpleStringProperty(dateString);
        });

        // 2nd dose
        colSecondDoseAppointment.setCellValueFactory(cellData -> {
            String dateString = cellData.getValue().getDoseXAppointmentDateString(2);
            return new SimpleStringProperty(dateString);
        });

        // Vaccine Centre
        colVaccineCentre.setCellValueFactory(new PropertyValueFactory<>("allocatedVaccineCenter"));

        tbViewRecipients.setItems(data);
    }
}