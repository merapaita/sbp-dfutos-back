package com.rosist.difunto.venta.service;

import java.util.List;

import com.rosist.difunto.service.ICRUD;
import com.rosist.difunto.venta.model.Parmae;
import com.rosist.difunto.venta.model.ParmaePK;

public interface IParmaeService extends ICRUD<Parmae, ParmaePK> {

//	Parmae registrar(Parmae parmae) throws Exception;
//	Parmae modificar(Parmae parmae) throws Exception;
//	List<Parmae> listar() throws Exception;
	List<Parmae> listarParmae(String tipo, String codigo, String codigoaux, Integer page, Integer size) throws Exception;
//	Parmae listarPorId(ParmaePK id) throws Exception;
//	void eliminar(ParmaePK id) throws Exception;
	
}
