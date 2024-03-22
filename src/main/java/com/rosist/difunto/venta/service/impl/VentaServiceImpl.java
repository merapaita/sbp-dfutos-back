package com.rosist.difunto.venta.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.model.Cliente;
import com.rosist.difunto.venta.model.Credito;
import com.rosist.difunto.venta.model.Donacion;
import com.rosist.difunto.venta.model.Subvencion;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.IClienteRepo;
import com.rosist.difunto.venta.repo.ICreditoRepo;
import com.rosist.difunto.venta.repo.IDonacionRepo;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.repo.ISubvencionRepo;
import com.rosist.difunto.venta.repo.IVentaRepo;
import com.rosist.difunto.venta.service.IClienteService;
import com.rosist.difunto.venta.service.ICreditoService;
import com.rosist.difunto.venta.service.IDonacionService;
import com.rosist.difunto.venta.service.IKarcreService;
import com.rosist.difunto.venta.service.ISubvencionService;
import com.rosist.difunto.venta.service.IVentaService;

import jakarta.transaction.Transactional;

@Service
public class VentaServiceImpl extends CRUDImpl<Venta, String> implements IVentaService {

	@Autowired
	private IVentaRepo repo;

	@Autowired
	private ICreditoRepo repoCredito;

	@Autowired
	private ICreditoService servCredito;

	@Autowired
	private IDonacionRepo repoDonacion;

	@Autowired
	private IDonacionService servDonacion;

	@Autowired
	private ISubvencionRepo repoSubvencion;

	@Autowired
	private ISubvencionService servSubvencion;

	@Autowired
	private IClienteRepo repoCliente;

	@Autowired
	private IClienteService servCliente;

	@Autowired
	private IKarcreService servKarcre;

//	@Autowired
//	private IParmaeService serviceParmae;

	@Override
	protected IGenericRepo<Venta, String> getRepo() {
		return repo;
	}

	private static Logger logger = LoggerFactory.getLogger(VentaServiceImpl.class);

	@Override
	public List<Venta> listarVentas(Integer anno, Integer mes, String _cliente, String estado, Integer page,
			Integer size) throws Exception {
		logger.info("listarVenta...: anno:" + anno + " mes:" + mes + " _cliente:" + _cliente + " estado:" + estado + " page:" + page + " size:" + size);
		List<Venta> ventas = new ArrayList<>();
		List<Object[]> registros = repo.listarVenta(anno, mes, _cliente, estado, page, size);

//logger.info("registros:" + registros);		
		registros.forEach(reg -> {

			Venta venta = new Venta();
			venta.setCodvta(String.valueOf(reg[0]));
			venta.setFecvta(LocalDate.parse(String.valueOf(reg[1])));
			venta.setTipvta(String.valueOf(reg[2]));
			if (reg[3] != null) {
				venta.setNvotipvta(String.valueOf(reg[3]));
			}
			venta.setConvta(String.valueOf(reg[4]));
			venta.setCompag(String.valueOf(reg[5]));
			venta.setSerie(String.valueOf(reg[6]));
			venta.setCodcp(String.valueOf(reg[7]));
			venta.setFeccp(LocalDate.parse(String.valueOf(reg[8])));

			Cliente cliente = new Cliente();
			cliente.setCodcli(Integer.parseInt(String.valueOf(reg[9])));
			cliente.setTipdoccli(String.valueOf(reg[10]));
			cliente.setDoccli(String.valueOf(reg[11]));
			cliente.setNomcli(String.valueOf(reg[12]));
			cliente.setDircli(String.valueOf(reg[13]));
			venta.setCliente(cliente);

			if (reg[24] != null) {
				Cliente aval = new Cliente();
				aval.setCodcli(Integer.parseInt(String.valueOf(reg[24])));
				aval.setTipdoccli(String.valueOf(reg[25]));
				aval.setDoccli(String.valueOf(reg[26]));
				aval.setNomcli(String.valueOf(reg[27]));
				aval.setDircli(String.valueOf(reg[28]));
				venta.setAval(aval);
			}

//			if (venta.getConvta().equals("2")) {
//				Credito credito = new Credito();
//				venta.setCredito(credito);
//			}
			if (reg[19] != null) {
				venta.setBcredito(Boolean.parseBoolean(String.valueOf(reg[19])));
			}
			if (reg[29] != null) {
				venta.setBsubvencion(Boolean.parseBoolean(String.valueOf(reg[29])));
			}
			if (reg[30] != null) {
				venta.setBdonacion(Boolean.parseBoolean(String.valueOf(reg[30])));
			}
			if (reg[50] != null) {
				venta.setBcambioNombre(Boolean.parseBoolean(String.valueOf(reg[50])));
			}
			if (venta.isBcredito()) {
				Credito credito = new Credito();
				credito.setCodcre(String.valueOf(reg[20]));
				credito.setFeccre(LocalDate.parse(String.valueOf(reg[21])));
				credito.setMtocre(Double.parseDouble(String.valueOf(reg[22])));
				credito.setEstado(String.valueOf(reg[23]));
				venta.setCredito(credito);
			}
			List<Subvencion> subvenciones = new ArrayList<>();
			if (venta.isBsubvencion()) {
				try {
					subvenciones = servSubvencion.listarSubvenciones(venta.getCodvta(), "", "", -1, 0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			venta.setSubvenciones(subvenciones);

			if (venta.isBdonacion()) {
				Donacion donacion = new Donacion();
				donacion.setCoddon(String.valueOf(reg[31]));
				donacion.setFecdon(LocalDate.parse(String.valueOf(reg[32])));
				donacion.setDocref(String.valueOf(reg[33]));
				donacion.setNumref(String.valueOf(reg[34]));
				donacion.setFecref(LocalDate.parse(String.valueOf(reg[35])));
				donacion.setExpediente(String.valueOf(reg[36]));
				donacion.setMtodon(Double.parseDouble(String.valueOf(reg[37])));
				donacion.setEstado(String.valueOf(reg[38]));
				venta.setDonacion(donacion);
			}
			if (venta.isBcambioNombre()) {
				if (reg[14] != null) {
					Cliente clienteOtro = new Cliente();
					clienteOtro.setCodcli(Integer.parseInt(String.valueOf(reg[14])));
					clienteOtro.setTipdoccli(String.valueOf(reg[15]));
					clienteOtro.setDoccli(String.valueOf(reg[16]));
					clienteOtro.setNomcli(String.valueOf(reg[17]));
					clienteOtro.setDircli(String.valueOf(reg[18]));
					venta.setClienteOtro(clienteOtro);
				}
			}
//			venta.setDonacion(donacion);
			venta.setMtovta(Double.parseDouble(String.valueOf(reg[39])));
			venta.setMtodon(Double.parseDouble(String.valueOf(reg[40])));
			venta.setMtosub(Double.parseDouble(String.valueOf(reg[41])));
			venta.setMtocan(Double.parseDouble(String.valueOf(reg[42])));
			venta.setMtocre(Double.parseDouble(String.valueOf(reg[43])));
			venta.setObserv(String.valueOf(reg[44]));
			venta.setEstado(String.valueOf(reg[45]));
			venta.setCodcem(String.valueOf(reg[46]));
			venta.setNomben(String.valueOf(reg[47]));
			venta.setCuartel(String.valueOf(reg[48]));
			venta.setNicho(String.valueOf(reg[49]));

////			venta.setDesTipcon(lTipcon.stream()
////					.filter(xx -> xx.getCodigo().equals(String.valueOf(reg[1])))
////					.collect(Collectors.toList()).get(0).getDescri());
////			
//			venta.setEstado(String.valueOf(reg[42]));
//			venta.setDesEstado(estados_venta.stream()
//					.filter(xx -> xx.getCodigo().equals(String.valueOf(reg[13])))
//					.collect(Collectors.toList()).get(0).getDescri());

			ventas.add(venta);
		});
		logger.info("ListaVentas:" + ventas);
		return ventas;
	}

	@Transactional
	@Override
	public Venta registraTransaccion(Venta venta) throws SQLException, Exception {
//		String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String cUser = "cambiame";
		LocalDateTime dUser = LocalDateTime.now();
		boolean agrCliente = false;

		if (venta.getCodvta() == null) {
			String mes = String.format("%1$02d", venta.getFecvta().getMonth().getValue());
			String anno = String.valueOf(venta.getFecvta().getYear());
			String idVenta = repo.idVenta(anno, mes);
			venta.setCodvta(idVenta);
		}
		if (venta.getTipvta() != null) {
			// algo tedra que hacer
		}
		if (venta.getNvotipvta() != null) {
			// algo tedra que hacer
		}
		if (venta.getConvta() != null) {
			// algo tedra que hacer
		}
		Cliente cliente = repoCliente.buscaProTipo(venta.getCliente().getTipdoccli(), venta.getCliente().getDoccli());
		if (cliente == null) {
			repoCliente.save(venta.getCliente());
		} else if (cliente.getCodcli() != venta.getCliente().getCodcli()) {
			venta.setCliente(cliente);
		}
		if (venta.getAval() != null) {
			Cliente aval = repoCliente.buscaProTipo(venta.getAval().getTipdoccli(), venta.getAval().getDoccli());
			if (aval == null) {
				repoCliente.save(venta.getAval());
			} else if (aval.getCodcli() != venta.getAval().getCodcli()) {
				venta.setAval(aval);
			}
		}

		if (venta.getClienteOtro() != null) {
			Optional<Cliente> clienteOtro = repoCliente.findById(venta.getClienteOtro().getCodcli());
			if (clienteOtro.isEmpty()) {
				repoCliente.save(venta.getCliente());
			}
		}
		if (venta.isBcredito()) {
			venta.setEstado("20");
			if (venta.getCredito().getCodcre() == null) {
				String mes = String.format("%1$02d", venta.getFecvta().getMonth().getValue());
				String anno = String.valueOf(venta.getFecvta().getYear());
				String idCredito = repoCredito.idCredito(anno, mes);
				venta.getCredito().setCodcre(idCredito);
			}
			venta.getCredito().setEstado("20");
			venta.getCredito().setUserup(cUser);
			venta.getCredito().setDuserup(dUser);
			venta.getCredito().setVenta(venta);
		} else {
			venta.setEstado("10");
		}

		venta.setUserup(cUser);
		venta.setDuserup(dUser);
		if (venta.isBdonacion()) {
			if (venta.getDonacion().getCoddon() == null) {
				String mes = String.format("%1$02d", venta.getFecvta().getMonth().getValue());
				String anno = String.valueOf(venta.getFecvta().getYear());
				String idDonacion = repoDonacion.idDonacion(anno, mes);
				venta.getDonacion().setCoddon(idDonacion);
			}
			venta.getDonacion().setEstado("20");
			venta.getDonacion().setUserup(cUser);
			venta.getDonacion().setDuserup(dUser);
			venta.getDonacion().setVenta(venta);
		}
		List<Subvencion> subvenciones = null;
		if (venta.isBsubvencion()) {
			subvenciones = venta.getSubvenciones();
			venta.setSubvenciones(null);
//			int _mes = venta.getFecvta().getMonth().getValue();
//			int _anno = venta.getFecvta().getYear();
//			Venta _venta = venta;
//			venta.getSubvenciones().forEach(reg -> {
//				String mes = String.format("%1$02d", _mes);
//				String anno = String.valueOf(_anno);
//				String idSubvencion = repoSubvencion.idSubvencion(anno, mes);
//				reg.setCodsub(idSubvencion);
//				reg.setEstado("20");
//				reg.setUserup(cUser);
//				reg.setDuserup(dUser);
//				reg.setVenta(_venta);
//				repoSubvencion.save(reg);
//			});
		}
		Venta _venta = repo.save(venta);
//		if (_venta != null) {
//			if (_venta.isBcredito()) {
//				servKarcre.actualizaKardex(_venta);
//			}
//		}
		if (_venta!=null) {
			if (venta.isBsubvencion()) {
				subvenciones.forEach(reg -> {
					if (reg.getCodsub()==null) {
						String mes = String.format("%1$02d", _venta.getFecvta().getMonth().getValue());
						String anno = String.valueOf(_venta.getFecvta().getYear());
						String idSubvencion = repoSubvencion.idSubvencion(anno, mes);
						reg.setCodsub(idSubvencion);
					}
					reg.setEstado("20");
					reg.setUserup(cUser);
					reg.setDuserup(dUser);
					reg.setVenta(_venta);
//					logger.info("antes de grabar");
					repoSubvencion.save(reg);
				});
			}
		}

		venta = listarPorId(_venta.getCodvta());
		return venta;

	}

//	@Override
//	public Venta buscaVenta(String idVenta) throws Exception {
////		logger.info("listarVenta...idVenta:" + idVenta + " tipcon:" + tipcon + " apepat:" + apepat + " apemat:" + apemat + " nombres:" + nombres);
//		List<Venta> lVenta = new ArrayList<>();
//		List<Object[]> registros = repo.listarVenta(idVenta, "", 0, "", "", "", -1, 0);
//
//		registros.forEach(reg -> {
//			Promotor promotor = new Promotor();
//			promotor.setIdPromotor(Integer.parseInt(String.valueOf(reg[4])));
//			promotor.setApePaterno(String.valueOf(reg[5]));
//			promotor.setApeMaterno(String.valueOf(reg[6]));
//			promotor.setNombres(String.valueOf(reg[7]));
//			promotor.setDireccion(String.valueOf(reg[8]));
//
//			Venta venta = new Venta();
//			venta.setIdVenta(Integer.parseInt(String.valueOf(reg[0])));
//			venta.setTipcon(String.valueOf(reg[1]));
//			venta.setCodcon(Integer.parseInt(String.valueOf(reg[2])));
//			venta.setFecha(LocalDate.parse(String.valueOf(reg[3])));
//			venta.setPromotor(promotor);
//			venta.setInscripcion(Double.parseDouble(String.valueOf(reg[9])));
//			venta.setMtotot(Double.parseDouble(String.valueOf(reg[10])));
//			venta.setCuota(Double.parseDouble(String.valueOf(reg[11])));
//			venta.setNumcuotas(Integer.parseInt(String.valueOf(reg[12])));
//			venta.setEstado(String.valueOf(reg[13]));
//
//			Socio titular = new Socio();
//			if (reg[14] != null) {
//				titular.setIdSocio(Integer.parseInt(String.valueOf(reg[14])));
//				titular.setTipoDocumento(String.valueOf(reg[15]));
//				titular.setDocumento(String.valueOf(reg[16]));
//				titular.setApePaterno(String.valueOf(reg[17]));
//				titular.setApeMaterno(String.valueOf(reg[18]));
//				titular.setNombres(String.valueOf(reg[19]));
//				titular.setDireccion(String.valueOf(reg[20]));
//				titular.setFecnac(LocalDate.parse(String.valueOf(reg[21])));
//				titular.setEdad(Integer.valueOf(String.valueOf(reg[22])));
//				titular.setTelefono(String.valueOf(reg[23]));
//				titular.setEstado(String.valueOf(reg[24]));
//			}
//
//			venta.setTitular(titular);
//
//			lVenta.add(venta);
//		});
//		
//		return (lVenta.size()>0?lVenta.get(0):null);
//	}
//	

//	@Override
//	public Venta modificaTransaccion(Venta venta) throws Exception {
//		String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
//		LocalDateTime dUser = LocalDateTime.now();
//		Venta _venta = repo.getById(venta.getIdVenta());
//
//		if (_venta.getBaja()!=null) {
//			throw new Excepciones("Este Venta ya fue dado de baja y no puede ser modificado.");
//		}
////		logger.info("venta" + venta);
//		venta.getDetItecon().forEach(det -> {
//			if (det.getIdItecon() == null) {
//				if (det.getSocio().getIdSocio() == 0) {
//					Socio socio = det.getSocio();
//					socio.setEstado("00");
//					socio.setUserup(cUser);
//					socio.setDuserup(dUser);
//					socio = repoSocio.save(det.getSocio());
////					logger.info("registrarTransaccion...socio grabado.-> ");
//					det.setSocio(socio);
//				}
//				det.setVenta(venta);
//				det.setEstado("00");
//				det.setUserup(cUser);
//				det.setDuserup(dUser);
//				if (det.getFecins() == null) {
//					det.setFecins(venta.getFecha());
//				}
//				det.setFecder(venta.getFecha().plusMonths(4));
//				_venta.getDetItecon().add(det);
//			}
//		});
//		return repo.save(_venta);
//	}
//	
//	public byte[] ventaPDF(Integer idVenta) throws Exception {
//		Venta venta = repo.getById(idVenta);
//		List<Parmae> estados = serviceParmae.listaPorTipo("ESTCON");
//		List<Parmae> parent  = serviceParmae.listaPorTipo("PARENT");
//		
//		if (venta == null) {
//			throw new ModelNotFoundException("CONTRATO NO ENCONTRADO ");
//		}
//		venta.getDetItecon().forEach(x -> {
//			x.getSocio().setNombre(x.getSocio().getApePaterno() + " " + x.getSocio().getApeMaterno() + ", " + x.getSocio().getNombres());
//		});
//		List<Itecon> lItecon = venta.getDetItecon().stream()
//				.filter(det -> det.getEstado().equals("00") && det.getParentesco().equals("00"))
//				.collect(Collectors.toList());
//		if (lItecon.size()>1) {
//			throw new ModelNotFoundException("No deben Existir mas de un Titular en el venta ");
//		} else if (lItecon.size()>0) {
//			venta.setTitular(lItecon.get(0).getSocio());
//		}
////		logger.info("venta:" );
//		Map<String, Object> parametros = new HashMap<String, Object>();
//		Sucursal sucursal = repoSucursal.getById(1);
//		parametros.put("venta", venta);
//		parametros.put("sucursal", sucursal);
//		parametros.put("estados", estados);
//		parametros.put("parent", parent);
////		logger.info("antes de pdf:" );
//		
//        PDFVenta pdfventa = new PDFVenta(parametros);
//        
//		return pdfventa.creaReporte();
//	}

	@Override
	public Venta modificaTransaccion(Venta venta) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Venta buscaVenta(String idVenta) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}