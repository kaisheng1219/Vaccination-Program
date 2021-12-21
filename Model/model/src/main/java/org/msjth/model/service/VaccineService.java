package org.msjth.model.service;

import org.msjth.dbHelper.DbUtil;
import org.msjth.model.*;

import java.sql.*;
import java.util.ArrayList;

@SuppressWarnings("all")

public class VaccineService {

    public static ArrayList<Vaccine> getUndistributedVaccines() throws SQLException {
        ArrayList<Vaccine> vaccines = new ArrayList<>();
        String query = "SELECT * FROM Vaccines WHERE VC_ID IS NULL";
        Connection conn = DbUtil.getConnection();
        ResultSet rs = conn.createStatement().executeQuery(query);
        while (rs.next()) {
            int vaccineBatchNo = rs.getInt("vaccine_batchno");
            vaccines.add(new Vaccine(vaccineBatchNo));
        }
        return vaccines;
    }

    public static ArrayList<Vaccine> getVaccinesByVCId(int vcId) throws SQLException {
        ArrayList<Vaccine> vaccinesOfVC = new ArrayList<>();
        String query = "SELECT vaccine_batchno FROM Vaccines WHERE VC_ID = ?";
        Connection conn = DbUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, vcId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            vaccinesOfVC.add(new Vaccine(rs.getInt(1)));
        return vaccinesOfVC;
    }

    public static ArrayList<Vaccine> getTotalVaccinesLeftByVCId(int vcId) throws SQLException {
        ArrayList<Vaccine> vaccinesLeftByVC = new ArrayList<>();
        String query = "SELECT vaccine_batchno FROM Vaccines WHERE VC_ID = ? AND vaccine_batchno NOT IN (SELECT vaccine_batchno FROM UsedVaccinesView )";
        Connection conn = DbUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setInt(1, vcId);
        ResultSet rs = ps.executeQuery();
        while (rs.next())
            vaccinesLeftByVC.add(new Vaccine(rs.getInt(1)));
        return vaccinesLeftByVC;
    }

    public static boolean undistributedVaccinesLeftAmountGreaterThan(int amount) throws SQLException {
        String query = "SELECT COUNT(vaccine_batchno) FROM Vaccines WHERE VC_ID IS NULL";
        Connection conn = DbUtil.getConnection();
        ResultSet rs = conn.createStatement().executeQuery(query);
        rs.next();
        return rs.getInt(1) >= amount;
    }
}