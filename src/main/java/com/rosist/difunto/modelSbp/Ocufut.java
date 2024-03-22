/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosist.difunto.modelSbp;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Marco
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Ocufut {
    private int codocu;
    private String nomocu;
    private String apepat;
    private String apemat;
    private String nombres;
    private int edad_a;
    private int edad_m;
    private int edad_d;
    private String sexo;
    private Cliente cliente;
    private String nomcli;
    private Cementerio cementerio;
    private Cuartel cuartel;
    private Nicho_t nicho;
    private Parmae estado;
    private String observ;
    private Transfer transfer;
    private Difunto difunto;
    private String recing;
    private String fecri;
    private Double mtori;
    private Parmae estvta;
    private String user;
    private String usercr;
    private Date duser;
    private Date dusercr;

}
