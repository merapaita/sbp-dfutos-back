/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosist.difunto.modelSbp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Vtafco {
    private int codvta;
    private String fecvta;
    private String numrec;
    private Parmae tipovtafco;
    private boolean valida;
    private Cementerio cementerio;
    private Difunto difunto;
    private Parmae tipoentierro;
    private Cuartel cuartel;
    private Nicho_t nicho;
    private Mausoleo mausoleo;
    private Double valor;
    private String nomcli;
    private String dircli;
    private Parmae estado;
    private int num;
    private boolean liquida;
    private String user;
    private String usercr;
    private String duser;
    private String dusercr;
}