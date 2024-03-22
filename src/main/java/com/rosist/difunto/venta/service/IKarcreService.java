package com.rosist.difunto.venta.service;

import java.util.List;

import com.rosist.difunto.service.ICRUD;
import com.rosist.difunto.venta.model.Ajuste;
import com.rosist.difunto.venta.model.Credito;
import com.rosist.difunto.venta.model.Karcre;
import com.rosist.difunto.venta.model.Pagacta;
import com.rosist.difunto.venta.model.Venta;

public interface IKarcreService extends ICRUD<Karcre, Integer> {

	public Integer getNewCorrel(String codcre);
	public void actualizaKardex(Venta venta) throws Exception;
	public void actualizaKardex(Pagacta pagacta) throws Exception;
	public void actualizaKardex(Ajuste ajuste) throws Exception;
	public List<Karcre> listaKarcrePorCredito(String codcre, String tipmov, String codmov, Integer page, Integer size);
//	public List<Credito> listaKarcre(Integer anno, Integer mes, String cliente, String codcre, String tipmov, String codmov, Integer page, Integer size);
	public byte[]      reporteKarcre(Integer anno, Integer mes, String cliente, String codcre) throws Exception;
	
}
