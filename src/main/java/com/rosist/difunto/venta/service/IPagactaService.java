package com.rosist.difunto.venta.service;

import java.sql.SQLException;
import java.util.List;

import com.rosist.difunto.service.ICRUD;
import com.rosist.difunto.venta.dto.PagactaDto;
import com.rosist.difunto.venta.model.Pagacta;
import com.rosist.difunto.venta.model.Venta;

public interface IPagactaService extends ICRUD<Pagacta, String> {

	public List<Pagacta> listarPagacta(Integer anno, Integer mes, String cliente, String estado, Integer page, Integer size) throws Exception;
	public Pagacta buscaPagacta(String idPagacta) throws Exception;
	public Pagacta registraTransaccion(PagactaDto pagacta) throws SQLException, Exception;
	public Pagacta modificaTransaccion(Pagacta pagacta) throws Exception;
	
}
