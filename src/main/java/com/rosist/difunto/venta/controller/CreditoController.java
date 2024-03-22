package com.rosist.difunto.venta.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.venta.model.Credito;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.ICreditoRepo;
import com.rosist.difunto.venta.service.ICreditoService;

@RestController
@RequestMapping("/credito")
public class CreditoController {

	@Autowired
	private ICreditoRepo repo;
	
	@Autowired
	private ICreditoService service;
	
	@GetMapping
	public ResponseEntity<List<Credito>> listar(
			@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes,
			@RequestParam(value = "cliente", defaultValue = "") String cliente,
			@RequestParam(value = "estado", defaultValue = "") String estado
			) throws Exception {
//		return new ResponseEntity<List<Credito>>(service.listar(), HttpStatus.OK);
		return new ResponseEntity<List<Credito>>(service.listarCreditos(anno, mes, cliente, estado, -1, 0), HttpStatus.OK);
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
			@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes,
			@RequestParam(value = "cliente", defaultValue = "") String cliente,
			@RequestParam(value = "estado", defaultValue = "") String estado,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) throws Exception {

		List<Credito> content = new ArrayList<Credito>();
		String condicion = "";

		int inicio = page * size;
		Integer totalReg = repo.countCredito(anno, mes, cliente, estado);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);
		
		content = null;
		content = service.listarCreditos(anno, mes, cliente, estado, page, size);
		
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
	public ResponseEntity<Credito> listarPorId(@PathVariable("id") String id) throws Exception {
		Credito obj = service.listarPorId(id);
		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@GetMapping(value = "/controlPdf", produces = MediaType.APPLICATION_PDF_VALUE) // APPLICATION_PDF_VALUE
																				   // APPLICATION_OCTET_STREAM_VALUE
	public ResponseEntity<byte[]> getCreditoControl(
			@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes,
			@RequestParam(value = "cliente", defaultValue = "") String cliente,
			@RequestParam(value = "estado", defaultValue = "") String estado
			) throws Exception {
		
		byte[] data = null;
		data = service.reporteCreditosControl(anno, mes, cliente, estado);
		
		return new ResponseEntity<byte[]>(data, HttpStatus.OK);
	}

}
