/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Cuartel;
import java.util.List;

public interface CuartelDao {
    public int getCuartelCount();
    public int getCuartelCount(String condicion);
    public int getNewIdCuartel(int codcem);
    public Cuartel insertaCuartel(Cuartel cuartel);
    public Cuartel modificaCuartel(Cuartel cuartel);
    public String eliminaCuartel(int codcem, int codcuar) throws Exception;
    public Cuartel buscaCuartel(int codcem, int codcuar);
    public List<Cuartel> listaCuartel(String condicion, String limit, String order);
    public List<Cuartel> listaCuartelDisp(int codcem);
	public byte[] reportePdf(int codcem, int codcuar, boolean bNichos) throws Exception;
}