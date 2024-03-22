/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Mausoleo;
import java.util.List;

/**
 *
 * @author Administrador
 */
public interface MausoleoDao {
    public int getMausoleoCount();
    public int getMausoleoCount(String condicion);
    public int getNewIdMausoleo(int codcem);
    public Mausoleo insertaMausoleo(Mausoleo mausoleo);
    public Mausoleo modificaMausoleo(Mausoleo mausoleo);
    public String eliminaMausoleo(int codcem, int codmau) throws Exception;
    public Mausoleo buscaMausoleo(int codcem, int codmau);
    public List<Mausoleo> listaMausoleo(String condicion, String limit, String orden);
}