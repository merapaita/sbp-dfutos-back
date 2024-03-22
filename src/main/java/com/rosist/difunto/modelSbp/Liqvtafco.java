package com.rosist.difunto.modelSbp;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Liqvtafco {
    private int idlvf;
    private String fecliq;
//    private Users usuario;
    private double mtonic;
    private double mtomau;
    private double mtotot;
    private String lisnic;
    private String lismau;
    private String lisanu;
    private int numnic;
    private int nummau;
    private int numanu;
    private int numtot;
    private Parmae estado;
    private List<Vtafco> detvtafco;
    private String user;
    private String usercr;
    private String duser;
    private String dusercr;
}
