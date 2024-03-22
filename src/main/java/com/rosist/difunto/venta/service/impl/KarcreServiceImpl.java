package com.rosist.difunto.venta.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.model.Ajuste;
import com.rosist.difunto.venta.model.Credito;
import com.rosist.difunto.venta.model.Karcre;
import com.rosist.difunto.venta.model.Pagacta;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.repo.IKarcreRepo;
//import com.rosist.difunto.venta.reports.PDFCuentaPorCredito;
import com.rosist.difunto.venta.service.IKarcreService;
import com.rosist.difunto.venta.service.ISucursalService;

@Service
public class KarcreServiceImpl extends CRUDImpl<Karcre, Integer> implements IKarcreService {

	@Autowired
	private IKarcreRepo repo;
	
	@Autowired
	private ISucursalService servSucursal;

	@Override
	protected IGenericRepo<Karcre, Integer> getRepo() {
		return repo;
	}
	
	private static final Logger log = LoggerFactory.getLogger(KarcreServiceImpl.class);
	
	@Override
	public Integer getNewCorrel(String codcre) {
		int _correl = 0;
		_correl = repo.getNewCorrel(codcre);
		return _correl;
	}

	@Override
	public void actualizaKardex(Venta venta) throws Exception {
		Karcre _karcre = repo.buscaPorItem("CRE", venta.getCodvta());
		
		if (_karcre==null) {
			Karcre karcre = new Karcre();
			karcre.setCredito(venta.getCredito());
			int correl = getNewCorrel(venta.getCredito().getCodcre());
			karcre.setCorrel(correl);
			karcre.setEstado(venta.getEstado());
			karcre.setFecha(venta.getFecvta());
			karcre.setTipmov("CRE");
			karcre.setCodmov(venta.getCodvta());
			karcre.setMtocre(venta.getMtocre());
			karcre.setMtoamo(0.0);
			karcre.setMtoint(0.0);
			karcre.setMtoaju(0.0);
			karcre.setUserup(venta.getUserup());
			karcre.setUsercr(venta.getUsercr());
			karcre.setDuserup(venta.getDuserup());
			karcre.setDusercr(venta.getDusercr());
			repo.save(karcre);
		} else {
			_karcre.setFecha(venta.getFecvta());
			_karcre.setMtocre(venta.getMtocre());
			_karcre.setMtoamo(0.0);
			_karcre.setMtoint(0.0);
			_karcre.setMtoaju(0.0);
			_karcre.setUserup(_karcre.getUserup());
			_karcre.setUsercr(_karcre.getUsercr());
			_karcre.setDuserup(venta.getDuserup());
			_karcre.setDusercr(venta.getDusercr());
			repo.save(_karcre);
		}
	}

	@Override
	public void actualizaKardex(Pagacta pagacta) throws Exception {
		Karcre _karcre = repo.buscaPorItem("CRE", pagacta.getCodpac());
		
		if (_karcre==null) {
			Karcre karcre = new Karcre();
			karcre.setCredito(pagacta.getCredito());
			int correl = getNewCorrel(pagacta.getCredito().getCodcre());
			karcre.setCorrel(correl);
			karcre.setEstado(pagacta.getEstado());
			karcre.setFecha(pagacta.getFecpac());
			karcre.setTipmov("PAC");
			karcre.setCodmov(pagacta.getCodpac());
			karcre.setMtocre(0.0);
			karcre.setMtoamo(pagacta.getMtoamo());
			karcre.setMtoint(pagacta.getInteres());
			karcre.setMtoaju(pagacta.getMtoaju());
			karcre.setUserup(pagacta.getUserup());
			karcre.setUsercr(pagacta.getUsercr());
			karcre.setDuserup(pagacta.getDuserup());
			karcre.setDusercr(pagacta.getDusercr());
			repo.save(karcre);
		} else {
			_karcre.setFecha(pagacta.getFecpac());
			_karcre.setMtocre(0.0);
			_karcre.setMtoamo(pagacta.getMtoamo());
			_karcre.setMtoint(pagacta.getInteres());
			_karcre.setMtoaju(pagacta.getMtoaju());
			_karcre.setUserup(_karcre.getUserup());
			_karcre.setUsercr(_karcre.getUsercr());
			_karcre.setDuserup(pagacta.getDuserup());
			_karcre.setDusercr(pagacta.getDusercr());
			repo.save(_karcre);
		}
	}

	@Override
	public void actualizaKardex(Ajuste ajuste) throws Exception {
		Karcre _karcre = repo.buscaPorItem("AJU", ajuste.getCodaju());
		
		if (_karcre==null) {
			Karcre karcre = new Karcre();
			karcre.setCredito(ajuste.getCredito());
			int correl = getNewCorrel(ajuste.getCredito().getCodcre());
			karcre.setCorrel(correl);
			karcre.setEstado(ajuste.getEstado());
			karcre.setFecha(ajuste.getFecaju());
			karcre.setTipmov("AJU");
			karcre.setCodmov(ajuste.getCodaju());
			karcre.setMtocre(0.0);
			karcre.setMtoamo(0.0);
			karcre.setMtoint(0.0);
			karcre.setMtoaju(ajuste.getMtoaju());
			karcre.setUserup(ajuste.getUserup());
			karcre.setUsercr(ajuste.getUsercr());
			karcre.setDuserup(ajuste.getDuserup());
			karcre.setDusercr(ajuste.getDusercr());
			repo.save(karcre);
		} else {
			_karcre.setFecha(ajuste.getFecaju());
			_karcre.setMtocre(0.0);
			_karcre.setMtoamo(0.0);
			_karcre.setMtoint(0.0);
			_karcre.setMtoaju(ajuste.getMtoaju());
			_karcre.setUserup(_karcre.getUserup());
			_karcre.setUsercr(_karcre.getUsercr());
			_karcre.setDuserup(ajuste.getDuserup());
			_karcre.setDusercr(ajuste.getDusercr());
			repo.save(_karcre);
		}
	}

	@Override
	public List<Karcre> listaKarcrePorCredito(String codcre, String tipmov, String codmov, Integer page,
			Integer size) {
		log.info("listarKardexPorContrato...");
		List<Karcre> lKarcre = new ArrayList<>();
		List<Object[]> registros = repo.listaKarcre(codcre, tipmov, codmov, page, size);
		
		registros.forEach(reg -> {
			Credito credito = new Credito();
			credito.setCodcre(String.valueOf(reg[1]));
			
			Karcre karcre = new Karcre();
			karcre.setIdKarcre(Integer.parseInt(String.valueOf(reg[0])));
			karcre.setCredito(credito);
			
			karcre.setCorrel(Integer.parseInt(String.valueOf(reg[2])));
			karcre.setEstado(String.valueOf(reg[3]));
			karcre.setFecha(LocalDate.parse(String.valueOf(reg[4])));
			karcre.setTipmov(String.valueOf(reg[5]));
			karcre.setCodmov(String.valueOf(reg[6]));
			
			karcre.setMtocre(Double.parseDouble(String.valueOf(reg[7])));
			karcre.setMtoamo(Double.parseDouble(String.valueOf(reg[8])));
			karcre.setMtoint(Double.parseDouble(String.valueOf(reg[9])));
			karcre.setMtoaju(Double.parseDouble(String.valueOf(reg[10])));
			
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//			karcre.setDuserup(LocalDateTime.parse( String.valueOf(reg[24]).substring(0, 19), formatter ));
//			if (reg[25]!=null) karcre.setUsercr(String.valueOf(reg[25]));
//			if (reg[26]!=null) karcre.setDusercr(LocalDateTime.parse(String.valueOf(reg[26]).subSequence(0, 19), formatter ));
			lKarcre.add(karcre);
		});
		
		double _sMtocre = 0, _sMtoamo = 0, _sMtoaju = 0, _sSaldo = 0;
		for (Karcre reg : lKarcre) {
			if (reg.getTipmov().equals("CRE")) {
				_sMtocre  += reg.getMtocre();
			} else if(reg.getTipmov().equals("PAC")) {
				_sMtoamo -= reg.getMtoamo();
			}
			if (reg.getTipmov().equals("AJU")) {
				_sMtoaju += reg.getMtoaju(); // - _sGtoadm;
			}
			_sSaldo = _sMtocre - _sMtoamo - _sMtoaju;
			
			reg.setMtocre(_sMtocre);
			reg.setMtoamo(_sMtoamo);
			reg.setMtoaju(_sMtoaju);
			reg.setSaldo(_sSaldo);
			
		}
		return lKarcre;
	}

	@Override
	public byte[] reporteKarcre(Integer anno, Integer mes, String codcre, String cliente) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public byte[] reporteKarcrePorCredito(String codcre) throws Exception {
////		List<Karcre> lKarcre = listaKarcrePorCredito(codcre, "", "", -1, 0);
////log.info("lKarcre:" + lKarcre);		
////		List<Parmae> tiposContrato = serviceParmae.listaPorTipo("TIPCON");
//		Map<String, Object> parametros = new HashMap<String, Object>();
//		Sucursal sucursal = servSucursal.listarPorId(1);
//		
//		log.info("reporteCuentaPorContrato...idContrato:");
//		
//		parametros.put("codcre", codcre);
//		parametros.put("datasource", datasource);
//		parametros.put("sucursal", sucursal);
//		
////		PDFCuentaPorCredito pdfCuentaPorContrato = new PDFCuentaPorCredito(parametros);
//		
////		return pdfCuentaPorContrato.creaReporte();
//		return null;
//	}
	
}
