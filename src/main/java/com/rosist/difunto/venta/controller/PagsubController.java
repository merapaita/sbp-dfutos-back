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
import com.rosist.difunto.venta.model.Pagsub;
import com.rosist.difunto.venta.repo.IPagsubRepo;
import com.rosist.difunto.venta.service.IPagsubService;

@RestController
@RequestMapping("/pagsub")
public class PagsubController {

	@Autowired
	private IPagsubRepo repo;

	@Autowired
	private IPagsubService service;

	private static final Logger log = LoggerFactory.getLogger(PagsubController.class);

	@GetMapping
	public ResponseEntity<List<Pagsub>> listar(@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes) throws Exception {
		return new ResponseEntity<List<Pagsub>>(service.listarPagsub(anno, mes, -1, 0), HttpStatus.OK);
	}

	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAll(@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "0") Integer size) throws Exception {

		List<Pagsub> content = new ArrayList<Pagsub>();
		String condicion = "";

		int inicio = page * size;
		Integer totalReg = repo.countPagsub(anno, mes);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = service.listarPagsub(anno, mes, page, size);

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
	public ResponseEntity<Pagsub> listarPorId(@PathVariable("id") String id) throws Exception {
		Pagsub obj = service.listarPorId(id);
		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Pagsub> registrar(@Valid @RequestBody Pagsub pagsub) throws Exception {
//		Cementerio obj = daoCementerio.insertaCementerio(cementerio);
		// localhost:8080/medicos/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getCodcem())
//				.toUri();
//		return ResponseEntity.created(location).build();
		validate(pagsub);
		return new ResponseEntity<>(service.registraTransaccion(pagsub), HttpStatus.CREATED); // 201
	}

	@PutMapping
	public ResponseEntity<Pagsub> modificar(@Valid @RequestBody Pagsub pagsub) throws Exception {
		String cError = "";
		validate(pagsub);
		return new ResponseEntity<>(service.modificaTransaccion(pagsub), HttpStatus.OK);
	}
	
	private void validate(Object obj) throws Exception {

		Pagsub pagsub = (Pagsub) obj;
		StringBuilder msg = new StringBuilder();

		if (pagsub.getFecps() == null) {
			msg.append(" Fecha de pago de subvencion no definida.");
		}
		if (pagsub.getCodent() == null || pagsub.getCodent().length() == 0) {
			msg.append("Entidad no definida.");
		}
		if (pagsub.getCodcp() == null || pagsub.getCodcp().length() == 0) {
			msg.append("Comprobante de pago no definido.");
		}
		if (pagsub.getCheque() == null || pagsub.getCheque().length() == 0) {
			msg.append("Cheque no definido.");
		}
		if (pagsub.getMtotot() == null) { // || pagacta.getFeccp().length()==0
			msg.append("Monto de pago no definido.");
		}

		if (pagsub.getSubvenciones() == null) {
			msg.append(" No se ha definido subvenciones a pagar.");
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
