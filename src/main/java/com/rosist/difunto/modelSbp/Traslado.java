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
 * @author Marco
 */
@Getter
@Setter
@ToString
public class Traslado {
    private int codtras;
    private Cliente cliente;
    private String nomcli;
    private String fectras;
    private Parmae docref;
    private String coddocrf;
    private String fecdocrf;
    private Parmae tiptras;
    private Difunto difuntoant;
    private Difunto difuntonew;
    private Cementerio cemanterior;
    private Cementerio cemnuevo;
    private boolean local;
    private String observ;
    private Parmae estado;
    private String user;
    private String usercr;
    private String duser;
    private String dusercr;
    
}
