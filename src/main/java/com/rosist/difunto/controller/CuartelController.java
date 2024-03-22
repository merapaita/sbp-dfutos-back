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

import com.rosist.difunto.dao.ClienteDao;
import com.rosist.difunto.dao.CuartelDao;
import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cliente;
import com.rosist.difunto.modelSbp.Cuartel;
import com.rosist.difunto.reports.PdfCuartel;
import com.rosist.difunto.reports.PdfCuartelNichos;
import com.rosist.difunto.reports.PdfCuartelNichosEstados;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cuartel")
public class CuartelController {

	@Autowired
	private CuartelDao daoCuartel;

    @Autowired
    private DriverManagerDataSource datasource;

	private Logger logger = LoggerFactory.getLogger(CuartelController.class);

	@GetMapping
	public ResponseEntity<List<Cuartel>> listar(@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar,
			@RequestParam(value = "nomcuar", defaultValue = "") String nomcuar,
			@RequestParam(value = "disponibles", defaultValue = "false") Boolean disponibles) throws Exception {
		List<Cuartel> cuarteles = new ArrayList<>();
		String condicion = (codcem != 0 ? " and codcem=" + codcem : "");
		condicion += (codcuar != 0 ? " and codcuar=" + codcuar : "");
		condicion += (!nomcuar.equals("") ? " and nomcuar like '%" + nomcuar + "%'" : "");

		if (disponibles == true) {
			if (codcem == 0) {
				throw new Exception("CEMENTERIO NO ENCONTRADO");
			}
			cuarteles = daoCuartel.listaCuartelDisp(codcuar);
		}
		cuarteles = daoCuartel.listaCuartel(condicion, "", "");
		return new ResponseEntity<List<Cuartel>>(cuarteles, HttpStatus.OK);
	}

	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar,
			@RequestParam(value = "nomcuar", defaultValue = "") String nomcuar,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		List<Cuartel> content = new ArrayList<Cuartel>();
		String condicion = (codcem != 0 ? " and codcem=" + codcem : "");
		condicion += (codcuar != 0 ? " and codcuar=" + codcuar : "");
		condicion += (!nomcuar.equals("") ? " and nomcuar like '%" + nomcuar + "%'" : "");

		int inicio = page * size;
		Integer totalReg = daoCuartel.getCuartelCount(condicion);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = daoCuartel.listaCuartel(condicion, " limit  " + inicio + ", " + size, "");

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

	@GetMapping("/idCuartel")
	public ResponseEntity<Cuartel> buscaPorId(@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar) {
		Cuartel obj = daoCuartel.buscaCuartel(codcem, codcuar);
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Cuartel> registrar(@Valid @RequestBody Cuartel cuartel) throws Exception {
//		Cliente obj = daoCliente.insertaCliente(cliente);
		// localhost:8080/clientes/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdmed()).toUri();
//		return ResponseEntity.created(location).build();
		validate(cuartel);
		return new ResponseEntity<>(daoCuartel.insertaCuartel(cuartel), HttpStatus.CREATED); // 201
	}

	@PutMapping
	public ResponseEntity<Cuartel> modificar(@Valid @RequestBody Cuartel cuartel) throws Exception {
		return new ResponseEntity<>(daoCuartel.modificaCuartel(cuartel), HttpStatus.OK);
	}

	@DeleteMapping("/elimina")
	public ResponseEntity<Void> eliminar(@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar) throws Exception {
		daoCuartel.eliminaCuartel(codcem, codcuar);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/reporteCuarteles", produces = MediaType.APPLICATION_PDF_VALUE) // APPLICATION_PDF_VALUE
	// //APPLICATION_OCTET_STREAM_VALUE
	public ResponseEntity<byte[]> generarReporte(@RequestParam(value = "reporte", defaultValue = "1") Integer reporte,
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar,
			@RequestParam(value = "estado", defaultValue = "0") Integer estado,
			@RequestParam(value = "bnichos", required = false) boolean bNichos) throws Exception {
		logger.info("generarReporte...");
		byte[] data = null;
		String filtro = "";
		if (codcem != 0)
			filtro = " and cu.codcem=" + codcem;
		if (codcuar != 0)
			filtro += " and cu.codcuar=" + codcuar;
		if (estado != 0)
			filtro += " and t.estado=" + estado;

		Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("datasource", datasource);
		parametros.put("condicion", filtro);

		if (reporte==1) {
	        PdfCuartel _reporte = new PdfCuartel(parametros);
	        data = _reporte.creaReporte();
		} else if (reporte==2) {
	        PdfCuartelNichos _reporte = new PdfCuartelNichos(parametros);
	        data = _reporte.creaReporte();
		} else if(reporte==3) {
	        PdfCuartelNichosEstados _reporte = new PdfCuartelNichosEstados(parametros);
	        data = _reporte.creaReporte();
		}
		return new ResponseEntity<byte[]>(data, HttpStatus.OK);
	}

	public void validate(Object obj) throws Exception {
		Cuartel cuartel = (Cuartel) obj;
		String msg = "";
		if (cuartel.getNomcuar() == null || cuartel.getNomcuar().length() == 0) {
			msg += " Debe Ingresar Nombre.";
		}
		if (cuartel.getFilas() == 0) {
			msg += " Debe Ingresar Numero de Filas.";
		}
		if (cuartel.getColumnas() == 0) {
			msg += " Debe Ingresar Numero de columnas.";
		}
		if (cuartel.getCementerio()==null) {
			msg += " No ha especificado cementerio.";
		} else {
			if (cuartel.getCementerio().getCodcem() == -1) {
				msg += " No ha seleccionado ningun cementerio.";
			}
		}
		if (cuartel.getTipcuar()==null) {
			msg += " No ha especificado tipo de Cuartel.";
		} else {
			if (cuartel.getTipcuar().getCodigo().equals("-1")) {
				msg += " Debe Ingresar el Tipo de Cuartel.";
			}
		}
        if (!msg.isEmpty()) {
        	throw new Exception(msg);
        }
	}
	
	// atheoas

}