/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Difunto;
import java.util.List;

public interface DifuntoDao {
    public int getDifuntoCount();
    public int getDifuntoCount(String condicion);
    public int getNewIdDifunto(int codcem);
    public Difunto insertaDifunto(Difunto difunto);
    public Difunto modificaDifunto(Difunto difunto) throws Exception;
    public int eliminaDifunto(int codcem, int coddif) throws Exception;
    public Difunto buscaDifunto(int codcem, int coddif);
    public List<Difunto> listaDifunto(String condicion, String limit, String orden);
}
