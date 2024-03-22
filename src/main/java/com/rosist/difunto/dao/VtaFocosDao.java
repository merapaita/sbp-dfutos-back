package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Vtafco;
import java.util.List;

public interface VtaFocosDao {
    public int getVtaFcoCount(String cAno);
    public int getVtaFcoCount(String cAno, String condicion);
    public int getNewIdVtaFco(int codcem, String cAno);    //int codcem
    public int insertaVtaFco(Vtafco vtafco, String cAno);
    public int modificaVtaFco(Vtafco vtafco, String cAno);
    public int anulaVtaFco(int codcem, int codvta, String cAno);
    public int valFco(String tipvf);
    public Vtafco buscaVtaFco(int codcem, int codvta, String cAno);
    public List<Vtafco> listaVtaFco(String condicion, String cano);
    public List<Vtafco> listaVtaFcoxCuartel(String condicion, String sLimit, String cAno);
    public List<Vtafco> listaResumenVtaFcoxCuartel(int codcem, String cAno);
    public List<Vtafco> listaVtaFcoxMausoleo(String condicion, String sLimit, String cAno);
	public byte[] reporteVtaFoco(int id) throws Exception;
}
