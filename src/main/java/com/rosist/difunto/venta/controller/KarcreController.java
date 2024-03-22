//package com.rosist.difunto.venta.controller;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.rosist.difunto.venta.model.Karcre;
//import com.rosist.difunto.venta.repo.IKarcreRepo;
//import com.rosist.difunto.venta.service.IKarcreService;
//
//@RestController
//@RequestMapping("/karcre")
//public class KarcreController {
//
//	@Autowired
//	private IKarcreService service;
//	
//	@Autowired
//	private IKarcreRepo repo;
//	
//	private static final Logger log = LoggerFactory.getLogger(KarcreController.class);
//
//	@GetMapping
//	public ResponseEntity<List<Karcre>> listar(
//			@RequestParam(value = "codcre", defaultValue = "") String codcre,
//			@RequestParam(value = "tipmov", defaultValue = "") String tipmov,
//			@RequestParam(value = "codmov", defaultValue = "") String codmov
//			) throws Exception {
//		return new ResponseEntity<List<Karcre>>(service.listaKarcre(codcre, tipmov, codmov, -1, 0), HttpStatus.OK);
//	}
//
//	@GetMapping("/pageable")
//	public ResponseEntity<Map<String, Object>> getAllParametros(
//			@RequestParam(value = "codcre", defaultValue = "") String codcre,
//			@RequestParam(value = "tipmov", defaultValue = "") String tipmov,
//			@RequestParam(value = "codmov", defaultValue = "") String codmov,
//			@RequestParam(value = "page", defaultValue = "0") int page,
//			@RequestParam(value = "size", defaultValue = "10") int size) throws Exception {
//
//		List<Karcre> content = new ArrayList<Karcre>();
//		String condicion = "";
//
//		int inicio = page * size;
//		Integer totalReg = repo.listaKarcre_count(codcre, tipmov, codmov);
//		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
//		boolean first = (page == 0 ? true : false);
//		boolean last = (totalPages - 1 == page ? true : false);
//
//		content = service.listaKarcrePorCredito(codcre, tipmov, codmov, page, size);
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("content", content);
//		response.put("number", page);
//		response.put("size", size);
//		response.put("totalElements", totalReg);
//		response.put("totalPages", totalPages);
//		response.put("first", first);
//		response.put("last", last);
//
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}
//	
//}