package org.msjth.model.service;

import org.msjth.dbHelper.DbUtil;
import org.msjth.model.*;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


@SuppressWarnings("all")
public class VCService {

    public static ArrayList<VC> getVCs() throws SQLException {
        ArrayList<VC> vcs = new ArrayList<>();
        Connection conn = DbUtil.getConnection();
        ResultSet VCsData = conn.createStatement().executeQuery("SELECT * FROM VaccineCenters");
        while (VCsData.next()) {
            int vcId = VCsData.getInt("vc_id");
            String vcUsername = VCsData.getString("vc_username");
            String vcPassword = VCsData.getString("vc_password");
            String vcName = VCsData.getString("vc_name");
            int vcCapPerDay = VCsData.getInt("vc_capperday");
            vcs.add(new VC(vcUsername, vcPassword, vcName, vcCapPerDay, RecipientService.getRecipientsByVC(vcId), VaccineService.getVaccinesByVCId(vcId)));
        }
        return vcs;
    }

    public static VC getVCById(int vcId) throws SQLException {
        Connection conn = DbUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM VaccineCenters WHERE VC_ID = ?");
        ps.setInt(1, vcId);
        ResultSet VCsData = ps.executeQuery();

        if (VCsData.getRow() == 1) {
            String vcUsername = VCsData.getString("vc_username");
            String vcPassword = VCsData.getString("vc_password");
            String vcName = VCsData.getString("vc_name");
            int vcCapPerDay = VCsData.getInt("vc_capperday");
            return new VC(vcUsername, vcPassword, vcName, vcCapPerDay, RecipientService.getRecipientsByVC(vcId), VaccineService.getVaccinesByVCId(vcId));
        }
        return null;
    }

    public static VC getVCByUsernameAndPassword(String username, String password) throws SQLException {
        Connection conn = DbUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM VaccineCenters WHERE VC_Username = ? AND VC_Password = ?");
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet VCsData = ps.executeQuery();

        if (VCsData.next()) {
            int vcId = VCsData.getInt("vc_id");
            String vcUsername = VCsData.getString("vc_username");
            String vcPassword = VCsData.getString("vc_password");
            String vcName = VCsData.getString("vc_name");
            int vcCapPerDay = VCsData.getInt("vc_capperday");
            return new VC(vcUsername, vcPassword, vcName, vcCapPerDay, RecipientService.getRecipientsByVC(vcId), VaccineService.getVaccinesByVCId(vcId));
        }
        return null;
    }

    public static String getVCNameById (int vcId) throws SQLException {
        Connection conn = DbUtil.getConnection();
        ResultSet stmp = conn.createStatement()
                        .executeQuery("SELECT vc_name FROM VaccineCenters WHERE vc_id = " + vcId);
        while (stmp.next())
            return stmp.getString("vc_name");
        return "NA";
    }

    public static Integer getVCIdByName (String vcName) throws SQLException {
        vcName = vcName.toUpperCase();
        Connection conn = DbUtil.getConnection();
        ResultSet stmp = conn.createStatement()
                .executeQuery("SELECT vc_id FROM VaccineCenters WHERE vc_name = " + "'" +  vcName + "'");
        while (stmp.next())
            return stmp.getInt("vc_id");
        return null;
    }

    public static int getCapacityLeftByVCByDate(VC vc, LocalDate date) throws SQLException {
        ArrayList<Appointment> appointmentsByVC = AppointmentService.getAppointmentsByVC(vc);
        int capByDay = vc.getCapPerDay();
        for (Appointment a : appointmentsByVC)
            if (a.getAppointmentDate().equals(date))
                capByDay--;
        return capByDay;
    }

    public static int getTotalDosesGivenByVCs() throws SQLException {
        String query = "SELECT COUNT(vaccine_batchno) FROM UsedVaccinesView";
        Connection conn = DbUtil.getConnection();
        ResultSet rs = conn.createStatement().executeQuery(query);
        rs.next();
        return rs.getInt(1);
    }

    public static int getTotalDosesGivenByVC(VC vc) throws SQLException {
        String query = "SELECT COUNT(vaccine_batchno) FROM UsedVaccinesView WHERE VC_ID = " + getVCIdByName(vc.getName());
        Connection conn = DbUtil.getConnection();
        ResultSet rs = conn.createStatement().executeQuery(query);
        rs.next();
        return rs.getInt(1);
    }

    public static int getTotalDosesGivenByVCsOnDay(LocalDate date) throws SQLException {
        String query = "SELECT COUNT(vaccine_batchno) FROM UsedVaccinesView WHERE Appointment_Date = " + "'" + date.toString() + "'";
        Connection conn = DbUtil.getConnection();
        ResultSet rs = conn.createStatement().executeQuery(query);
        rs.next();
        return rs.getInt(1);
    }

    public static int getTotalDosesGivenByVCOnDay(VC vc, LocalDate date) throws SQLException {
        String query = "SELECT COUNT(vaccine_batchno) FROM UsedVaccinesView WHERE VC_ID = ? AND Appointment_Date = ?";
        Connection conn = DbUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, getVCIdByName(vc.getName()));
        ps.setDate(2, Date.valueOf(date));
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public static int getTotalDosesGivenByVCsInMonth(int targetMonth) throws SQLException {
        String query = "SELECT COUNT(vaccine_batchno) FROM UsedVaccinesView WHERE EXTRACT(MONTH FROM Appointment_Date) = " + targetMonth;
        Connection conn = DbUtil.getConnection();
        ResultSet rs = conn.createStatement().executeQuery(query);
        rs.next();
        return rs.getInt(1);
    }

    public static int getTotalDosesGivenByVCInMonth(VC vc, int targetMonth) throws SQLException {
        String query = "SELECT COUNT(vaccine_batchno) FROM UsedVaccinesView WHERE VC_ID = ? AND EXTRACT(MONTH FROM Appointment_Date) = ?";
        Connection conn = DbUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, getVCIdByName(vc.getName()));
        ps.setInt(2, targetMonth);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt(1);
    }

    public static void updateRecipientAppointment(Recipient recipient, LocalDate date) throws SQLException {
        if (recipient.getAppointments() != null &&
            recipient.getAppointments().stream().filter(e -> e.getAppointmentDate() != null && e.getAppointmentDate().equals(date)).findAny().orElse(null) != null)
            throw new IllegalArgumentException("Action not allowed");
        String query = "INSERT INTO Appointments (Appointment_Date, Vaccine_BatchNo, Recipient_ID) VALUES (?, NULL, ?)";
        Connection conn = DbUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setDate(1, Date.valueOf(date));
        ps.setInt(2, RecipientService.getRecipientId(recipient.getUsername()));
        ps.executeUpdate();
    }

    public static void updateRecipientAppointment(Recipient recipient, Vaccine vaccine) throws SQLException {
        String query = "UPDATE appointments SET vaccine_batchno = ? WHERE recipient_id = ? and vaccine_batchno IS NULL";
        Connection conn = DbUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, vaccine.getBatchNumber());
        ps.setInt(2, RecipientService.getRecipientId(recipient.getUsername()));
        ps.executeUpdate();
    }
}