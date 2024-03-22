package com.rosist.difunto.venta.service;

import java.sql.SQLException;
import java.util.List;

import com.rosist.difunto.service.ICRUD;
import com.rosist.difunto.venta.model.Pagdon;
import com.rosist.difunto.venta.model.Pagsub;
import com.rosist.difunto.venta.model.Venta;

public interface IPagdonService extends ICRUD<Pagdon, String> {

	public List<Pagdon> listarPagdon(Integer anno, Integer mes, Integer page, Integer size) throws Exception;
	public Pagdon buscaPagdon(String idPagdon) throws Exception;
	public Pagdon registraTransaccion(Pagdon pagdon) throws SQLException, Exception;
	public Pagdon modificaTransaccion(Pagdon pagdon) throws Exception;
	
}
