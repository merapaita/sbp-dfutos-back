package com.rosist.difunto.venta.service;

import java.sql.SQLException;
import java.util.List;

import com.rosist.difunto.service.ICRUD;
import com.rosist.difunto.venta.model.Cliente;

public interface IClienteService extends ICRUD<Cliente, Integer> {

//	public Integer getNewCodcli();
	public List<Cliente> listarCliente(Integer codcli, String tipdoccli, String doccli, String nomcli, Integer page, Integer size) throws Exception;
//	public Cliente buscaCliente(Integer codcli) throws Exception;
	public Cliente registraTransaccion(Cliente cliente) throws SQLException, Exception;
	public Cliente modificaTransaccion(Cliente cliente) throws Exception;
}
