package com.rosist.difunto.venta.service;

import java.sql.SQLException;
import java.util.List;

import com.rosist.difunto.service.ICRUD;
import com.rosist.difunto.venta.model.Pagsub;

public interface IPagsubService extends ICRUD<Pagsub, String> {

	public List<Pagsub> listarPagsub(Integer anno, Integer mes, Integer page, Integer size) throws Exception;
	public Pagsub buscaPagsub(String idPagsub) throws Exception;
	public Pagsub registraTransaccion(Pagsub pagsub) throws SQLException, Exception;
	public Pagsub modificaTransaccion(Pagsub pagsub) throws Exception;
	
}
