package org.msjth.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class Recipient extends User {
    private String name;
    private int age;
    private final String phoneNo;
    private int status;
    private String allocatedVaccineCenter;
    private ArrayList<Appointment> appointments;

    public Recipient(String username,
                     String password,
                     String name,
                     String phoneNo,
                     int age,
                     int status,
                     String allocatedVaccineCenter,
                     ArrayList<Appointment> appointments) {
        super(username, password);
        this.name = name;
        this.phoneNo = phoneNo;
        this.age = age;
        this.status = status;
        this.allocatedVaccineCenter = allocatedVaccineCenter;
        this.appointments = appointments;
    }

    public String getDoseXAppointmentDateString(int doseNumber) {
        boolean hasDoseXAppointmentDate = appointments != null && appointments.size() >= doseNumber
                && appointments.get(doseNumber - 1).getAppointmentDate() != null;
        if (hasDoseXAppointmentDate)
            return appointments.get(doseNumber - 1).getAppointmentDate().toString();
        return "NA";
    }

    public String getStatusDescription() {
        String[] vaccinationStatusDescription = { "", "Pending", "1st Dose Appointment", "1st Dose Completed",
                "2nd Dose Appointment", "2nd Dose Completed"};
        return vaccinationStatusDescription[status];
    }
}