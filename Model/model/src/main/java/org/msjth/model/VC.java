package org.msjth.model;

import lombok.*;

import java.util.ArrayList;

@Setter
@Getter
public class VC extends User {
    private String name;
    private int capPerDay;
    private ArrayList<Recipient> recipients;
    private ArrayList<Vaccine> vaccines;

    public VC(String username, String password, String name, int capPerDay, ArrayList<Recipient> recipients, ArrayList<Vaccine> vaccines) {
        super(username, password);
        this.name = name;
        this.capPerDay = capPerDay;
        this.recipients = recipients;
        this.vaccines = vaccines;
    }
}