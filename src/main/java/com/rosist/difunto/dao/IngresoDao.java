package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Ingreso;
import java.io.IOException;
import java.util.List;

public interface IngresoDao {
    public int getIngresoCount();
    public int getIngresoCount(String idIng, String tiping, String tipcli, String tipcom, String conser, String sersbp, String codsbp, String fecha, String estcomsbp);
    public int getNewIdIngreso(int tiping);
    public String getNewCodSBP(String cSerie, String tipcom);
    public Ingreso insertaIngreso(Ingreso ingreso);
    public int imprimeComprobante(Ingreso ingreso) throws IOException ;
    public Ingreso modificaIngreso(Ingreso ingreso);
    public String eliminaIngreso(String iding);
    public String anulaIngreso(String iding);
    public Ingreso buscaIngreso(String iding);
//    public String generaPDF(String iding);
    public List<Ingreso> listaIngresos(String idIng, String tiping, String tipcli, String tipcom, String conser, String sersbp, String codsbp, String fecha, String estcomsbp, String order, Integer page, Integer size);
//	public byte[] reporteTicket(String id) throws Exception;
}