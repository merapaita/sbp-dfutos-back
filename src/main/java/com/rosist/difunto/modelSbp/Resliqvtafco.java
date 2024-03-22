package com.rosist.difunto.modelSbp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Resliqvtafco {
    private String fecvta;
    private String user;
    private int idliq;
    private Parmae tipvtafco;
    private double cantidad;
    private double valor;
    private double monto;
    private Parmae estado;
}
