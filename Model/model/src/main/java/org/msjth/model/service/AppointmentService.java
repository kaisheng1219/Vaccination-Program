package org.msjth.model.service;

import org.msjth.dbHelper.DbUtil;
import org.msjth.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("all")
public class AppointmentService {

    public static HashMap<Integer, ArrayList<Appointment>> getAppointments() throws SQLException {
        HashMap<Integer, ArrayList<Appointment>> appointments = new HashMap<>();
        Connection conn = DbUtil.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet appointmentsData = stmt.executeQuery("SELECT * FROM appointments");
        while (appointmentsData.next()) {
            LocalDate appointmentDate = appointmentsData.getDate("appointment_date").toLocalDate();
            int vaccineBatchNo = appointmentsData.getInt("vaccine_batchno");
            int recipientId = appointmentsData.getInt("recipient_id");
            if (!appointments.containsKey(recipientId))
                appointments.put(recipientId, new ArrayList<>(List.of(new Appointment(appointmentDate, new Vaccine(vaccineBatchNo)))));
            else {
                appointments.get(recipientId).add(new Appointment(appointmentDate, new Vaccine(vaccineBatchNo)));
            }
        }
        return appointments;
    }

    public static ArrayList<Appointment> getAppointmentsByVC(VC vc) throws SQLException {
        ArrayList<Appointment> appointments = new ArrayList<>();
        int vcID = VCService.getVCIdByName(vc.getName());
        Connection conn = DbUtil.getConnection();
        String query = "SELECT appointment_date, A.vaccine_BatchNo FROM Appointments A, Vaccines V WHERE A.vaccine_BatchNo = V.vaccine_BatchNo AND vc_ID = ?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, vcID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            LocalDate appointmentDate = rs.getDate("appointment_date").toLocalDate();
            int vaccineBatchNo = rs.getInt("vaccine_batchno");
            appointments.add(new Appointment(appointmentDate, new Vaccine(vaccineBatchNo)));
        }
        return appointments;
    }

    public static void updateAppointment(Recipient recipient, Object obj) throws SQLException {
        int recipientId = RecipientService.getRecipientId(recipient.getUsername());
        Connection conn = DbUtil.getConnection();
        PreparedStatement ps = null;
        if (obj instanceof LocalDate) {
            ps = conn.prepareStatement("UPDATE Appointments SET appointment_date = ? WHERE recipient_id = ? AND appointment_date IS NULL");
            ps.setDate(1, Date.valueOf((LocalDate) obj));
        }
        if (obj instanceof Vaccine) {
            ps = conn.prepareStatement("UPDATE Appointments SET vaccine_batchno = ? WHERE recipient_id = ? AND vaccine_batchno IS NULL");
            ps.setInt(1, ((Vaccine) obj).getBatchNumber());
        }
        ps.setInt(2, recipientId);
        ps.executeUpdate();
        ps.close();
    }
}