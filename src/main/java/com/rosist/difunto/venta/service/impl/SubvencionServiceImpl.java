package com.rosist.difunto.venta.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.model.Donacion;
import com.rosist.difunto.venta.model.Subvencion;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.repo.ISubvencionRepo;
import com.rosist.difunto.venta.service.ISubvencionService;

@Service
public class SubvencionServiceImpl extends CRUDImpl<Subvencion, String> implements ISubvencionService {

	@Autowired
	private ISubvencionRepo repo;
	
	@Override
	protected IGenericRepo<Subvencion, String> getRepo() {
		return repo;
	}

	@Override
	public List<Subvencion> listarSubvenciones(String codvta, String cliente, String estado, Integer page, Integer size) throws Exception {
		List<Subvencion> subvenciones = new ArrayList<>();
		List<Object[]> registros = repo.listaSubvenciones(codvta, cliente, estado, page, size);

		registros.forEach(reg -> {
			Venta venta = new Venta();
			venta.setCodvta(String.valueOf(reg[2]));

			Subvencion subvencion = new Subvencion();
			subvencion.setCodsub(String.valueOf(reg[0]));
			subvencion.setFecsub(LocalDate.parse(String.valueOf(reg[1])));
			subvencion.setVenta(venta);
			subvencion.setCodent(String.valueOf(reg[3]));
			subvencion.setDocref(String.valueOf(reg[4]));
			subvencion.setNumref(String.valueOf(reg[5]));
			subvencion.setFecref(LocalDate.parse(String.valueOf(reg[6])));
			subvencion.setExpediente(String.valueOf(reg[7]));
			subvencion.setMtosub(Double.parseDouble(String.valueOf(reg[8])));
			subvencion.setEstado(String.valueOf(reg[9]));

			subvenciones.add(subvencion);
		});
		
		return subvenciones;
	}
	
}
