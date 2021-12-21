package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import org.msjth.model.*;
import org.msjth.model.service.VCService;
import org.msjth.model.service.VaccineService;

@SuppressWarnings("all")

public class ViewStatisticsController implements Initializable {
    private VC vc = (VC) DataHolder.getDataInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            viewStatistics();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Label lblTotalGiven, lblTotalReceived, lblNoOfVaccinesLeft, lblFullyVax;
    @FXML
    private DatePicker dpForTotalByDay;
    @FXML
    private ChoiceBox<String> cbMonths;

    ObservableList<String> availableChoices = FXCollections.observableArrayList("01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12");

    @FXML
    private void viewStatistics() throws SQLException {
        String totalDosesGiven = String.valueOf(VCService.getTotalDosesGivenByVC(vc));
        String totalVaccinesReceived = String.valueOf(vc.getVaccines().size());
        String totalVaccinesLeft = String.valueOf(VaccineService.getTotalVaccinesLeftByVCId(VCService.getVCIdByName(vc.getName())).size());
        String percentOfFullyVaxed = getPercentOfFullyVaxRecipients();
        lblTotalGiven.setText(totalDosesGiven);
        lblTotalReceived.setText(totalVaccinesReceived);
        lblNoOfVaccinesLeft.setText(totalVaccinesLeft);
        lblFullyVax.setText(percentOfFullyVaxed);
        cbMonths.setItems(availableChoices);
    }

    private String getPercentOfFullyVaxRecipients() {
        int fullyVaxed = 0;
        int total = vc.getRecipients().size();
        for (Recipient recipient : vc.getRecipients())
            if (recipient.getStatus() == 5)
                fullyVaxed++;
        return String.format("%.1f", ((double)fullyVaxed/total) * 100) + "%";
    }

    private void createInfoBox(String title, String content, Control c) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", ButtonType.OK);
        alert.getDialogPane().setPrefSize(400, 100);

        if (c instanceof DatePicker)
            alert.getDialogPane().setHeaderText(title + ( (DatePicker) c ).getValue().toString());
        else
            alert.getDialogPane().setHeaderText(title + ( (ChoiceBox<String>) c ).getValue());

        Label temp = new Label();
        temp.setFont(new Font("Arial", 35));
        temp.setPrefSize(400, 50);
        temp.setTextFill(Color.rgb(22, 3, 133));
        temp.setAlignment(Pos.CENTER);
        temp.setText(content);
        alert.getDialogPane().setContent(temp);
        alert.show();
    }

    private void createWarningBox(String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING, "", ButtonType.OK);
        alert.getDialogPane().setPrefSize(500, 100);
        alert.getDialogPane().setHeaderText("Warning");;

        Label temp = new Label();
        temp.setFont(new Font("Arial", 35));
        temp.setPrefSize(500, 50);
        temp.setTextFill(Color.RED);
        temp.setAlignment(Pos.CENTER);
        temp.setText(content);
        alert.getDialogPane().setContent(temp);
        alert.show();
    }

    @FXML
    private void viewTotalVaxByDay() throws SQLException {
        if (dpForTotalByDay.getValue() != null) {
            String totalVaxGivenByDay = String.valueOf(VCService.getTotalDosesGivenByVCOnDay(vc, dpForTotalByDay.getValue()));
            createInfoBox("Total Doses Given on day ", totalVaxGivenByDay, dpForTotalByDay);
        }
        else {
            createWarningBox("Please Select a Date.");
        }
    }

    @FXML
    private void viewTotalVaxByMonth() throws SQLException {
        if (cbMonths.getValue() != null) {
            int selectedMonth = Integer.parseInt(cbMonths.getValue());
            String totalVaxGivenByMonth = String.valueOf(VCService.getTotalDosesGivenByVCInMonth(vc, selectedMonth));
            createInfoBox("Total Doses Given in month ", totalVaxGivenByMonth, cbMonths);
        }
        else {
            createWarningBox("Please Select a Month.");
        }
    }
}