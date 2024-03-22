package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Deuda;
import java.util.List;

public interface DeudaDao {
    public int getDeudaCount();
    public int getDeudaCount(String condicion);
    public int getNewDeuda();
    public int insertaDeuda(Deuda deuda);
    public int modificaDeuda(Deuda deuda);
    public String anulaDeuda(int iddeu);
    public String cancelaDeuda(int iddeu);
    public Deuda buscaDeuda(int iddeu);
    public List<Deuda> listaDeuda(String condicion);
}
