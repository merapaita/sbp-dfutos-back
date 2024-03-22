package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Traslado;
import java.util.List;

public interface TrasladoDao {
    public int getTrasladoCount();
    public int getTrasladoCount(String condicion);
    public int getNewIdTraslado();
    public Traslado insertaTraslado(Traslado traslado);
    public Traslado modificaTraslado(Traslado traslado);
    public int eliminaTraslado(int codtras) throws Exception;
    public Traslado buscaTraslado(int codtras);
    public List<Traslado> listaTraslado(String condicion, String limit, String orden);
}