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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rosist.difunto.dao.TrasladoDao;
import com.rosist.difunto.modelSbp.Ocufut;
import com.rosist.difunto.modelSbp.Traslado;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/traslado")
public class TrasladoController {

	@Autowired
	private TrasladoDao daoTraslado;
	
	private Logger logger = LoggerFactory.getLogger(OcufutController.class);

	@GetMapping
	public ResponseEntity<List<Traslado>> listar(
//			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
//			@RequestParam(value = "codocu", defaultValue = "0") Integer codocu,
//			@RequestParam(value = "estado", defaultValue = "") String estado,
//			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar,
//			@RequestParam(value = "fila1", defaultValue = "0") Integer fila1,
//			@RequestParam(value = "columna1", defaultValue = "0") Integer columna1,
//			@RequestParam(value = "apepat", defaultValue = "") String apepat,
//			@RequestParam(value = "apemat", defaultValue = "") String apemat,
//			@RequestParam(value = "nombres", defaultValue = "") String nombres
			) throws Exception {
		List<Traslado> traslados = new ArrayList<>();
		String condicion = "";
//		String condicion = (codcem!=0 ? " and d.codcem=" + codcem : "");
//		condicion += (codocu != 0 ? " and d.codocu=" + codocu : "");
//		condicion += (!estado.equals("") ? " and d.estado = '" + estado + "'" : "");
//		condicion += (codcuar != 0 ? " and d.codcuar=" + codcuar : "");
//		condicion += (fila1 != 0 ? " and d.fila1=" + fila1 : "");
//		condicion += (columna1 != 0 ? " and d.columna1=" + columna1 : "");
//		condicion += (!apepat.equals("") ? " and d.apepat like '%" + apepat + "%'" : "");
//		condicion += (!apemat.equals("") ? " and d.apemat like '%" + apemat + "%'" : "");
//		condicion += (!nombres.equals("") ? " and d.nombres like '%" + nombres + "%'" : "");

		traslados = daoTraslado.listaTraslado(condicion, "", "");
		return new ResponseEntity<List<Traslado>>(traslados, HttpStatus.OK);
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
//			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
//			@RequestParam(value = "codocu", defaultValue = "0") Integer codocu,
//			@RequestParam(value = "estado", defaultValue = "") String estado,
//			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar,
//			@RequestParam(value = "fila1", defaultValue = "0") Integer fila1,
//			@RequestParam(value = "columna1", defaultValue = "0") Integer columna1,
//			@RequestParam(value = "apepat", defaultValue = "") String apepat,
//			@RequestParam(value = "apemat", defaultValue = "") String apemat,
//			@RequestParam(value = "nombres", defaultValue = "") String nombres,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		List<Traslado> content = new ArrayList<Traslado>();
		String condicion = "";
//		String condicion = (codcem!=0 ? " and d.codcem=" + codcem : "");
//		condicion += (codocu != 0 ? " and d.codocu=" + codocu : "");
//		condicion += (!estado.equals("") ? " and d.estado = '" + estado + "'" : "");
//		condicion += (codcuar != 0 ? " and d.codcuar=" + codcuar : "");
//		condicion += (fila1 != 0 ? " and d.fila1=" + fila1 : "");
//		condicion += (columna1 != 0 ? " and d.columna1=" + columna1 : "");
//		condicion += (!apepat.equals("") ? " and d.apepat like '%" + apepat + "%'" : "");
//		condicion += (!apemat.equals("") ? " and d.apemat like '%" + apemat + "%'" : "");
//		condicion += (!nombres.equals("") ? " and d.nombres like '%" + nombres + "%'" : "");

		int inicio = page * size;
		Integer totalReg = daoTraslado.getTrasladoCount(condicion);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = daoTraslado.listaTraslado(condicion, " limit  " + inicio + ", " + size, "");

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
	
	@GetMapping("/{codtras}")
	public ResponseEntity<Traslado> buscaPorId(
			@PathVariable(value = "codtras") Integer codtras) {
		Traslado obj = daoTraslado.buscaTraslado(codtras);
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Traslado> registrar(@Valid @RequestBody Traslado traslado) throws Exception {
//		Cliente obj = daoCliente.insertaCliente(cliente);
		// localhost:8080/clientes/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdmed()).toUri();
//		return ResponseEntity.created(location).build();
		validate(traslado);
		return new ResponseEntity<>(daoTraslado.insertaTraslado(traslado), HttpStatus.CREATED); // 201
	}

	@PutMapping
	public ResponseEntity<Traslado> modificar(@Valid @RequestBody Traslado traslado) throws Exception {
		return new ResponseEntity<>(daoTraslado.modificaTraslado(traslado), HttpStatus.OK);
	}
	
	@DeleteMapping("/{codtras}")
	public ResponseEntity<Void> eliminar(
			@PathVariable(value = "codtras") Integer codtras) throws Exception {
		daoTraslado.eliminaTraslado(codtras);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
    public void validate(Object obj ){
        Traslado traslado = (Traslado)obj;
        StringBuilder msg = new StringBuilder();
        if (Integer.valueOf(traslado.getCliente().getTipdoccli().getCodigo())<0){
            msg.append("Tipo de Documento del Cliente no definido");
        } else if (Integer.valueOf(traslado.getCliente().getTipdoccli().getCodigo())>0) {
            if (traslado.getCliente().getDoccli()==null || traslado.getCliente().getDoccli().length()==0) {
                msg.append("Documento del cliente no definido");
            }
            if (traslado.getCliente().getNomcli()==null || traslado.getCliente().getNomcli().length()==0) {
                msg.append("Nombre del Cliente no Definido");
            }
        }
        if (traslado.getFectras()==null || traslado.getFectras().length()==0) {
            msg.append("Fecha de Traslado no registrada");
        }
        if (traslado.getDocref().getCodigo()==null || traslado.getDocref().getCodigo().length()==0 || traslado.getDocref().getCodigo().equals("-1") ) {
            msg.append("Tipo Documento de Referencia");
        }
        if (traslado.getCoddocrf()==null || traslado.getCoddocrf().length()==0) {
            msg.append("documento referencia no ingresado");
        }
        if (traslado.getFecdocrf()==null || traslado.getFecdocrf().length()==0) {
            msg.append("Fecha de documento referencia no ingresado");
        }
        if (traslado.getTiptras().getCodigo()==null || traslado.getTiptras().getCodigo().length()==0 || traslado.getTiptras().getCodigo().equals("-1") ) {
        	msg.append("Tipo de traslado no definido");
        } else {
            if (traslado.getTiptras().getCodigo().equals("-1")){
            	msg.append("Tipo de traslado no definido");
            } else {
                System.out.println("Cem erior " + traslado.getCemanterior());
                if (traslado.getDifuntoant()==null){
                	msg.append("difunto anterior no registrado");
                }
                if (traslado.getCemanterior().getCodcem()==0){
                	msg.append("cementerio anterior no registrado");
                }
                int nOpc = Integer.valueOf(traslado.getTiptras().getCodigo());
                System.out.println("nOpc.-=> " + nOpc);
                switch (nOpc) {
                    case 1:  
                    case 2:
                        System.out.println("entre a la opcion 2");
                        if (traslado.getDifuntonew().getReservado()==null || traslado.getDifuntonew().getReservado().length()==0 || traslado.getDifuntonew().getReservado().equals("-1")) {
                        	msg.append("Reservacion no definida");
                        }
                        if (traslado.getDifuntonew().getCementerio()==null){
                        	msg.append("Codigo cementerio nuevo no registrado");
                        } else if (traslado.getDifuntonew().getCementerio().getCodcem()==0){
                        	msg.append("Codigo cementerio nuevo no registrado");
                        }
                        if (traslado.getDifuntonew().getCuartel()==null){
                        	msg.append("Cuartel no Definido");
                        } else if (traslado.getDifuntonew().getCuartel().getCodcuar()==-1){
                        	msg.append("Cuartel no Definido");
                        }
                        if (traslado.getDifuntonew().getNicho()==null){
                        	msg.append("Nicho no definido");
                        } else if (traslado.getDifuntonew().getNicho().getFila1()==-1){
                        	msg.append("Nicho no definido");
                        } else if (traslado.getDifuntonew().getNicho().getCol1()==-1){
                        	msg.append("Nicho no definido");
                        }
                        break;
                    case 3:
//                        System.out.println("entre a la opcion 3");
//                        System.out.println("traslado.getDifuntonew().getReservado().-=> " + traslado.getDifuntonew().getReservado());
                        if (traslado.getDifuntonew().getMausoleo()==null){
                        	msg.append("Mausoleo nuevo no definido");
                        } else {
                            if (traslado.getDifuntonew().getMausoleo().getCodmau()==null){
                            	msg.append("Mausoleo nuevo no definido");
                            }
                        }
                        break;
                    case 4:  
                    case 5:
                    case 6:  
                        if (traslado.isLocal()){
//                            if (traslado.getDifuntonew().getCoddif()==0){
//                                errors.rejectValue("codtras","error.empty.field","Codigo difunto nuevo no registrado");
//                            }
//                            if (traslado.getCemnuevo().getCodcem()==0){
//                                errors.rejectValue("codtras","error.empty.field","Codigo cementerio nuevo no registrado");
//                            }
                        }
                        break;
                }
            }
        }
    }
	
}
