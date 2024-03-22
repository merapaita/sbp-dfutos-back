package com.rosist.difunto.modelSbp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Deuda {
    private int iddeu;
    private String fecdeu;
    private Difunto difunto;
    private String nomcli;
    private String dircli;
    private Parmae tipent;
    private Cementerio cementerio;
    private Cuartel cuartel;
    private Nicho_t nicho;
    private Mausoleo mausoleo;
    private double monto;
    private Parmae estado;
    private String user;
    private String duser;
    private String usercr;
    private String dusercr;
}
