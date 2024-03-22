package com.rosist.difunto.venta.service;

import java.util.List;

import com.rosist.difunto.service.ICRUD;
import com.rosist.difunto.venta.model.Credito;
import com.rosist.difunto.venta.model.Karcre;

public interface ICreditoService extends ICRUD<Credito, String>{

	public List<Credito> listarCreditos       (Integer anno, Integer mes, String cliente, String estado, Integer page, Integer size) throws Exception;
	public List<Credito> listarCreditosControl(Integer anno, Integer mes, String cliente, String estado, Integer page, Integer size) throws Exception;
	public List<Karcre>  listarCreditosControlResumen(String codcre);
	public byte[] reporteCreditosControl      (Integer anno, Integer mes, String cliente, String estado) throws Exception;
	public Credito buscaCredito(String idCredito) throws Exception;
	
}
