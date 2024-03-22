package com.rosist.difunto.venta.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.venta.dto.PagactaDto;
import com.rosist.difunto.venta.model.Pagacta;
import com.rosist.difunto.venta.model.Pagdon;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.IPagdonRepo;
import com.rosist.difunto.venta.service.IPagdonService;

@RestController
@RequestMapping("/pagdon")
public class PagdonController {

	@Autowired
	private IPagdonRepo repo;

	@Autowired
	private IPagdonService service;

	private static final Logger log = LoggerFactory.getLogger(PagdonController.class);

	@GetMapping
	public ResponseEntity<List<Pagdon>> listar(@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes) throws Exception {
		return new ResponseEntity<List<Pagdon>>(service.listarPagdon(anno, mes, -1, 0), HttpStatus.OK);
	}

	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "0") Integer size) throws Exception {

		List<Pagdon> content = new ArrayList<Pagdon>();
		String condicion = "";

		int inicio = page * size;
		Integer totalReg = repo.countPagdon(anno, mes);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = service.listarPagdon(anno, mes, page, size);

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
	public ResponseEntity<Pagdon> listarPorId(@PathVariable("id") String id) throws Exception {
		Pagdon obj = service.listarPorId(id);
		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Pagdon> registrar(@Valid @RequestBody Pagdon pagdon) throws Exception {
//		Cementerio obj = daoCementerio.insertaCementerio(cementerio);
		// localhost:8080/medicos/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getCodcem())
//				.toUri();
//		return ResponseEntity.created(location).build();
		validate(pagdon);
		return new ResponseEntity<>(service.registraTransaccion(pagdon), HttpStatus.CREATED); // 201
	}

	@PutMapping
	public ResponseEntity<Pagdon> modificar(@Valid @RequestBody Pagdon pagdon) throws Exception {
		String cError = "";
		validate(pagdon);
		return new ResponseEntity<>(service.modificaTransaccion(pagdon), HttpStatus.OK);
	}
	
	private void validate(Object obj) throws Exception {

		Pagdon pagdon = (Pagdon) obj;
		StringBuilder msg = new StringBuilder();

		if (pagdon.getFecpd() == null) {
			msg.append(" Fecha de pago de donacion no definida.");
		}
		if (pagdon.getCodent() == null || pagdon.getCodent().length() == 0) {
			msg.append("Entidad no definida.");
		}
		if (pagdon.getCodcp() == null || pagdon.getCodcp().length() == 0) {
			msg.append("Comprobante de pago no definido.");
		}
		if (pagdon.getCheque() == null || pagdon.getCheque().length() == 0) {
			msg.append("Cheque no definido.");
		}
		if (pagdon.getMtotot() == null) { // || pagacta.getFeccp().length()==0
			msg.append("Monto de pago no definido.");
		}

		if (pagdon.getDonaciones() == null) {
			msg.append(" No se ha definido donaciones a pagar.");
		} else {
//			pagdon.getDonaciones().forEach(reg -> {
//				if (reg.getCodcre() == null) {
//					msg.append(" No se ha definido codigo del credito.");
//				}
//			});
		}
		if (!msg.isEmpty()) {
			throw new Exception(msg.toString());
		}
	}
	
	

}