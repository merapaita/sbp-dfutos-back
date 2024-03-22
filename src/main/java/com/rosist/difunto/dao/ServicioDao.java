package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Servicio;
import java.util.List;

public interface ServicioDao {
    public int getServicioCount();
    public int getServicioCount(String idser, String tipser, String desser);
    public int getNewIdServicio(String tipser);
    public Servicio insertaServicio(Servicio servicio);
    public Servicio modificaServicio(Servicio servicio);
    public String eliminaServicio(String idser);
	public byte[] reporteServicio() throws Exception;
    public Servicio buscaServicio(String idser);
    public List<Servicio> listaServicios(String idser, String tipser, String desser, Integer page, Integer size);
}