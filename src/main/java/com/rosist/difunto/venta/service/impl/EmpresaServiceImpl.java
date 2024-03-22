package com.rosist.difunto.venta.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rosist.difunto.venta.model.Empresa;
import com.rosist.difunto.venta.repo.IEmpresaRepo;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.service.IEmpresaService;

@Service
public class EmpresaServiceImpl extends CRUDImpl<Empresa, Integer> implements IEmpresaService {

	@Autowired
	private IEmpresaRepo repo;

	@Override
	protected IGenericRepo<Empresa, Integer> getRepo() {
		return repo;
	}
}
