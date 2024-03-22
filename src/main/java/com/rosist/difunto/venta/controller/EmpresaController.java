package com.rosist.difunto.venta.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.venta.model.Empresa;
import com.rosist.difunto.venta.service.IEmpresaService;

@RestController
@RequestMapping("/empresa")
public class EmpresaController {

	@Autowired
	private IEmpresaService service;
	
	@GetMapping
	public ResponseEntity<List<Empresa>> listar(
			) throws Exception {
		return new ResponseEntity<List<Empresa>>(service.listar(), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Empresa> listarPorId(@PathVariable("id") Integer id) throws Exception {
		Empresa obj = service.listarPorId(id);
		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

}
