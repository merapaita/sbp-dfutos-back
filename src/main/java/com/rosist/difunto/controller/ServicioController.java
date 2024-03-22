package com.rosist.difunto.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rosist.difunto.dao.ServicioDao;
import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.modelSbp.Servicio;

@RestController
@RequestMapping("/servicio")
public class ServicioController {

	@Autowired
	private ServicioDao daoServicio;
	
	private Logger logger = LoggerFactory.getLogger(ServicioController.class);
	
	@GetMapping
	public ResponseEntity<List<Servicio>> listar(
	        @RequestParam(value="idser",  defaultValue = "") String idser,
	        @RequestParam(value="tipser", defaultValue = "") String tipser,
	        @RequestParam(value="desser", defaultValue = "") String desser
			) throws Exception{
		List<Servicio> servicios = new ArrayList<>();
		servicios = daoServicio.listaServicios(idser, tipser, desser, -1, 0);
		return new ResponseEntity<List<Servicio>>(servicios, HttpStatus.OK);
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
	        @RequestParam(value="idser",     defaultValue = "") String idser,
	        @RequestParam(value="tipser",     defaultValue = "") String tipser,
	        @RequestParam(value="desser",     defaultValue = "") String desser,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "10") Integer size) {

		List<Servicio> content = new ArrayList<Servicio>();

		int inicio = page * size;
		Integer totalReg = daoServicio.getServicioCount(idser, tipser, desser);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = daoServicio.listaServicios(idser, tipser, desser, page, size);

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
	
	@GetMapping("/{id}")
	public ResponseEntity<Servicio> listarPorId(@PathVariable("id") String id) throws Exception{
		Servicio obj = daoServicio.buscaServicio(id);
		if(obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);		
	}
	
	@PostMapping
	public ResponseEntity<Servicio> registrar(@Valid @RequestBody Servicio servicio) throws Exception {
		Servicio obj = daoServicio.insertaServicio(servicio);
		//localhost:8080/servicios/1
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdser()).toUri();
		return ResponseEntity.created(location).build();
//		return new ResponseEntity<>(service.registrar(paciente), HttpStatus.CREATED);		// 201
	}

	@PutMapping
	public ResponseEntity<Servicio> modificar(@RequestBody Servicio servicio) throws Exception {
		return new ResponseEntity<>(daoServicio.modificaServicio(servicio), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") String id) throws Exception{
		daoServicio.eliminaServicio(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "/reportePdf", produces = MediaType.APPLICATION_PDF_VALUE) //APPLICATION_PDF_VALUE		//APPLICATION_OCTET_STREAM_VALUE
	public ResponseEntity<byte[]> reporteItepec() throws Exception {
		byte[] data = null;
		data = daoServicio.reporteServicio();
		return new ResponseEntity<byte[]>(data, HttpStatus.OK);
	}
	
}
