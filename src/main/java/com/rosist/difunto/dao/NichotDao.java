package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Nicho_t;
import java.util.List;

public interface NichotDao {
    public int getNichotCount();
    public int getNichotCount(String condicion);
    public int insertaNichot(int codcem, int codcuar, Nicho_t nichot);
    public int modificaNichot(int codcem, int codcuar, Nicho_t nichot);
    public int eliminaNichot(int codcem, int codcuar, int fila1);
    public Nicho_t buscaNichot(int codcem, int codcuar, int fila1, int col1);
    public List<Nicho_t> listaNichot(String condicion);
    public List<Nicho_t> listaFilas(int codcem, int codcuar);
    public List<Nicho_t> listaFilasDisp(int codcem, int codcuar);
    public List<Nicho_t> listaResumen(int codcem, String cAno);
}
