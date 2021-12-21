package controller;

import java.io.IOException;
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
import org.msjth.model.service.MOHService;

@SuppressWarnings("all")

public class ViewStatisticsController implements Initializable {
    private MOH moh = (MOH) DataHolder.getDataInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            viewStatistics();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private Label lblTotalGivenToVCs, lblTotalVax, lblNoOfVaccinesLeft, lblFullyVaxed;
    @FXML
    private DatePicker dpForTotalByDay;
    @FXML
    private ChoiceBox<String> cbMonths;

    ObservableList<String> availableChoices = FXCollections.observableArrayList("01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12");

    @FXML
    private void viewStatistics() throws SQLException {
        String totalVaxGiven = String.valueOf(MOHService.getTotalVaccinesDistributed());
        String totalVax = String.valueOf(MOHService.getTotalVax());
        String numVaccinesLeft = String.valueOf(moh.getVaccines().size());
        lblTotalGivenToVCs.setText(totalVaxGiven);
        lblTotalVax.setText(totalVax);
        lblNoOfVaccinesLeft.setText(numVaccinesLeft);
        lblFullyVaxed.setText(getPercentOfFullyVax());
        cbMonths.setItems(availableChoices);
    }

    private String getPercentOfFullyVax() {
        int paxOfFullyVax = 0;
        for (Recipient r: moh.getRecipients()) {
            if (r.getStatus() == 5)
                paxOfFullyVax++;
        }
        return String.format("%.1f", (paxOfFullyVax/200.0) * 100) + "%";
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
    private void viewTotalVaxByDay() throws IOException, InterruptedException, SQLException {
        if (dpForTotalByDay.getValue() != null) {
            String totalVaxGivenByDay = String.valueOf(MOHService.getTotalVaxGivenByDay(dpForTotalByDay.getValue()));
            createInfoBox("Total Vaccines Given on ", totalVaxGivenByDay, dpForTotalByDay);
        } else {
            createWarningBox("Please Select a Date!");
        }
    }

    @FXML
    private void viewTotalVaxByMonth() throws IOException, InterruptedException, SQLException {
        if (cbMonths.getValue() != null) {
            int selectedChoice = Integer.parseInt(cbMonths.getValue());
            String totalVaxGivenByMonth = String.valueOf(MOHService.getTotalVaccinesGivenByMonth(selectedChoice));
            createInfoBox("Total Vaccines Given in ", totalVaxGivenByMonth, cbMonths);
        } else {
            createWarningBox("Please Select a Month!");
        }
    }
}