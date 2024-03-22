package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Nicho_n;
import java.util.List;

/**
 *
 * @author Administrador
 */
public interface NichonDao {
    public int insertaNichon(int codcem, int codcuar, Nicho_n nichon);
    public int modificaNichon(int codcem, int codcuar, int fila1, Nicho_n nichon);
    public int eliminaNichon(int codcem, int codcuar, int fila1);
    public Nicho_n buscaNichon(int codcem, int codcuar, int fila1);
    public List<Nicho_n> listaNichon(String condicion);
}
