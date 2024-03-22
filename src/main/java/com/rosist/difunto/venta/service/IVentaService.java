package com.rosist.difunto.venta.service;

import java.sql.SQLException;
import java.util.List;

import com.rosist.difunto.service.ICRUD;
import com.rosist.difunto.venta.model.Venta;

public interface IVentaService extends ICRUD<Venta, String> {

	public List<Venta> listarVentas(Integer anno, Integer mes, String cliente, String estado, Integer page, Integer size) throws Exception;
	public Venta buscaVenta(String idVenta) throws Exception;
	public Venta registraTransaccion(Venta venta) throws SQLException, Exception;
	public Venta modificaTransaccion(Venta venta) throws Exception;
	
}