package org.msjth.model;

import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class MOH {
    ArrayList<Recipient> recipients;
    ArrayList<VC> vcs;
    ArrayList<Vaccine> vaccines;
}
