package com.rosist.difunto.venta.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.model.Donacion;
import com.rosist.difunto.venta.model.Pagacta;
import com.rosist.difunto.venta.model.Pagdon;
import com.rosist.difunto.venta.model.Parmae;
import com.rosist.difunto.venta.repo.IDonacionRepo;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.repo.IPagdonRepo;
import com.rosist.difunto.venta.service.IDonacionService;
import com.rosist.difunto.venta.service.IPagdonService;
import com.rosist.difunto.venta.service.IParmaeService;

@Repository
public class PagdonServiceImpl extends CRUDImpl<Pagdon, String> implements IPagdonService {

	@Autowired
	private IPagdonRepo repo;

	@Autowired
	private IDonacionService servDonacion;

	@Autowired
	private IParmaeService servParmae;

	@Override
	protected IGenericRepo<Pagdon, String> getRepo() {
		return repo;
	}

	private static final Logger log = LoggerFactory.getLogger(PagdonServiceImpl.class);

	@Override
	public List<Pagdon> listarPagdon(Integer anno, Integer mes, Integer page, Integer size) throws Exception {
		List<Pagdon> pagos = new ArrayList<>();
		List<Object[]> registros = repo.listarPagdon(anno, mes, page, size);
		List<Parmae> entidades = servParmae.listarParmae("", "ENTDON", "", -1, 0);

		registros.forEach(reg -> {
			Pagdon pagdon = Pagdon.builder().codpd(String.valueOf(reg[0]))
					.fecpd(LocalDate.parse(String.valueOf(reg[1]))).codent(String.valueOf(reg[2]))
					.codcp(String.valueOf(reg[3])).cheque(String.valueOf(reg[4]))
					.mtotot(Double.parseDouble(String.valueOf(reg[5]))).observ(String.valueOf(reg[6])).build();

			pagdon.setDesCodent(entidades.stream().filter(xx -> xx.getId().getCodigo().equals(String.valueOf(reg[2])))
					.collect(Collectors.toList()).get(0).getDescri());

			pagos.add(pagdon);

		});
		return pagos;
	}

	@Override
	public Pagdon buscaPagdon(String idPagdon) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pagdon registraTransaccion(Pagdon pagdon) throws SQLException, Exception {
//		String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String cUser = "cambiame";
		LocalDateTime dUser = LocalDateTime.now();

		Pagdon _pagdon = new Pagdon();

		if (pagdon.getCodpd() == null) {
			String anno = String.valueOf(pagdon.getFecpd().getYear());
			String idPagdon = repo.idPagdon(anno);
			_pagdon.setCodpd(idPagdon);
		} else {
			_pagdon.setCodpd(pagdon.getCodpd());
		}
		_pagdon.setFecpd(pagdon.getFecpd());
		_pagdon.setCodent(pagdon.getCodent());
		_pagdon.setCodcp(pagdon.getCodcp());
		_pagdon.setCheque(pagdon.getCheque());
		_pagdon.setObserv(pagdon.getObserv());
		_pagdon.setEstado("00");
		_pagdon.setUserup(cUser);
		_pagdon.setDuserup(dUser);
		_pagdon.setDonaciones(new ArrayList<>());
		pagdon.getDonaciones().forEach(reg -> {
			if (reg.isPagado()) {
				try {
					String coddon = reg.getCoddon();
					Donacion donacion = servDonacion.listarPorId(coddon);
					donacion.setPagado(true);
					donacion.setEstado("10");
					donacion.setPagdon(_pagdon);
					_pagdon.getDonaciones().add(donacion);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Double mtotot = _pagdon.getDonaciones().stream().mapToDouble(Donacion::getMtodon).sum();
		_pagdon.setMtotot(mtotot);
		return repo.save(_pagdon);
	}

	@Override
	public Pagdon modificaTransaccion(Pagdon pagdon) throws Exception {
//		String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String cUser = "cambiame";
		LocalDateTime dUser = LocalDateTime.now();

		Pagdon _pagdon = repo.findById(pagdon.getCodpd()).orElse(null);

		if (_pagdon == null) {
			throw new Exception("No existe Pago de donacion a corregir");
		}

		_pagdon.setFecpd(pagdon.getFecpd());
		_pagdon.setCodcp(pagdon.getCodcp());
		_pagdon.setCheque(pagdon.getCheque());
		_pagdon.setObserv(pagdon.getObserv());
		_pagdon.setUsercr(cUser);
		_pagdon.setDusercr(dUser);

		List<Donacion> _ItemsAgregados = new ArrayList<Donacion>();
		List<Donacion> _ItemsNoAgregados = new ArrayList<Donacion>();

		pagdon.getDonaciones().forEach(reg -> {
			if (reg.isPagado() == true) {
				try {
					Donacion donacion = servDonacion.listarPorId(reg.getCoddon());
					_ItemsAgregados.add(donacion);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					Donacion donacion = servDonacion.listarPorId(reg.getCoddon());
					_ItemsNoAgregados.add(donacion);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		_ItemsAgregados.forEach(regAgregado -> {
			if (regAgregado.getEstado().equals("20")) {
				try {
					regAgregado.setEstado("10");
					regAgregado.setPagado(true);
					regAgregado.setPagdon(_pagdon);
					_pagdon.getDonaciones().add(regAgregado);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		_ItemsNoAgregados.forEach(regNoAgregado -> {
			if (regNoAgregado.getEstado().equals("10")) {
				try {
					regNoAgregado.setEstado("20");
					regNoAgregado.setPagado(false);
					regNoAgregado.setPagdon(null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		Double mtotot = _pagdon.getDonaciones().stream()
				.filter(reg -> reg.isPagado()==true)
				.mapToDouble(Donacion::getMtodon).sum();
		_pagdon.setMtotot(mtotot);
		Pagdon pagdonGrabados = repo.save(_pagdon);
		return pagdonGrabados;
	}

}
