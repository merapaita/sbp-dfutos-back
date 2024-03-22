/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosist.difunto.modelSbp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Administrador
 */
@Setter
@Getter
@ToString
public class Mausoleo {
    private Cementerio cementerio;
    private Integer codmau;
    private String lotizado;
    private String nomlote;
    private Parmae tipomau;
    private String ubicacion;
    private String familia;
    private String area_adq;
    private String area_cons;
    private String area_cerc;
    private int totdif;
    private int numdif;
    private Cliente cliente;
    private String nomcli;
    private Parmae estado;
    private Parmae estvta;
    private String observ;

}