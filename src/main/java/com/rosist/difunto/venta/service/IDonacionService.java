package com.rosist.difunto.venta.service;

import java.util.List;

import com.rosist.difunto.service.ICRUD;
import com.rosist.difunto.venta.model.Donacion;

public interface IDonacionService extends ICRUD<Donacion, String> {

	public List<Donacion> listarDonaciones(String codvta, String cliente, String estado, Integer page, Integer size) throws Exception;
	
}
