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

import com.rosist.difunto.dao.PartidaDao;
import com.rosist.difunto.exception.ModelNotFoundException;
//import com.rosist.difunto.model.Empleado;
import com.rosist.difunto.modelSbp.Partida;

@RestController
@RequestMapping("/partida")
public class PartidaController {

	@Autowired
	private PartidaDao daoPartida;
	
	private Logger logger = LoggerFactory.getLogger(PartidaController.class);
	
	@GetMapping
	public ResponseEntity<List<Partida>> listar(
	        @RequestParam(value="codpart", defaultValue = "") String codpart,
	        @RequestParam(value="descri",  defaultValue = "") String descri
			) throws Exception{
		List<Partida> partidas = new ArrayList<>();
		partidas = daoPartida.listaPartidas(codpart, descri, -1, 0);
		return new ResponseEntity<List<Partida>>(partidas, HttpStatus.OK);
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
	        @RequestParam(value="codpart", defaultValue = "") String codpart,
	        @RequestParam(value="descri",  defaultValue = "")  String descri,
			@RequestParam(value = "page", defaultValue = "0")  Integer page,
			@RequestParam(value = "size", defaultValue = "10") Integer size) {
		List<Partida> content = new ArrayList<Partida>();

		int inicio = page * size;
		Integer totalReg = daoPartida.getPartidaCount(codpart, descri);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = daoPartida.listaPartidas(codpart, descri, page, size);
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

	@GetMapping("/idPartida/{codpart}")
	public ResponseEntity<Partida> listarPorId(@PathVariable("codpart") String codpart) throws Exception{
		Partida obj = daoPartida.buscaPartida(codpart);
		
		if(obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + codpart);
		}
		
		return new ResponseEntity<>(obj, HttpStatus.OK);		
	}
	
	@PostMapping
	public ResponseEntity<Partida> registrar(@Valid @RequestBody Partida partida) throws Exception {
		Partida obj = daoPartida.insertaPartida(partida);
		//localhost:8080/partidas/1
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getCodpart()).toUri();
		return ResponseEntity.created(location).build();
//		return new ResponseEntity<>(service.registrar(paciente), HttpStatus.CREATED);		// 201
	}

	@PutMapping
	public ResponseEntity<Partida> modificar(@Valid @RequestBody Partida partida) throws Exception {
		return new ResponseEntity<>(daoPartida.modificaPartida(partida), HttpStatus.OK);
	}

	@DeleteMapping("/{codpart}")
	public ResponseEntity<Void> eliminar(@PathVariable("codpart") String codpart) throws Exception{
		daoPartida.eliminaPartida(codpart);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
}
