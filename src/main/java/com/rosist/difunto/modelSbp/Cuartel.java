package com.rosist.difunto.modelSbp;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Cuartel {
	
    private Cementerio cementerio;
    private int codcuar;
    private String nomcuar;
    private Parmae tipcuar ;
    private int filas;
    private int columnas;
    private int orden;
    private String grupo;
    private Parmae estado;
    private List<Nicho_n> nichos_n;
    private List<Nicho_e> nichos_e;
    private List<Nicho_f> nichos_f;
    private List<Nicho_t> nichos_t;
    
}
