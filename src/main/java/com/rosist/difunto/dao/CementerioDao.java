/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Cementerio;
import java.util.List;

public interface CementerioDao {
    public int getCementerioCount();
    public int getCementerioCount(String condicion);
    public int getNewCementerio();
    public Cementerio insertaCementerio(Cementerio cementerio);
    public Cementerio modificaCementerio(Cementerio cementerio);
    public String eliminaCementerio(int codcem) throws Exception;
    public Cementerio buscaCementerio(int codcem);
    public List<Cementerio> listaCementerio(String condicion, String limit, String order);
}