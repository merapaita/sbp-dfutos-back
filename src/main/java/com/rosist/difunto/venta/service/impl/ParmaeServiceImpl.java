package com.rosist.difunto.venta.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.model.Parmae;
import com.rosist.difunto.venta.model.ParmaePK;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.repo.IParmaeRepo;
import com.rosist.difunto.venta.service.IParmaeService;

@Service
public class ParmaeServiceImpl extends CRUDImpl<Parmae, ParmaePK> implements IParmaeService {

	@Autowired
	private IParmaeRepo repo;
	
	@Override
	protected IGenericRepo<Parmae, ParmaePK> getRepo() {
		return null;
	}

	@Override
	public List<Parmae> listarParmae(String tipo, String codigo, String codigoaux, Integer page, Integer size) throws Exception {
		List<Parmae> parametros = new ArrayList<>();
		List<Object[]> registros = repo.listarParmae(tipo, codigo, codigoaux, page, size);

		registros.forEach(reg -> {
			ParmaePK id = new ParmaePK();
			id.setTipo(String.valueOf(reg[0]));
			id.setCodigo(String.valueOf(reg[1]));
			id.setCodigoaux(String.valueOf(reg[2]));
			
			Parmae _parmae = new Parmae();
			_parmae.setId(id);
			_parmae.setDescri(String.valueOf(reg[3]));
			parametros.add(_parmae);
			
		});

		return parametros;
	}


}
