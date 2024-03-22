package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Partida;
import java.util.List;

public interface PartidaDao {
    public int getPartidaCount();
    public int getPartidaCount(String codpart, String descri);
    public Partida insertaPartida(Partida partida);
    public Partida modificaPartida(Partida partida);
    public int eliminaPartida(String codpart);
    public Partida buscaPartida(String codpart);
    public List<Partida> listaPartidas(String codpart, String descri, Integer page, Integer size);
//    public List<Partida> listaPartidas2(String condicion);
}