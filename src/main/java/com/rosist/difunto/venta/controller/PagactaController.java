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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.venta.dto.PagactaDto;
import com.rosist.difunto.venta.model.Pagacta;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.IPagactaRepo;
import com.rosist.difunto.venta.service.IPagactaService;

@RestController
@RequestMapping("/pagacta")
public class PagactaController {

	@Autowired
	private IPagactaService service;
	
	@Autowired
	private IPagactaRepo repo;
	
	private static final Logger log = LoggerFactory.getLogger(PagactaController.class);
	
	@GetMapping
	public ResponseEntity<List<Pagacta>> listar(
			@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes,
			@RequestParam(value = "cliente", defaultValue = "") String cliente,
			@RequestParam(value = "estado", defaultValue = "") String estado
			) throws Exception {
		return new ResponseEntity<List<Pagacta>>(service.listarPagacta(anno, mes, cliente, estado, -1, 0), HttpStatus.OK);
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
		
		List<Pagacta> content = new ArrayList<Pagacta>();
		String condicion = "";

		int inicio = page * size;
		Integer totalReg = repo.countVenta(anno, mes, cliente, estado);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = service.listarPagacta(anno, mes, cliente, estado, page, size);

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
	public ResponseEntity<Pagacta> listarPorId(@PathVariable("id") String id) throws Exception {
		Pagacta obj = service.listarPorId(id);
		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Pagacta> registrar(@Valid @RequestBody PagactaDto pagactaDto) throws Exception {
//		Cementerio obj = daoCementerio.insertaCementerio(cementerio);
		// localhost:8080/medicos/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getCodcem())
//				.toUri();
//		return ResponseEntity.created(location).build();
		log.info("hola entrando");
		validate(pagactaDto);
		return new ResponseEntity<>(service.registraTransaccion(pagactaDto), HttpStatus.CREATED);		// 201
	}

	private void validate(Object obj) throws Exception {
		
		PagactaDto pagacta = (PagactaDto)obj;
		StringBuilder msg = new StringBuilder();
		
		if (pagacta.getFecpac()==null) {
            msg.append(" Fecha de pago a cuenta no definida.");
		}
    	if (pagacta.getCredito()==null) {
            msg.append(" No se ha definido el credito.");
    	} else {
   			if (pagacta.getCredito().getCodcre()==null) {
                msg.append(" No se ha definido codigo del credito.");
   			}
    	}
        if (pagacta.getCompag()==null || pagacta.getCompag().length()==0) {
            msg.append("Comprobante de pago no definido.");
        }
        if (pagacta.getSerie()==null || pagacta.getSerie().length()==0) {
            msg.append("Serie de Comprobante de pago no definido.");
        }
        if (pagacta.getCodcp()==null || pagacta.getCodcp().length()==0) {
            msg.append("Codigo de Comprobante de pago no definido.");
        }
        if (pagacta.getFeccp()==null) {		//  || pagacta.getFeccp().length()==0
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
        if (pagacta.getInteres()==null) {
            msg.append(" Interes no definido.");
        }
        if (pagacta.getMtoaju()==null) {
            msg.append(" Monto ajustado no definido no definido.");
        }
        if (pagacta.getMtoamo()==null) {
            msg.append(" Monto amortizado no definido no definido.");
        }
        if (pagacta.getObserv()==null) {
            msg.append(" Observacion no definido no definido.");
        }
		if (!msg.isEmpty()) {
			throw new Exception(msg.toString());
		}
	}
	
	
}
