package com.rosist.difunto.venta.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.dto.AjusteDto;
import com.rosist.difunto.venta.model.Ajuste;
import com.rosist.difunto.venta.model.Cliente;
import com.rosist.difunto.venta.model.Credito;
import com.rosist.difunto.venta.model.Pagacta;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.IAjusteRepo;
import com.rosist.difunto.venta.repo.IClienteRepo;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.service.IAjusteService;
import com.rosist.difunto.venta.service.ICreditoService;
import com.rosist.difunto.venta.service.IKarcreService;

@Repository
public class AjusteServiceImpl extends CRUDImpl<Ajuste, String> implements IAjusteService {

	@Autowired
	private IAjusteRepo repo;
	
	@Autowired
	private ICreditoService servCredito;
	
	@Autowired
	private IClienteRepo repoCliente;
	
	@Autowired
	private IKarcreService servKarcre;
	
	@Override
	protected IGenericRepo<Ajuste, String> getRepo() {
		return repo;
	}
	
	@Override
	public List<Ajuste> listarAjuste(Integer anno, Integer mes, String cliente, String estado, Integer page,
			Integer size) throws Exception {
		List<Ajuste> ajustes = new ArrayList<>();
		List<Object[]> registros = repo.listarAjuste(anno, mes, cliente, estado, page, size);
		
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
			
			Ajuste ajuste = new Ajuste();
			ajuste.setCodaju(String.valueOf(reg[0]));
			ajuste.setFecaju(LocalDate.parse(String.valueOf(reg[1])));
			ajuste.setCredito(credito);
			ajuste.setCompag(String.valueOf(reg[20]));
			ajuste.setSerie(String.valueOf(reg[21]));
			ajuste.setCodcp(String.valueOf(reg[22]));
			ajuste.setFeccp(LocalDate.parse(String.valueOf(reg[23])));
			ajuste.setCliente(clientePac);
			
//			ajuste.setInteres(Double.parseDouble(String.valueOf(reg[29])));
//			ajuste.setMtoamo(Double.parseDouble(String.valueOf(reg[30])));
			ajuste.setMtoaju(Double.parseDouble(String.valueOf(reg[31])));
			ajuste.setObserv(String.valueOf(reg[32]));
			ajuste.setEstado(String.valueOf(reg[33]));
			
			ajustes.add(ajuste);
		});
		
		return ajustes;
	}

	@Override
	public Ajuste buscaAjuste(String idPagacta) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Ajuste registraTransaccion(AjusteDto ajusteDto) throws SQLException, Exception {
		Ajuste ajuste = new Ajuste();
//		String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String cUser = "cambiame";
		LocalDateTime dUser = LocalDateTime.now();

		if (ajusteDto.getCodaju()==null) {
			String mes = String.format("%1$02d",ajusteDto.getFecaju().getMonth().getValue());
			String anno = String.valueOf(ajusteDto.getFecaju().getYear());
			String idAjuste = repo.idAjuste(anno, mes);
			ajuste.setCodaju(idAjuste);
		}
		ajuste.setFecaju(ajusteDto.getFecaju());
		
		String codcre = ajusteDto.getCredito().getCodcre();
		Credito credito = servCredito.listarPorId(codcre);
		if (credito!=null) {
			ajuste.setCredito(credito);
		}
		
		ajuste.setCompag(ajusteDto.getCompag());
		ajuste.setSerie(ajusteDto.getSerie());
		ajuste.setCodcp(ajusteDto.getCodcp());
		ajuste.setFeccp(ajusteDto.getFeccp());
		
		Cliente cliente = repoCliente.buscaProTipo(ajusteDto.getCliente().getTipdoccli(), ajusteDto.getCliente().getDoccli());
		if (cliente==null) {
			if (ajusteDto.getCliente().getCodcli()==null) {
				ajusteDto.getCliente().setCodcli(repoCliente.getNewId());
			}
			ajuste.setCliente(ajusteDto.getCliente());
			repoCliente.save(ajuste.getCliente());
		} else if (cliente.getCodcli()!=ajusteDto.getCliente().getCodcli()) {
			ajuste.setCliente(cliente); 
		}
		
//		ajuste.setInteres(ajusteDto.getInteres());
//		ajuste.setMtoamo(ajusteDto.getMtoamo());
		ajuste.setMtoaju(ajusteDto.getMtoaju());
		ajuste.setObserv(ajusteDto.getObserv());
		ajuste.setEstado("00");
		ajuste.setUserup(cUser);
		ajuste.setDuserup(dUser);
		
		ajuste = repo.save(ajuste);
		
		if (ajuste!=null) {
			servKarcre.actualizaKardex(ajuste);
		}
		return ajuste;
	}

	@Override
	public Ajuste modificaTransaccion(Ajuste ajuste) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}