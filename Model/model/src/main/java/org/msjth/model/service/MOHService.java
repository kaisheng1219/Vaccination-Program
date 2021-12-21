package org.msjth.model.service;

import org.msjth.dbHelper.DbUtil;
import org.msjth.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

@SuppressWarnings("all")

public class MOHService {

    public static MOH getMOH() throws SQLException {
        return new MOH(RecipientService.getRecipients(), VCService.getVCs(), VaccineService.getUndistributedVaccines());
    }

    public static boolean distributeVaccines(VC vc, int amount) throws SQLException {
        Connection conn = DbUtil.getConnection();
        if (!VaccineService.undistributedVaccinesLeftAmountGreaterThan(amount))
            throw new IllegalArgumentException("Insufficient Amount of Vaccines Left");
        String query = "CALL distributeVaccines(?, ?)";
        CallableStatement cs = conn.prepareCall(query);
        cs.setInt(1, VCService.getVCIdByName(vc.getName()));
        cs.setInt(2, amount);
        cs.execute();
        return true;
    }

    public static boolean distributeRecipientsToVC(VC vc, ArrayList<Recipient> recipients) throws SQLException {
        Connection conn = DbUtil.getConnection();
        for (Recipient recipient : recipients) {
            String query = "CALL distributeThisRecipientTo(?, ?)";
            CallableStatement cs = conn.prepareCall(query);
            cs.setInt(1, RecipientService.getRecipientId(recipient.getUsername()));
            cs.setInt(2, VCService.getVCIdByName(vc.getName()));
            cs.execute();
        }
        return true;
    }

    public static int getTotalVaccinesDistributed() throws SQLException {
        Connection conn = DbUtil.getConnection();
        String query = "SELECT COUNT(Vaccine_BatchNo) FROM Vaccines WHERE VC_ID IS NOT NULL";
        ResultSet rs = conn.createStatement().executeQuery(query);
        rs.next();
        return rs.getInt(1);
    }

    public static int getTotalVax() throws SQLException {
        return VCService.getTotalDosesGivenByVCs();
    }

    public static int getTotalVaxGivenByDay(LocalDate date) throws SQLException {
        return VCService.getTotalDosesGivenByVCsOnDay(date);
    }

    public static int getTotalVaccinesGivenByMonth(int targetMonth) throws SQLException {
        return VCService.getTotalDosesGivenByVCsInMonth(targetMonth);
    }
}