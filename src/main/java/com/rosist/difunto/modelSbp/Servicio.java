package com.rosist.difunto.modelSbp;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Servicio {
    private String idser;
    private Parmae tipser;
    private int    correl;
    private String desser;
    private double mtosbp;
    private double mtomed;
    private double mtotot;
    private double tarssf;
    private double tartra;
    private Partida partida;
    private Parmae tipoOperacion;
    private MultipartFile archivo;

    public Servicio(String idser, Parmae tipser, int correl, String desser, double mtosbp, double mtomed, double mtotot, double tarssf, double tartra, Partida partida, Parmae tipoOperacion) {
        this.idser = idser;
        this.tipser = tipser;
        this.correl = correl;
        this.desser = desser;
        this.mtosbp = mtosbp;
        this.mtomed = mtomed;
        this.mtotot = mtotot;
        this.tarssf = tarssf;
        this.tartra = tartra;
        this.partida = partida;
        this.tipoOperacion = tipoOperacion;
    }

}