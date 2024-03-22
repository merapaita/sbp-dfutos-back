package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Ubigeo;
import java.util.List;

public interface UbigeoDao {
    public int getUbigeoCount();
    public int getUbigeoCount(String condicion);
//    public int getNewUbigeo();
    public int insertaUbigeo(Ubigeo ubigeo);
    public int modificaUbigeo(Ubigeo ubigeo);
    public int eliminaUbigeo(String ubigeo);
    public Ubigeo buscaUbigeo(String ubigeo);
    public List<Ubigeo> listaUbigeo(String condicion);
}