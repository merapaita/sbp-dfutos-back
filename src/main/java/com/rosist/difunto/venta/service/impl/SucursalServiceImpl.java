package com.rosist.difunto.venta.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rosist.difunto.venta.model.Sucursal;
import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.repo.ISucursalRepo;
import com.rosist.difunto.venta.service.ISucursalService;

@Service
public class SucursalServiceImpl extends CRUDImpl<Sucursal, Integer> implements ISucursalService {
	@Autowired
	private ISucursalRepo repo;

	@Override
	protected IGenericRepo<Sucursal, Integer> getRepo() {
		return repo;
	}
	
}
