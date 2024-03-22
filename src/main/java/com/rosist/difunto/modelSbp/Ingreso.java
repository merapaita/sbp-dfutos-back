package com.rosist.difunto.modelSbp;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Ingreso {
    private String servicio;
    private String iding;
    private Sucursal tiping;
    private int correl;
    private Parmae tipcli;
    private Parmae tipocomprobante;
    private Parmae conser;
    private String sersbp;
    private String codsbp;
    private String fecha;
    private Parmae turno;
    private Cliente cliente;
    private String nomcli;
    private Difunto difunto;
    private double mtosbpgr;
    private double mtosbpex;
    private double mtosbpin;
    private double mtotot;
    private double dscsbppor;
    private double dscsbp;
    private double dscigv;
    private double mtocp;
    private double basimpgr;
    private double basimpex;
    private double basimpin;
    private double igv;
    private boolean liquida;
    private String idliq;
    private List<Iteing> detiteing;
//    private Notacredito notacredito;
    private Parmae estado;
    private String user;
    private String usercr;
    private String duser;
    private String dusercr;
}
