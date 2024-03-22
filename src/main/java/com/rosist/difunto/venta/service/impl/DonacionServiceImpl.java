package com.rosist.difunto.venta.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.model.Donacion;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.IDonacionRepo;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.service.IDonacionService;

@Service
public class DonacionServiceImpl extends CRUDImpl<Donacion, String> implements IDonacionService {
	
	@Autowired
	private IDonacionRepo repo;
	
	@Override
	protected IGenericRepo<Donacion, String> getRepo() {
		return repo;
	}
	
	@Override
	public List<Donacion> listarDonaciones(String codvta, String cliente, String estado, Integer page, Integer size) throws Exception {
		List<Donacion> donaciones = new ArrayList<>();
		List<Object[]> registros = repo.listaDonaciones(codvta, cliente, estado, page, size);

		registros.forEach(reg -> {
			Venta venta = new Venta();
			venta.setCodvta(String.valueOf(reg[2]));

			Donacion donacion = new Donacion();
			donacion.setCoddon(String.valueOf(reg[0]));
			donacion.setFecdon(LocalDate.parse(String.valueOf(reg[1])));
			donacion.setVenta(venta);
			donacion.setDocref(String.valueOf(reg[3]));
			donacion.setNumref(String.valueOf(reg[4]));
			donacion.setFecref(LocalDate.parse(String.valueOf(reg[5])));
			donacion.setExpediente(String.valueOf(reg[6]));
			donacion.setMtodon(Double.parseDouble(String.valueOf(reg[7])));
			donacion.setEstado(String.valueOf(reg[8]));

			donaciones.add(donacion);
		});
		
		return donaciones;
	}


}
