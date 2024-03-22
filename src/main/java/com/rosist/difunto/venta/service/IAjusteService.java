package com.rosist.difunto.venta.service;

import java.sql.SQLException;
import java.util.List;

import com.rosist.difunto.service.ICRUD;
import com.rosist.difunto.venta.dto.AjusteDto;
import com.rosist.difunto.venta.model.Ajuste;

public interface IAjusteService extends ICRUD<Ajuste, String> {

	public List<Ajuste> listarAjuste(Integer anno, Integer mes, String cliente, String estado, Integer page, Integer size) throws Exception;
	public Ajuste buscaAjuste(String idPagacta) throws Exception;
	public Ajuste registraTransaccion(AjusteDto ajuste) throws SQLException, Exception;
	public Ajuste modificaTransaccion(Ajuste ajuste) throws Exception;
	
}
