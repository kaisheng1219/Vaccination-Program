package model;

import org.msjth.model.*;
import org.msjth.model.service.VCService;
import org.msjth.model.service.VaccineService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class VCHallSimulator {
    private VC vc;
    private ArrayList<Recipient> recipients;

    private Stack<Vaccine> vaccineStack;
    private Queue<Recipient> seniorQueue;
    private Queue<Recipient> normalQueue;

    private LocalDate targetDate;

    public VCHallSimulator(VC vc) {
        this.vc = vc;
    }

    public void setTargetDate(LocalDate targetDate) throws SQLException {
        this.targetDate = targetDate;
        setRecipientByDate();
        setVaccines();
    }

    private void setRecipientByDate() {
        recipients = new ArrayList<>();
        ArrayList<Recipient> temp = vc.getRecipients();
        for (Recipient recipient : temp) {
            ArrayList<Appointment> appointments = recipient.getAppointments();
            if (appointments != null && appointments.size() > 0) {
                int lastAppointmentIndex = appointments.size() - 1;
                if (recipient.getStatus() == 2 || recipient.getStatus() == 4)
                    if (recipient.getAppointments().get(lastAppointmentIndex).getAppointmentDate().equals(targetDate)) {
                        recipients.add(recipient);
                }
            }
        }
    }

    public ArrayList<Recipient> getRecipients() {
        return this.recipients;
    }

    private void setVaccines() throws SQLException {
        ArrayList<Vaccine> vaccinesLeft = VaccineService.getTotalVaccinesLeftByVCId(VCService.getVCIdByName(vc.getName()));
        vaccineStack = new Stack<>();
        if (recipients != null)
            for (int i = 0; i < recipients.size(); i++)
                vaccineStack.add(vaccinesLeft.get(i));
    }

    public Stack<Vaccine> getVaccineStack() {
        return this.vaccineStack;
    }

    public Queue<Recipient> getSeniorQueue() {
        return this.seniorQueue;
    }

    public Queue<Recipient> getNormalQueue() {
        return this.normalQueue;
    }

    public LocalDate getTargetDate() {
        return this.targetDate;
    }

    public VC getVc() {
        return this.vc;
    }

    public void setVc(VC vc) {
        this.vc = vc;
    }

    public void splitRecipients() {
        seniorQueue = new LinkedList<>();
        normalQueue = new LinkedList<>();
        for (Recipient recipient : recipients) {
            if (recipient.getAge() >= 60) {
                seniorQueue.offer(recipient);
            } else {
                normalQueue.offer(recipient);
            }
        }
    }

}
