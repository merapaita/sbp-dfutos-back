package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Ocufut;
import java.util.List;

public interface OcuFutDao {
    public int getOcuFutCount();
    public int getOcuFutCount(String condicion);
    public int getNewIdOcuFut(int codcem);
    public Ocufut insertaOcuFut(Ocufut ocufut);
    public Ocufut modificaOcuFut(Ocufut ocufut);
    public int eliminaOcuFut(int codcem, int codocu);
    public Ocufut buscaOcuFut(int codcem, int codocu);
    public List<Ocufut> listaOcuFut(String condicion, String limit, String orden);
}
