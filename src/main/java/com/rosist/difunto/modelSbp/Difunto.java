/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosist.difunto.modelSbp;

import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Administrador
 */
@Getter
@Setter
@ToString
public class Difunto {
    private Integer coddif;
    private String apepat;
    private String apemat;
    private String nombres;
    private String nomdif;
    private String sexodif;
    private int    edad_a;
    private int    edad_m;
    private int    edad_d;
    private Parmae tipo_entierro;
    private Cementerio cementerio;
    private Cuartel cuartel;
    private Nicho_t  nicho;
    private Mausoleo mausoleo;
    private Cliente cliente;
    private String nomcli;
    private Traslado traslado;
    private Ocufut ocufut;
    private String fecfall;
    private String fecsep;
    private String horasep;
    private String ordinm;
    private String actdef;
    private String diamed;
    private String recing;
    private String fecri;
    private Double mtori;
    private String Observ;
    private Parmae estado;
    private Parmae estvta;
    private String reservado;
	private MultipartFile archivo;
    private String user;
    private String usercr;
    private String duser;
    private String dusercr;

}
