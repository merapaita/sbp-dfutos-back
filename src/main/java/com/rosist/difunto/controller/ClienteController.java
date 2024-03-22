package com.rosist.difunto.controller;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rosist.difunto.dao.ClienteDao;
import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.modelSbp.Cliente;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

	@Autowired
	private ClienteDao daoCliente;

	private Logger logger = LoggerFactory.getLogger(ClienteController.class);

	@GetMapping
	public ResponseEntity<List<Cliente>> listar(@RequestParam(value = "tipdoccli", defaultValue = "") String tipdoccli,
			@RequestParam(value = "doccli", defaultValue = "") String doccli,
			@RequestParam(value = "nomcli", defaultValue = "") String nomcli) throws Exception {
		List<Cliente> clientes = new ArrayList<>();
		String condicion = (!tipdoccli.equals("") ? " and tipdoc='" + tipdoccli + "'" : "");
		condicion += (!doccli.equals("") ? " and doccli='" + doccli + "'" : "");
		condicion += (!nomcli.equals("") ? " and nomcli like '%" + nomcli + "%'" : "");
		
		clientes = daoCliente.listaClientes(condicion, "", "");
		return new ResponseEntity<List<Cliente>>(clientes, HttpStatus.OK);
	}

	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
			@RequestParam(value = "tipdoccli", defaultValue = "") String tipdoccli,
			@RequestParam(value = "doccli", defaultValue = "") String doccli,
			@RequestParam(value = "nomcli", defaultValue = "") String nomcli,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		List<Cliente> content = new ArrayList<Cliente>();
		String condicion = (!tipdoccli.equals("") ? " and tipdoc='" + tipdoccli + "'" : "");
		condicion += (!doccli.equals("") ? " and doccli='" + doccli + "'" : "");
		condicion += (!nomcli.equals("") ? " and nomcli like '%" + nomcli + "%'" : "");

		int inicio = page * size;
		Integer totalReg = daoCliente.getClienteCount(condicion);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = daoCliente.listaClientes(condicion, " limit  " + inicio + ", " + size, "");

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

	@GetMapping("/idCliente")
	public ResponseEntity<Cliente> getAllParametros(
			@RequestParam(value = "tipdoccli", defaultValue = "") String tipdoccli,
			@RequestParam(value = "doccli", defaultValue = "") String doccli
	      ) {
		
		Cliente obj = daoCliente.buscaCliente(tipdoccli, doccli);
		return new ResponseEntity<>(obj, HttpStatus.OK);
		
	}

	@GetMapping("/busca")
	public ResponseEntity<Cliente> listarPorId(
			@RequestParam(value = "tipdoccli.codigo", defaultValue = "0") String tipdoccli,
			@RequestParam(value = "doccli", defaultValue = "0") String doccli) throws Exception {
		Cliente obj = daoCliente.buscaCliente(tipdoccli, doccli);

		if (obj == null) {
			throw new ModelNotFoundException("CLIENTE NO ENCONTRADO ");
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Cliente> registrar(@Valid @RequestBody Cliente cliente) throws Exception {
//		Cliente obj = daoCliente.insertaCliente(cliente);
		// localhost:8080/clientes/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdmed()).toUri();
//		return ResponseEntity.created(location).build();
		return new ResponseEntity<>(daoCliente.insertaCliente(cliente), HttpStatus.CREATED); // 201
	}

	@PutMapping
	public ResponseEntity<Cliente> modificar(@Valid @RequestBody Cliente cliente) throws Exception {
		return new ResponseEntity<>(daoCliente.modificaCliente(cliente), HttpStatus.OK);
	}

	@DeleteMapping("/elimina")
	public ResponseEntity<Void> eliminar(
			@RequestParam(value = "tipdoccli", defaultValue = "") String tipdoccli,
			@RequestParam(value = "doccli", defaultValue = "") String doccli) throws Exception {
		daoCliente.eliminaCliente(tipdoccli, doccli);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
