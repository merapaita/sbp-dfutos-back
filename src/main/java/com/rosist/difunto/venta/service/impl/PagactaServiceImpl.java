package com.rosist.difunto.venta.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.dto.PagactaDto;
import com.rosist.difunto.venta.model.Cliente;
import com.rosist.difunto.venta.model.Credito;
import com.rosist.difunto.venta.model.Karcre;
import com.rosist.difunto.venta.model.Pagacta;
import com.rosist.difunto.venta.model.Subvencion;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.IClienteRepo;
import com.rosist.difunto.venta.repo.ICreditoRepo;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.repo.IKarcreRepo;
import com.rosist.difunto.venta.repo.IPagactaRepo;
import com.rosist.difunto.venta.repo.IVentaRepo;
import com.rosist.difunto.venta.service.ICreditoService;
import com.rosist.difunto.venta.service.IKarcreService;
import com.rosist.difunto.venta.service.IPagactaService;
import com.rosist.difunto.venta.service.IVentaService;

import jakarta.transaction.Transactional;

@Repository
public class PagactaServiceImpl extends CRUDImpl<Pagacta, String> implements IPagactaService {

	@Autowired
	private IPagactaRepo repo;
	
	@Autowired
	private IVentaRepo repoVenta;
	
	@Autowired
	private ICreditoRepo repoCredito;
	
	@Autowired
	private ICreditoService servCredito;
	
	@Autowired
	private IVentaService servVenta;
	
	@Autowired
	private IClienteRepo repoCliente;
	
	@Autowired
	private IKarcreService servKarcre;
	
	@Override
	protected IGenericRepo<Pagacta, String> getRepo() {
		return repo;
	}
	
	private static final Logger log = LoggerFactory.getLogger(PagactaServiceImpl.class);

	@Override
	public List<Pagacta> listarPagacta(Integer anno, Integer mes, String cliente, String estado, Integer page,
			Integer size) throws Exception {
		List<Pagacta> pagos = new ArrayList<>();
		List<Object[]> registros = repo.listarVenta(anno, mes, cliente, estado, page, size);
		
		registros.forEach(reg -> {
			Cliente clienteVenta = new Cliente();
			clienteVenta.setCodcli(Integer.parseInt(String.valueOf(reg[15])));
			clienteVenta.setTipdoccli(String.valueOf(reg[16]));
			clienteVenta.setDoccli(String.valueOf(reg[17]));
			clienteVenta.setNomcli(String.valueOf(reg[18]));
			clienteVenta.setDircli(String.valueOf(reg[19]));
			
			Venta venta = new Venta();
			venta.setCodvta(String.valueOf(reg[6]));
			venta.setFecvta(LocalDate.parse(String.valueOf(reg[7])));
			venta.setTipvta(String.valueOf(reg[8]));
			if (reg[9] != null) {
				venta.setNvotipvta(String.valueOf(reg[9]));
			}
			venta.setConvta(String.valueOf(reg[10]));
			venta.setCompag(String.valueOf(reg[11]));
			venta.setSerie(String.valueOf(reg[12]));
			venta.setCodcp(String.valueOf(reg[13]));
			venta.setFeccp(LocalDate.parse(String.valueOf(reg[14])));
			venta.setCliente(clienteVenta);
			
			Credito credito = new Credito();
			credito.setCodcre(String.valueOf(reg[2]));
			credito.setFeccre(LocalDate.parse(String.valueOf(reg[3])));
			credito.setMtocre(Double.parseDouble(String.valueOf(reg[4])));
			credito.setEstado(String.valueOf(reg[5]));
			credito.setVenta(venta);
			
			Cliente clientePac = new Cliente();
			clienteVenta.setCodcli(Integer.parseInt(String.valueOf(reg[24])));
			clienteVenta.setTipdoccli(String.valueOf(reg[25]));
			clienteVenta.setDoccli(String.valueOf(reg[26]));
			clienteVenta.setNomcli(String.valueOf(reg[27]));
			clienteVenta.setDircli(String.valueOf(reg[28]));

			Pagacta pagacta = new Pagacta();
			pagacta.setCodpac(String.valueOf(reg[0]));
			pagacta.setFecpac(LocalDate.parse(String.valueOf(reg[1])));
			pagacta.setCredito(credito);
			pagacta.setCompag(String.valueOf(reg[20]));
			pagacta.setSerie(String.valueOf(reg[21]));
			pagacta.setCodcp(String.valueOf(reg[22]));
			pagacta.setFeccp(LocalDate.parse(String.valueOf(reg[23])));
			pagacta.setCliente(clientePac);
			
			pagacta.setInteres(Double.parseDouble(String.valueOf(reg[29])));
			pagacta.setMtoamo(Double.parseDouble(String.valueOf(reg[30])));
			pagacta.setMtoaju(Double.parseDouble(String.valueOf(reg[31])));
			pagacta.setObserv(String.valueOf(reg[32]));
			pagacta.setEstado(String.valueOf(reg[33]));
			
			pagos.add(pagacta);
		});
		
		return pagos;
	}

	@Override
	public Pagacta buscaPagacta(String idPagacta) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	@Override
	public Pagacta registraTransaccion(PagactaDto pagactaDto) throws SQLException, Exception {
		Pagacta pagacta = new Pagacta();
//		String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String cUser = "cambiame";
		LocalDateTime dUser = LocalDateTime.now();

		if (pagactaDto.getCodpac()==null) {
			String mes = String.format("%1$02d",pagactaDto.getFecpac().getMonth().getValue());
			String anno = String.valueOf(pagactaDto.getFecpac().getYear());
			String idPagacta = repo.idPagacta(anno, mes);
			pagacta.setCodpac(idPagacta);
		}
		pagacta.setFecpac(pagactaDto.getFecpac());
		
		String codcre = pagactaDto.getCredito().getCodcre();
		Credito credito = servCredito.listarPorId(codcre);
		if (credito!=null) {
			pagacta.setCredito(credito);
		}
		
		pagacta.setCompag(pagactaDto.getCompag());
		pagacta.setSerie(pagactaDto.getSerie());
		pagacta.setCodcp(pagactaDto.getCodcp());
		pagacta.setFeccp(pagactaDto.getFeccp());
		
		Cliente cliente = repoCliente.findById(pagactaDto.getCliente().getCodcli()).orElse(null);
//		Cliente cliente = repoCliente.buscaProTipo(pagactaDto.getCliente().getTipdoccli(), pagactaDto.getCliente().getDoccli());
		if (cliente==null) {
			pagacta.setCliente(pagactaDto.getCliente());
			repoCliente.save(pagacta.getCliente());
		} else {
			pagacta.setCliente(cliente); 
		}
		
		List<Karcre> saldos = servCredito.listarCreditosControlResumen(codcre);
		Double saldoAnterior = saldos.get(0).getSaldo();
		Double saldoActual = 0.0;
		
		pagacta.setInteres(pagactaDto.getInteres());
		pagacta.setMtoamo(pagactaDto.getMtoamo());
		pagacta.setMtoaju(0.0);
		pagacta.setObserv(pagactaDto.getObserv());
		pagacta.setEstado("00");
		
		saldoActual = saldoAnterior + pagacta.getInteres() - pagacta.getMtoaju() - pagacta.getMtoamo();
		String estadoVenta = "20";
		if (saldoActual<=0.0) {
			estadoVenta = "10";
			Venta venta = servVenta.listarPorId(credito.getVenta().getCodvta());
			venta.setEstado(estadoVenta);
			credito.setEstado(estadoVenta);
			repoVenta.save(venta);
			repoCredito.save(credito);
		}
		log.info("SaldoAnterior:" + saldoAnterior + " saldoActual:" + saldoActual);			
		
		pagacta.setUserup(cUser);
		pagacta.setDuserup(dUser);
		pagacta = repo.save(pagacta);
		
		return pagacta;
		
	}
	
	@Override
	public Pagacta modificaTransaccion(Pagacta pagacta) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
