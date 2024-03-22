package com.rosist.difunto.venta.service.impl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.model.Donacion;
import com.rosist.difunto.venta.model.Pagdon;
import com.rosist.difunto.venta.model.Pagsub;
import com.rosist.difunto.venta.model.Parmae;
import com.rosist.difunto.venta.model.Subvencion;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.repo.IPagsubRepo;
import com.rosist.difunto.venta.service.IDonacionService;
import com.rosist.difunto.venta.service.IPagsubService;
import com.rosist.difunto.venta.service.IParmaeService;
import com.rosist.difunto.venta.service.ISubvencionService;

@Repository
public class PagsubServiceImpl extends CRUDImpl<Pagsub, String> implements IPagsubService {

	@Autowired
	private IPagsubRepo repo;

	@Autowired
	private ISubvencionService servSubvencion;

	@Autowired
	private IParmaeService servParmae;
	
	@Override
	protected IGenericRepo<Pagsub, String> getRepo() {
		return repo;
	}

	private static final Logger log = LoggerFactory.getLogger(PagsubServiceImpl.class);

	@Override
	public List<Pagsub> listarPagsub(Integer anno, Integer mes, Integer page, Integer size) throws Exception {
		List<Pagsub> pagos = new ArrayList<>();
		List<Object[]> registros = repo.listarPagsub(anno, mes, page, size);
		List<Parmae> entidades = servParmae.listarParmae("", "ENTSUB", "", -1, 0);

		registros.forEach(reg -> {
			Pagsub pagsub = Pagsub.builder().codps(String.valueOf(reg[0]))
					.fecps(LocalDate.parse(String.valueOf(reg[1]))).codent(String.valueOf(reg[2]))
					.codcp(String.valueOf(reg[3])).cheque(String.valueOf(reg[4]))
					.mtotot(Double.parseDouble(String.valueOf(reg[5]))).observ(String.valueOf(reg[6])).build();

			pagsub.setDesCodent(entidades.stream().filter(xx -> xx.getId().getCodigo().equals(String.valueOf(reg[2])))
					.collect(Collectors.toList()).get(0).getDescri());

			pagos.add(pagsub);
		});
		return pagos;
	}

	@Override
	public Pagsub buscaPagsub(String idPagsub) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pagsub registraTransaccion(Pagsub pagsub) throws SQLException, Exception {
//		String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String cUser = "cambiame";
		LocalDateTime dUser = LocalDateTime.now();

		Pagsub _pagsub = new Pagsub();

		if (pagsub.getCodps() == null) {
			String anno = String.valueOf(pagsub.getFecps().getYear());
			String idPagsub = repo.idPagsub(anno);
			_pagsub.setCodps(idPagsub);
		} else {
			_pagsub.setCodps(pagsub.getCodps());
		}
		_pagsub.setFecps(pagsub.getFecps());
		_pagsub.setCodent(pagsub.getCodent());
		_pagsub.setCodcp(pagsub.getCodcp());
		_pagsub.setCheque(pagsub.getCheque());
		_pagsub.setObserv(pagsub.getObserv());
		_pagsub.setEstado("00");
		_pagsub.setUserup(cUser);
		_pagsub.setDuserup(dUser);
		_pagsub.setSubvenciones(new ArrayList<>());
		pagsub.getSubvenciones().forEach(reg -> {
			if (reg.isPagado()) {
				try {
					String codsub = reg.getCodsub();
					Subvencion subvencion = servSubvencion.listarPorId(codsub);
					subvencion.setPagado(true);
					subvencion.setEstado("10");
					subvencion.setPagsub(_pagsub);
					_pagsub.getSubvenciones().add(subvencion);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		Double mtotot = _pagsub.getSubvenciones().stream().mapToDouble(Subvencion::getMtosub).sum();
		_pagsub.setMtotot(mtotot);
		return repo.save(_pagsub);
	}

	@Override
	public Pagsub modificaTransaccion(Pagsub pagsub) throws Exception {
//		String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String cUser = "cambiame";
		LocalDateTime dUser = LocalDateTime.now();

		Pagsub _pagsub = repo.findById(pagsub.getCodps()).orElse(null);

		if (_pagsub == null) {
			throw new Exception("No existe Pago de donacion a corregir");
		}

		_pagsub.setFecps(pagsub.getFecps());
		_pagsub.setCodcp(pagsub.getCodcp());
		_pagsub.setCheque(pagsub.getCheque());
		_pagsub.setObserv(pagsub.getObserv());
		_pagsub.setUsercr(cUser);
		_pagsub.setDusercr(dUser);

		List<Subvencion> _ItemsAgregados = new ArrayList<Subvencion>();
		List<Subvencion> _ItemsNoAgregados = new ArrayList<Subvencion>();

		pagsub.getSubvenciones().forEach(reg -> {
			if (reg.isPagado() == true) {
				try {
					Subvencion subvencion = servSubvencion.listarPorId(reg.getCodsub());
					_ItemsAgregados.add(subvencion);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					Subvencion subvencion = servSubvencion.listarPorId(reg.getCodsub());
					_ItemsNoAgregados.add(subvencion);
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
					regAgregado.setPagsub(_pagsub);
					_pagsub.getSubvenciones().add(regAgregado);
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
					regNoAgregado.setPagsub(null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		Double mtotot = _pagsub.getSubvenciones().stream()
				.filter(reg -> reg.isPagado()==true)
				.mapToDouble(Subvencion::getMtosub).sum();
		_pagsub.setMtotot(mtotot);
		Pagsub pagsubGrabados = repo.save(_pagsub);
		return pagsubGrabados;
	}

	
}
