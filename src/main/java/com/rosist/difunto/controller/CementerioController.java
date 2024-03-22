package com.rosist.difunto.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.rosist.difunto.dao.CementerioDao;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cliente;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cementerio")
public class CementerioController {

	@Autowired
	private CementerioDao daoCementerio;

	private Logger logger = LoggerFactory.getLogger(CementerioController.class);

	@GetMapping
	public ResponseEntity<List<Cementerio>> listar(@RequestParam(value = "codcem", defaultValue = "") String codcem,
			@RequestParam(value = "nomcem", defaultValue = "") String nomcem) throws Exception {
		List<Cementerio> cementerios = new ArrayList<>();
		String condicion = (!codcem.equals("") ? " and codcem=" + codcem : "");
		condicion += (!nomcem.equals("") ? " and nomcem like '%" + nomcem + "%'" : "");

		cementerios = daoCementerio.listaCementerio(condicion, "", "");
		return new ResponseEntity<List<Cementerio>>(cementerios, HttpStatus.OK);
	}

	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
			@RequestParam(value = "codcem", defaultValue = "") String codcem,
			@RequestParam(value = "nomcem", defaultValue = "") String nomcem,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		List<Cementerio> content = new ArrayList<Cementerio>();
		String condicion = (!codcem.equals("") ? " and codcem=" + codcem : "");
		condicion += (!nomcem.equals("") ? " and nomcem like '%" + nomcem + "%'" : "");

		int inicio = page * size;
		Integer totalReg = daoCementerio.getCementerioCount(condicion);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = daoCementerio.listaCementerio(condicion, " limit  " + inicio + ", " + size, "");

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
	public ResponseEntity<Cementerio> getAllParametros(@PathVariable("id") Integer id) {
		Cementerio obj = daoCementerio.buscaCementerio(id);
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Cementerio> registrar(@Valid @RequestBody Cementerio cementerio) throws Exception {
//		Cementerio obj = daoCementerio.insertaCementerio(cementerio);
		// localhost:8080/medicos/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getCodcem())
//				.toUri();
//		return ResponseEntity.created(location).build();
		validate(cementerio);
		return new ResponseEntity<>(daoCementerio.insertaCementerio(cementerio), HttpStatus.CREATED);		// 201
	}

	@PutMapping
	public ResponseEntity<Cementerio> modificar(@Valid @RequestBody Cementerio cementerio) throws Exception {
		return new ResponseEntity<>(daoCementerio.modificaCementerio(cementerio), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) throws Exception {
		daoCementerio.eliminaCementerio(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

    public void validate(Object obj) throws Exception  {
        Cementerio xcementerio = (Cementerio) obj;
        String msg = "";
        if (xcementerio.getNomcem() == null || xcementerio.getNomcem().length() == 0) {
            msg ="Debe Ingresar Nombre";
        }
        if (!msg.isEmpty()) {
        	throw new Exception(msg);
        }
        
    }


}
