package org.msjth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class Appointment {
    private LocalDate appointmentDate;
    private Vaccine vaccine;
}