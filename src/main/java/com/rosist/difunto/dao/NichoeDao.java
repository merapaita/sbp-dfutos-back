package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Nicho_e;
import java.util.List;

public interface NichoeDao {
    public int insertaNichoe(int codcem, int codcuar, int fila1, Nicho_e nichoe);
    public int modificaNichoe(int codcem, int codcuar, int fila1, Nicho_e nichoe);
    public int eliminaNichoe(int codcem, int codcuar, int fila1);
    public List<Nicho_e> listaNichoe(String condicion);
    public Nicho_e buscaNichoe(int codcem, int codcuar, int fila1);
}
