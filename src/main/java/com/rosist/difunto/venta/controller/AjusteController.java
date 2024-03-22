package com.rosist.difunto.venta.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.venta.dto.AjusteDto;
import com.rosist.difunto.venta.model.Ajuste;
import com.rosist.difunto.venta.repo.IAjusteRepo;
import com.rosist.difunto.venta.service.IAjusteService;

@RestController
@RequestMapping("/ajuste")
public class AjusteController {

	@Autowired
	private IAjusteService service;
	
	@Autowired
	private IAjusteRepo repo;
	
	@GetMapping
	public ResponseEntity<List<Ajuste>> listar(
			@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes,
			@RequestParam(value = "cliente", defaultValue = "") String cliente,
			@RequestParam(value = "estado", defaultValue = "") String estado
			) throws Exception {
		return new ResponseEntity<List<Ajuste>>(service.listarAjuste(anno, mes, cliente, estado, -1, 0), HttpStatus.OK);
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAll(
			@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes,
			@RequestParam(value = "cliente", defaultValue = "") String cliente,
			@RequestParam(value = "estado", defaultValue = "") String estado,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "0") Integer size
			) throws Exception {
		
		List<Ajuste> content = new ArrayList<Ajuste>();
		String condicion = "";

		int inicio = page * size;
		Integer totalReg = repo.countAjuste(anno, mes, cliente, estado);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = service.listarAjuste(anno, mes, cliente, estado, page, size);

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
	public ResponseEntity<Ajuste> listarPorId(@PathVariable("id") String id) throws Exception {
		Ajuste obj = service.listarPorId(id);
		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Ajuste> registrar(@Valid @RequestBody AjusteDto ajusteDto) throws Exception {
//		Cementerio obj = daoCementerio.insertaCementerio(cementerio);
		// localhost:8080/medicos/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getCodcem())
//				.toUri();
//		return ResponseEntity.created(location).build();
//		log.info("hola entrando");
		validate(ajusteDto);
		return new ResponseEntity<>(service.registraTransaccion(ajusteDto), HttpStatus.CREATED);		// 201
	}

	private void validate(Object obj) throws Exception {
		
		AjusteDto ajuste = (AjusteDto)obj;
		StringBuilder msg = new StringBuilder();
		
		if (ajuste.getFecaju()==null) {
            msg.append(" Fecha de pago a cuenta no definida.");
		}
    	if (ajuste.getCredito()==null) {
            msg.append(" No se ha definido el credito.");
    	} else {
   			if (ajuste.getCredito().getCodcre()==null) {
                msg.append(" No se ha definido codigo del credito.");
   			}
    	}
        if (ajuste.getCompag()==null || ajuste.getCompag().length()==0) {
            msg.append("Comprobante de pago no definido.");
        }
        if (ajuste.getSerie()==null || ajuste.getSerie().length()==0) {
            msg.append("Serie de Comprobante de pago no definido.");
        }
        if (ajuste.getCodcp()==null || ajuste.getCodcp().length()==0) {
            msg.append("Codigo de Comprobante de pago no definido.");
        }
        if (ajuste.getFeccp()==null) {		//  || pagacta.getFeccp().length()==0
            msg.append("Fecha de Comprobante de Pago no definido.");
        }
//        if (pagacta.getCliente()==null) {
//            msg.append("Cliente no definido.");
//        } else {
//   			if (pagacta.getCliente().getCodcli()==null) {
//                msg.append(" Codigo de Cliente no definido.");
//   			}
//   	        if (pagacta.getCliente().getTipdoccli()==null || pagacta.getCliente().getTipdoccli().length()==0) {
//   	            msg.append("Tipo de documento del cliente no definido.");
//   	        }
//   	        if (pagacta.getCliente().getDoccli()==null || pagacta.getCliente().getDoccli().length()==0) {
//   	            msg.append("Documento del cliente no definido.");
//   	        }
//        }
        if (ajuste.getMtoaju()==null) {
            msg.append(" Monto ajustado no definido no definido.");
        }
        if (ajuste.getObserv()==null) {
            msg.append(" Observacion no definido no definido.");
        }
		if (!msg.isEmpty()) {
			throw new Exception(msg.toString());
		}
	}
	
}
