package com.rosist.difunto.venta.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.rosist.difunto.venta.model.Parmae;
import com.rosist.difunto.venta.model.ParmaePK;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.IParmaeRepo;
import com.rosist.difunto.venta.service.IParmaeService;

@RestController
@RequestMapping("/parmae1")
public class Parmae1Controller {

	@Autowired
	private IParmaeService service;
	
	@Autowired
	private IParmaeRepo repo;
	
	@GetMapping
	public ResponseEntity<List<Parmae>> listar(
	        @RequestParam(value="tipo",     defaultValue = "") String tipo,
	        @RequestParam(value="codigo",   defaultValue = "") String codigo,
	        @RequestParam(value="codigoaux", defaultValue = "") String codigoaux
			) throws Exception{
		
		return new ResponseEntity<>(service.listarParmae(tipo, codigo, codigoaux, -1, 0), HttpStatus.OK);
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
			@RequestParam(value = "tipo", defaultValue = "") String tipo,
			@RequestParam(value = "codigo", defaultValue = "") String codigo,
			@RequestParam(value = "codigoaux", defaultValue = "") String codigoaux,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) throws Exception {

		List<Parmae> content = new ArrayList<Parmae>();
		String condicion = "";

		int inicio = page * size;
		Integer totalReg = repo.countParmae(tipo, codigo, codigoaux);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = service.listarParmae(tipo, codigo, codigoaux, page, size);

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
	
//	@GetMapping("/{id}")
//	public Parmae listarPorId(@PathVariable("id") ParmaePK id) throws Exception{
//		return service.listarPorId(id);		
//	}
	
	@PostMapping
	public Parmae registrar(@RequestBody Parmae parmae) throws Exception {
		return service.registrar(parmae);		
	}
	
	@PutMapping
	public Parmae modificar(@RequestBody Parmae parmae) throws Exception {
		return service.modificar(parmae);		
	}
	
	@DeleteMapping("/{id}")
	public void eliminar(@PathVariable("id") ParmaePK id) throws Exception{
		service.eliminar(id);		
	}
	
}
