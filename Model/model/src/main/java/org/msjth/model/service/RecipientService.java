package org.msjth.model.service;

import org.msjth.dbHelper.DbUtil;
import org.msjth.model.*;

import java.sql.*;
import java.util.*;

@SuppressWarnings("all")

public class RecipientService {

    public static ArrayList<Recipient> getRecipients() throws SQLException {
        ArrayList<Recipient> recipients = new ArrayList<>();
        HashMap<Integer, ArrayList<Appointment>> appointments = AppointmentService.getAppointments();
        populateRecipients(recipients, appointments);
        return recipients;
    }

    public static ArrayList<Recipient> getRecipientsByVC(int VcId) throws SQLException {
        ArrayList<Recipient> recipients = new ArrayList<>();
        HashMap<Integer, ArrayList<Appointment>> appointments = AppointmentService.getAppointments();
        Connection conn = DbUtil.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet recipientsData = stmt.executeQuery("SELECT * FROM Recipients WHERE vc_id = " + VcId);
        while (recipientsData.next()) {
            int recipientId = recipientsData.getInt("recipient_id");
            String recipientUsername = recipientsData.getString("recipient_username");
            String recipientPassword = recipientsData.getString("recipient_password");
            String recipientName = recipientsData.getString("recipient_name");
            String recipientPhoneNo = recipientsData.getString("recipient_phoneno");
            int recipientStatus = recipientsData.getInt("recipient_status");
            int recipientAge = recipientsData.getInt("recipient_age");

            Recipient recipient = new Recipient(
                    recipientUsername,
                    recipientPassword,
                    recipientName,
                    recipientPhoneNo,
                    recipientAge,
                    recipientStatus,
                    VCService.getVCNameById(VcId),
                    null );
            if (appointments.containsKey(recipientId))
                recipient.setAppointments(appointments.get(recipientId));
            recipients.add(recipient);
        }
        return recipients;
    }

    public static int getRecipientId(String username) throws SQLException {
        Connection conn = DbUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT recipient_id FROM Recipients WHERE recipient_username = ?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            return rs.getInt("recipient_id");
        return -1;
    }

    private static void populateRecipients(ArrayList<Recipient> recipients, HashMap<Integer, ArrayList<Appointment>> appointments) throws SQLException {
        Connection conn = DbUtil.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet recipientsData = stmt.executeQuery("SELECT * FROM recipients");

        while (recipientsData.next()) {
            int recipientId = recipientsData.getInt("recipient_id");
            String recipientUsername = recipientsData.getString("recipient_username");
            String recipientPassword = recipientsData.getString("recipient_password");
            String recipientName = recipientsData.getString("recipient_name");
            String recipientPhoneNo = recipientsData.getString("recipient_phoneno");
            int recipientStatus = recipientsData.getInt("recipient_status");
            int recipientAge = recipientsData.getInt("recipient_age");
            int vcId = recipientsData.getInt("vc_id");

            Recipient recipient = new Recipient(
                    recipientUsername,
                    recipientPassword,
                    recipientName,
                    recipientPhoneNo,
                    recipientAge,
                    recipientStatus,
                    VCService.getVCNameById(vcId),
                    null);
            if (appointments.containsKey(recipientId))
                recipient.setAppointments(appointments.get(recipientId));
            recipients.add(recipient);
        }
    }
}