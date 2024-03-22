package com.rosist.difunto.venta.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.model.Cliente;
import com.rosist.difunto.venta.model.Credito;
import com.rosist.difunto.venta.model.Empresa;
import com.rosist.difunto.venta.model.Karcre;
import com.rosist.difunto.venta.model.Sucursal;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.ICreditoRepo;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.reports.PDFCreditosControl;
import com.rosist.difunto.venta.service.ICreditoService;
import com.rosist.difunto.venta.service.IEmpresaService;
import com.rosist.difunto.venta.service.ISucursalService;

@Service
public class CreditoServiceImpl extends CRUDImpl<Credito, String>  implements ICreditoService{

	@Autowired
	private ICreditoRepo repo;
	
	@Autowired
	private ISucursalService servSucursal;
	
	@Override
	protected IGenericRepo<Credito, String> getRepo() {
		return repo;
	}
	
	private static final Logger log = LoggerFactory.getLogger(CreditoServiceImpl.class);

	@Override
	public List<Credito> listarCreditos(Integer anno, Integer mes, String _cliente, String estado, Integer page,
			Integer size) throws Exception {
		List<Credito> creditos = new ArrayList<>();
		List<Object[]> registros = repo.listarCredito(anno, mes, _cliente, estado, page, size);
		
		registros.forEach(reg -> {
			Cliente cliente = new Cliente();
			cliente.setCodcli(Integer.parseInt(String.valueOf(reg[13])));
			cliente.setTipdoccli(String.valueOf(reg[14]));
			cliente.setDoccli(String.valueOf(reg[15]));
			cliente.setNomcli(String.valueOf(reg[16]));
			cliente.setDircli(String.valueOf(reg[17]));
			
			Venta venta = new Venta();
			venta.setCodvta(String.valueOf(reg[4]));
			venta.setFecvta(LocalDate.parse(String.valueOf(reg[5])));
			venta.setTipvta(String.valueOf(reg[6]));
			if (reg[7] != null) {
				venta.setNvotipvta(String.valueOf(reg[7]));
			}
			venta.setConvta(String.valueOf(reg[8]));
			venta.setCompag(String.valueOf(reg[9]));
			venta.setSerie(String.valueOf(reg[10]));
			venta.setCodcp(String.valueOf(reg[11]));
			venta.setFeccp(LocalDate.parse(String.valueOf(reg[12])));
			venta.setCliente(cliente);
			
			Credito credito = new Credito();
			credito.setCodcre(String.valueOf(reg[0]));
			credito.setFeccre(LocalDate.parse(String.valueOf(reg[1])));
			credito.setMtocre(Double.parseDouble(String.valueOf(reg[2])));
			credito.setEstado(String.valueOf(reg[3]));
			credito.setVenta(venta);
//			venta.setDesEstado(estados_venta.stream()
//					.filter(xx -> xx.getCodigo().equals(String.valueOf(reg[13])))
//					.collect(Collectors.toList()).get(0).getDescri());
			creditos.add(credito);
		});
		log.info("ListaVentas:" + creditos);
		return creditos;
	}
	
	@Override
	public List<Credito> listarCreditosControl(Integer anno, Integer mes, String cliente, String estado, Integer page,
			Integer size) {		// , String codcre, String tipmov, String codmov
		
		List<Credito> creditos = new ArrayList<>();
		List<Object[]> registros = repo.listarCreditoControl(anno, mes, cliente, estado, page, size);
		String _codcre = "";
		for(Object[] reg: registros) {
//			log.info("_codcre:" + _codcre);
			if (!_codcre.equals(String.valueOf(reg[1]))) {
//				log.info("entre al if");
				_codcre = String.valueOf(reg[1]);
				
				Cliente _cliente = new Cliente();
				_cliente.setCodcli(Integer.parseInt(String.valueOf(reg[11])));
				_cliente.setTipdoccli(String.valueOf(reg[12]));
				_cliente.setDoccli(String.valueOf(String.valueOf(reg[13])));
				_cliente.setNomcli(String.valueOf(reg[14]));
				_cliente.setDircli(String.valueOf(reg[15]));
				
				Cliente aval = null;
				if (reg[16]!=null) {
					aval = new Cliente();
					aval.setCodcli(Integer.parseInt(String.valueOf(reg[16])));
					aval.setTipdoccli(String.valueOf(reg[17]));
					aval.setDoccli(String.valueOf(String.valueOf(reg[18])));
					aval.setNomcli(String.valueOf(reg[19]));
					aval.setDircli(String.valueOf(reg[20]));
				}
				
				Cliente otro = null;
				if (reg[21]!=null) {
					otro = new Cliente();
					otro.setCodcli(Integer.parseInt(String.valueOf(reg[21])));
					otro.setTipdoccli(String.valueOf(reg[22]));
					otro.setDoccli(String.valueOf(String.valueOf(reg[23])));
					otro.setNomcli(String.valueOf(reg[24]));
					otro.setDircli(String.valueOf(reg[25]));
				}
				
				Venta venta = new Venta();
				venta.setCodvta(String.valueOf(reg[5]));
				venta.setFecvta(LocalDate.parse(String.valueOf(reg[6])));
				venta.setCompag(String.valueOf(reg[7]));
				venta.setSerie(String.valueOf(reg[8]));
				venta.setCodcp(String.valueOf(reg[9]));
				venta.setFeccp(LocalDate.parse(String.valueOf(reg[10])));
				venta.setCliente(_cliente);
				venta.setAval(aval);
				venta.setClienteOtro(otro);
				
				Credito credito = new Credito();
				credito.setCodcre(_codcre);
				credito.setFeccre(LocalDate.parse(String.valueOf(reg[2])));
				credito.setMtocre(Double.parseDouble(String.valueOf(reg[3])));
				credito.setEstado(String.valueOf(reg[4]));
				credito.setVenta(venta);
				credito.setKarcre(new ArrayList<>());
				
				creditos.add(credito);
			}
//			log.info("_codcre:" + _codcre);
			
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            karcre.setDuserup(LocalDateTime.parse( String.valueOf(reg[24]).substring(0, 19), formatter ));
//			if (reg[25]!=null) karcre.setUsercr(String.valueOf(reg[25]));
//			if (reg[26]!=null) karcre.setDusercr(LocalDateTime.parse(String.valueOf(reg[26]).subSequence(0, 19), formatter ));
//			lKarcre.add(karcre);
			
		}
		
		for(Object[] reg: registros) {
			_codcre = String.valueOf(reg[1]);
			
			Karcre karcre = new Karcre();
			karcre.setCorrel(Integer.parseInt(String.valueOf(reg[26])));
			karcre.setEstado(String.valueOf(reg[27]));
			karcre.setFecha(LocalDate.parse(String.valueOf(reg[28])));
			karcre.setTipmov(String.valueOf(reg[29]));
			karcre.setCodmov(String.valueOf(reg[30]));
			karcre.setMtocre(Double.parseDouble(String.valueOf(reg[31])));
			karcre.setMtoamo(Double.parseDouble(String.valueOf(reg[32])));
			karcre.setMtoint(Double.parseDouble(String.valueOf(reg[33])));
			karcre.setMtoaju(Double.parseDouble(String.valueOf(reg[34])));
			
			for(Credito reg1: creditos) {
				if (reg1.getCodcre().equals(_codcre)) {
					reg1.getKarcre().add(karcre);
				}
			};
		}
		
		for (Credito credito:creditos) {
			Double totcre=0.0, totamo=0.0, totint=0.0, totaju=0.0, saldo=0.0;
			for (Karcre karcre: credito.getKarcre()) {
				if (karcre.getTipmov().equals("CRE")) {
					totcre += karcre.getMtocre();
				}
				if (karcre.getTipmov().equals("PAC")) {
					totamo += karcre.getMtoamo();
					totint += karcre.getMtoint();
				}
				if (karcre.getTipmov().equals("AJU")) {
					totaju += karcre.getMtoaju();
				}
				saldo = totcre + totint - totamo - totaju;
				karcre.setTotamo(totamo);
				karcre.setTotint(totint);
				karcre.setTotaju(totaju);
				karcre.setSaldo(saldo);
			}
		}
		return creditos;
	}
	
	@Override
	public List<Karcre> listarCreditosControlResumen(String codcre) {		// , String codcre, String tipmov, String codmov
		
		List<Karcre> resumen = new ArrayList<>();
		List<Object[]> registros = repo.listarCreditoControlResumen(codcre);
		for(Object[] reg: registros) {
			Credito credito = new Credito();
			credito.setCodcre(String.valueOf(reg[0]));
			credito.setFeccre(LocalDate.parse(String.valueOf(reg[1])));
			credito.setMtocre(Double.parseDouble(String.valueOf(reg[2])));
			credito.setKarcre(new ArrayList<>());
			
			Karcre karcre = new Karcre();
			karcre.setCredito(credito);
			karcre.setMtocre(Double.parseDouble(String.valueOf(reg[2])));
			karcre.setMtoint(Double.parseDouble(String.valueOf(reg[3])));
			karcre.setTotint(Double.parseDouble(String.valueOf(reg[3])));
			karcre.setMtoaju(Double.parseDouble(String.valueOf(reg[4])));
			karcre.setTotaju(Double.parseDouble(String.valueOf(reg[4])));
			karcre.setMtoamo(Double.parseDouble(String.valueOf(reg[5])));
			karcre.setTotamo(Double.parseDouble(String.valueOf(reg[5])));
			Double saldo = karcre.getMtocre() + karcre.getTotint() - karcre.getTotamo() - karcre.getTotaju();
			karcre.setSaldo(saldo);
			resumen.add(karcre);
		}
		
		return resumen;
	}
	
	@Override
	public byte[] reporteCreditosControl(Integer anno, Integer mes, String cliente, String estado) throws Exception {
		List<Credito> creditos = listarCreditosControl(anno, mes, cliente, estado, -1, 0);
		Map<String, Object> parametros = new HashMap<String, Object>();
		Sucursal sucursal = servSucursal.listarPorId(1);

		parametros.put("creditos", creditos);
		parametros.put("sucursal", sucursal);
		
		PDFCreditosControl pdfCreditosControl = new PDFCreditosControl(parametros);
		
		return pdfCreditosControl.creaReporte();
	}
	
	@Override
	public Credito buscaCredito(String codcre) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
