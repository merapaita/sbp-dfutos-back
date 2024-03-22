package com.rosist.difunto.venta.service;

import java.util.List;

import com.rosist.difunto.service.ICRUD;
import com.rosist.difunto.venta.model.Subvencion;

public interface ISubvencionService extends ICRUD<Subvencion, String> {

	public List<Subvencion> listarSubvenciones(String codvta, String cliente, String estado, Integer page, Integer size) throws Exception;
	
}
