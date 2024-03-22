package com.rosist.difunto.modelSbp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Iteing {
    private int item;
    private Parmae tipser;
    private Servicio servicio;
    private String descri;
    private Partida partida;
    private double mtosbp;
    private double total;
    private double basimp;
    private double dscsbppor;
    private double dscsbp;
    private double dscigv;
    private double igv;
    private int liquida;
    private String idliq;
    private double mtocp;
    private Integer add=0;    // boolean flag
    private Integer remove=0; // boolean flag
    private Integer wrapper;  // boolean flag
    private String user;
    private String usercr;
    private String duser;
    private String dusercr;
}