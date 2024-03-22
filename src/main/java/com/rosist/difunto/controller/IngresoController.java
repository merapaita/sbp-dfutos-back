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

import com.rosist.difunto.dao.IngresoDao;
import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.modelSbp.Cliente;
import com.rosist.difunto.modelSbp.Ingreso;

@RestController
@RequestMapping("/ingreso")
public class IngresoController {

	@Autowired
	private IngresoDao daoIngreso;
	
	private Logger logger = LoggerFactory.getLogger(IngresoController.class);
	
	@GetMapping
	public ResponseEntity<List<Ingreso>> listar() throws Exception{
		List<Ingreso> ingresos = new ArrayList<>();
		ingresos = daoIngreso.listaIngresos("", "", "", "", "", "", "", "", "", "", -1, 0);
		return new ResponseEntity<List<Ingreso>>(ingresos, HttpStatus.OK);
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllIngresos(
			@RequestParam(value = "iding", defaultValue = "") String iding,
			@RequestParam(value = "tiping", defaultValue = "") String tiping,
			@RequestParam(value = "tipcli", defaultValue = "") String tipcli,
			@RequestParam(value = "tipcom", defaultValue = "") String tipcom,
			@RequestParam(value = "conser", defaultValue = "") String conser,
			@RequestParam(value = "sersbp", defaultValue = "") String sersbp,
			@RequestParam(value = "codsbp", defaultValue = "") String codsbp,
			@RequestParam(value = "codmed", defaultValue = "") String codmed,
			@RequestParam(value = "fecha", defaultValue = "") String fecha,
			@RequestParam(value = "estcomsbp", defaultValue = "") String estcomsbp,
			@RequestParam(value = "estsun", defaultValue = "") String estsun,
			@RequestParam(value = "order", defaultValue = "") String order,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		List<Ingreso> content = new ArrayList<Ingreso>();

//		int inicio = page * size;
		Integer totalReg = daoIngreso.getIngresoCount(iding, tiping, tipcli, tipcom, conser, sersbp, codsbp, fecha, estcomsbp);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = daoIngreso.listaIngresos(iding, tiping, tipcli, tipcom, conser, sersbp, codsbp, fecha, estcomsbp, order, page, size);

		Map<String, Object> response = new HashMap<>();
		response.put("content", content);
		response.put("number", page);
		response.put("totalElements", totalReg);
		response.put("totalPages", totalPages);
		response.put("size", size);
		response.put("first", first);
		response.put("last", last);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/{iding}")
	public ResponseEntity<Ingreso> listarPorId(@PathVariable("iding") String iding) throws Exception{
		Ingreso obj = daoIngreso.buscaIngreso(iding);
		if(obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + iding);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Ingreso> registrar(@Valid @RequestBody Ingreso ingreso) throws Exception {
		Ingreso obj = daoIngreso.insertaIngreso(ingreso);
		//localhost:8080/ingresos/1
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{codingreso}").buildAndExpand(obj.getIding()).toUri();
		return ResponseEntity.created(location).build();
//		return new ResponseEntity<>(service.registrar(paciente), HttpStatus.CREATED);		// 201
	}

	@PutMapping
	public ResponseEntity<Ingreso> modificar(@Valid @RequestBody Ingreso ingreso) throws Exception {
		return new ResponseEntity<>(daoIngreso.modificaIngreso(ingreso), HttpStatus.OK);
	}

	@DeleteMapping("/{iding}")
	public ResponseEntity<Void> eliminar(@PathVariable("iding") String iding) throws Exception{
		daoIngreso.eliminaIngreso(iding);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}
