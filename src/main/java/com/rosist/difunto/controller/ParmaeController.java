package com.rosist.difunto.controller;

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

import com.rosist.difunto.dao.ParmaeDao;
import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.modelSbp.Parmae;

@RestController
@RequestMapping("/parmae")
public class ParmaeController {

	@Autowired
	private ParmaeDao daoParmae;
	
	private static final Logger logger = LoggerFactory.getLogger(ParmaeController.class);
	
	@GetMapping
	public ResponseEntity<List<Parmae>> listar(
	        @RequestParam(value="tipo",     defaultValue = "") String tipo,
	        @RequestParam(value="codigo",   defaultValue = "") String codigo,
	        @RequestParam(value="codigoAux", defaultValue = "") String codigoAux
			) throws Exception{
		
	      String condicion = (!tipo.equals("")? " and tipo='" + tipo + "'": "");
	      condicion += (!codigo.equals("")? " and codigo='" + codigo + "'": "");
	      condicion += (!codigoAux.equals("")? " and codigoAux='" + codigoAux + "'": "");
	      
		return new ResponseEntity<>(daoParmae.listaParmae(condicion,"",""), HttpStatus.OK);
	}

	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
	        @RequestParam(value="tipo",     defaultValue = "") String tipo,
	        @RequestParam(value="codigo",   defaultValue = "") String codigo,
	        @RequestParam(value="codigoAux", defaultValue = "") String codigoAux,
	        @RequestParam(value="page",     defaultValue = "0") int page,
	        @RequestParam(value="size",     defaultValue = "10") int size
	      ) {

	      List<Parmae> content = new ArrayList<Parmae>();
	      String condicion = (!tipo.equals("")? " and tipo='" + tipo + "'": "");
	      condicion += (!codigo.equals("")? " and codigo='" + codigo + "'": "");
	      condicion += (!codigoAux.equals("")? " and codigoAux='" + codigoAux + "'": "");
	      
	      int inicio = page * size;
	      Integer totalReg = daoParmae.getParmaeCount(condicion);
	      long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
	      boolean first = (page==0?true:false);
	      boolean last = (totalPages-1==page?true:false);
	      
	      content = daoParmae.listaParmae(condicion, " limit  " + inicio + ", " + size, "" );
	      
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
	
	@GetMapping("/idParmae")
	public ResponseEntity<Parmae> getAllParametros(
	        @RequestParam(value="tipo",     defaultValue = "") String tipo,
	        @RequestParam(value="codigo",   defaultValue = "") String codigo,
	        @RequestParam(value="codigoaux", defaultValue = "") String codigoaux
	      ) {
		
		Parmae obj = daoParmae.buscaParmae(tipo, codigo, codigoaux);
		return new ResponseEntity<>(obj, HttpStatus.OK);
		
	}
	

//	@GetMapping("/{id}")
//	public ResponseEntity<Parmae> listarPorId(@PathVariable("id") Integer id) throws Exception{
//		Parmae obj = service.listarPorId(id);
//		
//		if(obj == null) {
//			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
//		}
//		
//		return new ResponseEntity<>(obj, HttpStatus.OK);
//	}
	
	@GetMapping("/tipo/{tipo}")
	public ResponseEntity<List<Parmae>> listarPorTipo(@PathVariable("tipo") String tipo) throws Exception{
		List<Parmae> obj = daoParmae.listaParmae(" and tipo='" + tipo + "'","","");
		if(obj == null) {
			throw new ModelNotFoundException("PARAMETROS NO ENCONTRADO ");
		}
		
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@GetMapping("codigo/{tipo}/{codigo}")
	public ResponseEntity<Parmae> buscaPorCodigo(@PathVariable("tipo") String tipo, @PathVariable("codigo") String codigo) throws Exception{
		Parmae obj = daoParmae.buscaParmae(tipo, codigo, "");
		if(obj == null) {
			throw new ModelNotFoundException("PARAMETRO NO ENCONTRADO ");
		}
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Parmae> registrar(@Valid @RequestBody Parmae parmae) throws Exception {
//		Parmae obj = daoParmae.insertaParmae(parmae);
//		//localhost:8080/promotors/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdParmae()).toUri();
//		return ResponseEntity.created(location).build();
		return new ResponseEntity<>(daoParmae.insertaParmae(parmae), HttpStatus.CREATED);	// 201
	}

	@PutMapping
	public ResponseEntity<Parmae> modificar(@Valid @RequestBody Parmae parmae) throws Exception {
		return new ResponseEntity<>(daoParmae.modificaParmae(parmae), HttpStatus.OK);
	}
	
	@DeleteMapping("/elimina")
	public ResponseEntity<Void> eliminar(
	        @RequestParam(value="tipo",      defaultValue = "") String tipo,
	        @RequestParam(value="codigo",    defaultValue = "") String codigo,
	        @RequestParam(value="codigoAux", defaultValue = "") String codigoAux
			) throws Exception{

		daoParmae.eliminaParmae(tipo, codigo, codigoAux);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);		
	}
	
//	@GetMapping("/hateoas/{id}")
//	public EntityModel<Parmae> listarHateoasPorId(@PathVariable("id") Integer id) throws Exception{
//		Parmae obj = service.listarPorId(id);
//		
//		if(obj == null) {
//			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
//		}
//		
//		EntityModel<Parmae> recurso = EntityModel.of(obj);
//		//localhost:8080/promotors/hateoas/1
//		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).listarHateoasPorId(id));
//		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).listarHateoasPorId(id));
//		recurso.add(link1.withRel("parmae-recurso1"));
//		recurso.add(link2.withRel("parmae-recurso2"));
//		
//		return recurso;
//		
//	}
}
