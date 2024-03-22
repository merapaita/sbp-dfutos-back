package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Iteing;
import java.util.List;

public interface IteIngDao {
    public int getIteIngCount();
    public int getIteIngCount(String condicion);
    public int insertaIteIng(int iding, Iteing iteing);
    public int modificaIteIng(int iding, Iteing iteing);
    public int eliminaIteIng(int iding, int iteing);
    public Iteing buscaIteIng(int iding, int iteing);
    public List<Iteing> listaIteIng(int iding, String condicion);
    public List<Iteing> listaIteIng(String condicion);
}