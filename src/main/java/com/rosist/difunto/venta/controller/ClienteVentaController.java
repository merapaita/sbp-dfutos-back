package com.rosist.difunto.venta.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rosist.difunto.dao.ParmaeDao;
import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.modelSbp.Parmae;
import com.rosist.difunto.venta.model.Cliente;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.IClienteRepo;
import com.rosist.difunto.venta.service.IClienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clienteVenta")
public class ClienteVentaController {
	
	@Autowired
	private IClienteService service;
	
	@Autowired
	private ParmaeDao daoParmae;
	
	@Autowired
	private IClienteRepo repo;
	
	private static final Logger log = LoggerFactory.getLogger(ClienteVentaController.class);

	@GetMapping
	public ResponseEntity<List<Cliente>> listar(
			@RequestParam(value = "codcli", defaultValue = "0") Integer codcli,
			@RequestParam(value = "tipdoccli", defaultValue = "") String tipdoccli,
			@RequestParam(value = "doccli", defaultValue = "") String doccli,
			@RequestParam(value = "nomcli", defaultValue = "") String nomcli
			) throws Exception {
		return new ResponseEntity<List<Cliente>>(service.listarCliente(codcli, tipdoccli, doccli, nomcli, -1, 0), HttpStatus.OK);
	}

	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
			@RequestParam(value = "codcli", defaultValue = "0") Integer codcli,
			@RequestParam(value = "tipdoccli", defaultValue = "") String tipdoccli,
			@RequestParam(value = "doccli", defaultValue = "") String doccli,
			@RequestParam(value = "nomcli", defaultValue = "") String nomcli,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
			) throws Exception {

		List<Cliente> content = new ArrayList<Cliente>();
		String condicion = "";

		int inicio = page * size;
		Integer totalReg = repo.countCliente(codcli, tipdoccli, doccli, nomcli);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = service.listarCliente(codcli, tipdoccli, doccli, nomcli, page, size);

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
	public ResponseEntity<Cliente> listarPorId(@PathVariable("id") Integer id) throws Exception {
		Cliente obj = service.listarPorId(id);
		List<Parmae> tipdoccli_s = daoParmae.listaParmae(" and tipo='TDCLI'", "", "");
		
//		if (obj == null) {
//			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
//		}
		
		obj.setDesTipdoccli(tipdoccli_s.stream()
				.filter(yy -> yy.getCodigo().equals(obj.getTipdoccli()))
				.collect(Collectors.toList()).get(0).getDescri()
				);
		
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Cliente> registrar(@Valid @RequestBody Cliente cliente) throws Exception {
//		Cliente obj = daoCementerio.insertaCementerio(cementerio);
		// localhost:8080/medicos/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getCodcem())
//				.toUri();
//		return ResponseEntity.created(location).build();
//		validate(venta);
		return new ResponseEntity<>(service.registraTransaccion(cliente), HttpStatus.CREATED);		// 201
	}

	@PutMapping
	public ResponseEntity<Cliente> modificar(@Valid @RequestBody Cliente cliente) throws Exception {
		String cError = "";
		validate(cliente);
		return new ResponseEntity<>(service.modificaTransaccion(cliente), HttpStatus.OK);
	}
	
	@GetMapping("/hateoas/{id}")
	public EntityModel<Cliente> listarHateoasPorId(@PathVariable("id") Integer id) throws Exception {
		Cliente obj = service.listarPorId(id);

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}

		EntityModel<Cliente> recurso = EntityModel.of(obj);
		// localhost:8080/medicos/hateoas/1
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).listarHateoasPorId(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).listarHateoasPorId(id));
		recurso.add(link1.withRel("Paciente-recurso1"));
		recurso.add(link2.withRel("Paciente-recurso2"));

		return recurso;
	}

	public void validate(Object obj) throws Exception {
		Cliente cliente = (Cliente)obj;
		StringBuilder msg = new StringBuilder();
		
//        if (cliente.getCodcli()==null || cliente.getCodcli().length()==0) {
//            msg.append(" Codigo de Venta no definida.");
//        } else {
//        	
//        }
        
		if (!msg.isEmpty()) {
			throw new Exception(msg.toString());
		}

	}

}
