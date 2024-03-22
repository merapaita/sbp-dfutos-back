package com.rosist.difunto.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rosist.difunto.dao.CuartelDao;
import com.rosist.difunto.dao.MausoleoDao;
import com.rosist.difunto.modelSbp.Cuartel;
import com.rosist.difunto.modelSbp.Mausoleo;
import com.rosist.difunto.reports.PdfCuartel;
import com.rosist.difunto.reports.PdfCuartelNichos;
import com.rosist.difunto.reports.PdfCuartelNichosEstados;
import com.rosist.difunto.reports.PdfMausoleo;
import com.rosist.difunto.reports.PdfMausoleoDifuntos;
import com.rosist.difunto.reports.XlsMausoleo;
import com.rosist.difunto.reports.XlsMausoleoDifuntos;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/mausoleo")
public class MausoleoController {

	@Autowired
	private MausoleoDao daoMausoleo;

    @Autowired
    private DriverManagerDataSource datasource;

	private Logger logger = LoggerFactory.getLogger(MausoleoController.class);

	@GetMapping
	public ResponseEntity<List<Mausoleo>> listar(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codmau", defaultValue = "0") Integer codmau,
			@RequestParam(value = "nomlote", defaultValue = "") String nomlote,
			@RequestParam(value = "ubicacion", defaultValue = "") String ubicacion,
			@RequestParam(value = "familia", defaultValue = "") String familia
			) throws Exception {
		List<Mausoleo> mausoleos = new ArrayList<>();
		String condicion = (codcem!=0 ? " and ma.codcem=" + codcem : "");
		condicion += (codmau != 0 ? " and ma.codmau=" + codmau : "");
		condicion += (!nomlote.equals("") ? " and ma.nomlote like '%" + nomlote + "%'" : "");
		condicion += (!ubicacion.equals("") ? " and ma.ubicacion like '%" + ubicacion + "%'" : "");
		condicion += (!familia.equals("") ? " and ma.familia like '%" + familia + "%'" : "");

		mausoleos = daoMausoleo.listaMausoleo(condicion, "", "");
		return new ResponseEntity<List<Mausoleo>>(mausoleos, HttpStatus.OK);
	}

	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codmau", defaultValue = "0") Integer codmau,
			@RequestParam(value = "nomlote", defaultValue = "") String nomlote,
			@RequestParam(value = "ubicacion", defaultValue = "") String ubicacion,
			@RequestParam(value = "familia", defaultValue = "") String familia,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		List<Mausoleo> content = new ArrayList<Mausoleo>();
		String condicion = (codcem != 0 ? " and codcem=" + codcem : "");
		condicion += (codmau != 0 ? " and codmau=" + codmau : "");
		condicion += (!nomlote.equals("") ? " and nomlote like '%" + nomlote + "%'" : "");
		condicion += (!ubicacion.equals("") ? " and ubicacion like '%" + ubicacion + "%'" : "");
		condicion += (!familia.equals("") ? " and familia like '%" + familia + "%'" : "");

		int inicio = page * size;
		Integer totalReg = daoMausoleo.getMausoleoCount(condicion);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = daoMausoleo.listaMausoleo(condicion, " limit  " + inicio + ", " + size, "");

		Map<String, Object> response = new HashMap<>();
		response.put("content", content);
		response.put("number", page);
		response.put("size", size);
		response.put("totalElements", totalReg);
		response.put("totalPages", totalPages);
		response.put("first", first);
		response.put("last", last);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/idMausoleo")
	public ResponseEntity<Mausoleo> buscaPorId(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codmau", defaultValue = "0") Integer codmau) {
		Mausoleo obj = daoMausoleo.buscaMausoleo(codcem, codmau);
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Mausoleo> registrar(@Valid @RequestBody Mausoleo mausoleo) throws Exception {
//		Cliente obj = daoCliente.insertaCliente(cliente);
		// localhost:8080/clientes/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdmed()).toUri();
//		return ResponseEntity.created(location).build();
		validate(mausoleo);
		return new ResponseEntity<>(daoMausoleo.insertaMausoleo(mausoleo), HttpStatus.CREATED); // 201
	}

	@PutMapping
	public ResponseEntity<Mausoleo> modificar(@Valid @RequestBody Mausoleo mausoleo) throws Exception {
		return new ResponseEntity<>(daoMausoleo.modificaMausoleo(mausoleo), HttpStatus.OK);
	}

	@DeleteMapping("/elimina")
	public ResponseEntity<Void> eliminar(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codmau", defaultValue = "0") Integer codmau) throws Exception {
		daoMausoleo.eliminaMausoleo(codcem, codmau);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/reporteMausoleos", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE) // APPLICATION_PDF_VALUE
	// //APPLICATION_OCTET_STREAM_VALUE
	public ResponseEntity<byte[]> generarReporte(@RequestParam(value = "reporte", defaultValue = "1") Integer reporte,
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "orden", defaultValue = "1") Integer orden,
			@RequestParam(value = "formato", defaultValue = "1") Integer formato,
            @RequestParam(value="bdifuntos", required=false) boolean bdifuntos) throws Exception {
		logger.info("generarReporte...");
		byte[] data = null;
		String filtro = "";
		String _orden = "";
		if (codcem != 0)
			filtro = " and ma.codcem=" + codcem;
		
    	if (orden==1) _orden = "codcem, codmau";
        else if (orden==2) _orden = "codcem, familia";
        else if (orden==3) _orden = "codcem, nomlote";

		Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("datasource", datasource);
		parametros.put("condicion", filtro);
		parametros.put("orden", _orden);

		if (formato==1) {
			if (reporte==1) {
		        PdfMausoleo _reporte = new PdfMausoleo(parametros);
		        data = _reporte.creaReporte();
			} else if (reporte==2) {
		        PdfMausoleoDifuntos _reporte = new PdfMausoleoDifuntos(parametros);
		        data = _reporte.creaReporte();
			}
		} else if (formato==2) {
			if (reporte==1) {
		        XlsMausoleo _reporte = new XlsMausoleo(parametros);
		        data = _reporte.creaReporte();
			} else if (reporte==2) {
		        XlsMausoleoDifuntos _reporte = new XlsMausoleoDifuntos(parametros);
		        data = _reporte.creaReporte();
			}
		}
		return new ResponseEntity<byte[]>(data, HttpStatus.OK);
	}
	
    public void validate(Object obj ) throws Exception{
        Mausoleo mausoleo = (Mausoleo)obj;
        StringBuilder msg = new StringBuilder();
        if (mausoleo.getCementerio()==null) {
            msg.append(" No se ha definido cementerio.");
        } else {
        	if (mausoleo.getCementerio().getCodcem()==-1) {
        		msg.append(" No se ha especificado el cementerio.");
        	}
        }
        if (mausoleo.getLotizado()==null || mausoleo.getLotizado().length()==0) {
        	msg.append(" Debe especificar S o N.");
        }
        if (mausoleo.getNomlote()==null || mausoleo.getNomlote().length()==0) {
            msg.append(" Debe Ingresar Nombre del lote.");
        }
        if (mausoleo.getTipomau()==null) {
            msg.append(" No se ha definido tipo de mausoleo.");
        } else {
        	if (mausoleo.getTipomau().getCodigo().equals("-1")) {
        		msg.append(" Debe Ingresar un tipo de mausoleo.") ;
        	}
        }
        if (mausoleo.getUbicacion()==null || mausoleo.getUbicacion().length()==0) {
            msg.append(" Debe Ingresar una ubicacion.") ;
        }
        if (mausoleo.getFamilia()==null || mausoleo.getFamilia().length()==0) {
            msg.append(" Debe Ingresar Nombre de la familia o congregación.");
        }
        if (mausoleo.getCliente()==null) {
            msg.append(" No se ha definido cliente.");
        } else {
        	if (mausoleo.getCliente().getTipdoccli().getCodigo().equals("-1")) {
        		msg.append(" Debe Ingresar un tipo de documento del cliente.") ;
        	}
        	if (mausoleo.getCliente().getNomcli()==null || mausoleo.getCliente().getNomcli().length()==0) {
        		msg.append(" Debe Ingresar el nombre del cliente.") ;
        	}
        	if (mausoleo.getCliente().getDircli()==null || mausoleo.getCliente().getDircli().length()==0) {
        		msg.append(" Debe Ingresar la direccion del cliente.") ;
        	}
        }
        if (mausoleo.getTotdif()==0) {
            msg.append(" Ingresar total maximo de difuntos a enterrar.");
        }
        if (!msg.isEmpty()) {
        	throw new Exception(msg.toString());
        }
    }

}