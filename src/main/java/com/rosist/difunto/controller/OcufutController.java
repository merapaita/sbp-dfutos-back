package com.rosist.difunto.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rosist.difunto.dao.EmpresaDao;
import com.rosist.difunto.dao.OcuFutDao;
import com.rosist.difunto.dao.SucursalDao;
import com.rosist.difunto.modelSbp.Difunto;
import com.rosist.difunto.modelSbp.Empresa;
import com.rosist.difunto.modelSbp.Ocufut;
import com.rosist.difunto.modelSbp.Sucursal;
import com.rosist.difunto.reports.PdfDifuntos;
import com.rosist.difunto.reports.PdfOcufut;
import com.rosist.difunto.reports.XlsDifuntos;
import com.rosist.difunto.reports.XlsOcufut;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ocufut")
public class OcufutController {

	@Autowired
	private OcuFutDao daoOcufut;
	
	@Autowired
	private EmpresaDao daoEmpresa;

	@Autowired
	private SucursalDao daoSucursal;

    @Autowired
    private DriverManagerDataSource datasource;
    
	private Logger logger = LoggerFactory.getLogger(OcufutController.class);
	
    String ruc = "20147082861";
    Integer idsucursal = 6;
    
	@GetMapping
	public ResponseEntity<List<Ocufut>> listar(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codocu", defaultValue = "0") Integer codocu,
			@RequestParam(value = "estado", defaultValue = "") String estado,
			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar,
			@RequestParam(value = "fila1", defaultValue = "0") Integer fila1,
			@RequestParam(value = "columna1", defaultValue = "0") Integer columna1,
			@RequestParam(value = "apepat", defaultValue = "") String apepat,
			@RequestParam(value = "apemat", defaultValue = "") String apemat,
			@RequestParam(value = "nombres", defaultValue = "") String nombres
			) throws Exception {
		List<Ocufut> ocufuts = new ArrayList<>();
		String condicion = (codcem!=0 ? " and d.codcem=" + codcem : "");
		condicion += (codocu != 0 ? " and d.codocu=" + codocu : "");
		condicion += (!estado.equals("") ? " and d.estado = '" + estado + "'" : "");
		condicion += (codcuar != 0 ? " and d.codcuar=" + codcuar : "");
		condicion += (fila1 != 0 ? " and d.fila1=" + fila1 : "");
		condicion += (columna1 != 0 ? " and d.columna1=" + columna1 : "");
		condicion += (!apepat.equals("") ? " and d.apepat like '%" + apepat + "%'" : "");
		condicion += (!apemat.equals("") ? " and d.apemat like '%" + apemat + "%'" : "");
		condicion += (!nombres.equals("") ? " and d.nombres like '%" + nombres + "%'" : "");

		ocufuts = daoOcufut.listaOcuFut(condicion, "", "");
		return new ResponseEntity<List<Ocufut>>(ocufuts, HttpStatus.OK);
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codocu", defaultValue = "0") Integer codocu,
			@RequestParam(value = "estado", defaultValue = "") String estado,
			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar,
			@RequestParam(value = "fila1", defaultValue = "0") Integer fila1,
			@RequestParam(value = "columna1", defaultValue = "0") Integer columna1,
			@RequestParam(value = "apepat", defaultValue = "") String apepat,
			@RequestParam(value = "apemat", defaultValue = "") String apemat,
			@RequestParam(value = "nombres", defaultValue = "") String nombres,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		List<Ocufut> content = new ArrayList<Ocufut>();
		String condicion = (codcem!=0 ? " and d.codcem=" + codcem : "");
		condicion += (codocu != 0 ? " and d.codocu=" + codocu : "");
		condicion += (!estado.equals("") ? " and d.estado = '" + estado + "'" : "");
		condicion += (codcuar != 0 ? " and d.codcuar=" + codcuar : "");
		condicion += (fila1 != 0 ? " and d.fila1=" + fila1 : "");
		condicion += (columna1 != 0 ? " and d.columna1=" + columna1 : "");
		condicion += (!apepat.equals("") ? " and d.apepat like '%" + apepat + "%'" : "");
		condicion += (!apemat.equals("") ? " and d.apemat like '%" + apemat + "%'" : "");
		condicion += (!nombres.equals("") ? " and d.nombres like '%" + nombres + "%'" : "");

		int inicio = page * size;
		Integer totalReg = daoOcufut.getOcuFutCount(condicion);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = daoOcufut.listaOcuFut(condicion, " limit  " + inicio + ", " + size, "");

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
	
	@GetMapping("/idOcufut")
	public ResponseEntity<Ocufut> buscaPorId(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codocu", defaultValue = "0") Integer codocu) {
		Ocufut obj = daoOcufut.buscaOcuFut(codcem, codocu);
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Ocufut> registrar(@Valid @RequestBody Ocufut ocufut) throws Exception {
//		Cliente obj = daoCliente.insertaCliente(cliente);
		// localhost:8080/clientes/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdmed()).toUri();
//		return ResponseEntity.created(location).build();
		validate(ocufut);
		return new ResponseEntity<>(daoOcufut.insertaOcuFut(ocufut), HttpStatus.CREATED); // 201
	}

	@PutMapping
	public ResponseEntity<Ocufut> modificar(@Valid @RequestBody Ocufut ocufut) throws Exception {
		return new ResponseEntity<>(daoOcufut.modificaOcuFut(ocufut), HttpStatus.OK);
	}
	
	@DeleteMapping("/elimina")
	public ResponseEntity<Void> eliminar(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "codocu", defaultValue = "0") Integer codocu) throws Exception {
		daoOcufut.eliminaOcuFut(codcem, codocu);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping(value = "/reporteOcufuts", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE) // APPLICATION_PDF_VALUE
	// //APPLICATION_OCTET_STREAM_VALUE
	public ResponseEntity<byte[]> generarReporte(		//	Resource
			@RequestParam(value = "reporte", defaultValue = "1") Integer reporte,
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "tipent", defaultValue = "1") String tipent,
			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar,
			@RequestParam(value="fecini", defaultValue = "") String fecini,
            @RequestParam(value="fecfin", defaultValue = "") String fecfin,

            @RequestParam(value="apepat", defaultValue = "") String apepat,
            @RequestParam(value="apemat", defaultValue = "") String apemat,
            @RequestParam(value="nombres", defaultValue = "") String nombres,
			@RequestParam(value = "orden", defaultValue = "1") Integer orden,
			@RequestParam(value = "formato", defaultValue = "1") Integer formato,
            @RequestParam(value="bdifuntos", required=false) boolean bdifuntos) throws Exception {
		logger.info("generarReporte...");
		byte[] data = null;
		
		String _orden = "";
		
		String filtro = (codcem!=0 ? " and d.codcem=" + codcem : "");
		filtro += (!tipent.equals("") ? " and d.tipent = '" + tipent + "'" : "");
		filtro += (codcuar != 0 ? " and d.codcuar=" + codcuar : "");
		filtro += (!fecini.equals("") ? " and d.fecfall >= '" + fecini + "'" : "");
		filtro += (!fecfin.equals("") ? " and d.fecfall <= '" + fecfin + "'" : "");
		filtro += (!apepat.equals("") ? " and d.apepat like '%" + apepat + "%'" : "");
		filtro += (!apemat.equals("") ? " and d.apemat like '%" + apemat + "%'" : "");
		filtro += (!nombres.equals("") ? " and d.nombres like '%" + nombres + "%'" : "");
		
    	if (orden==1) _orden = "codcem, ciddif";
        else if (orden==2) _orden = "codcem, familia";
        else if (orden==3) _orden = "codcem, nomlote";

		Empresa empresa = daoEmpresa.buscaEmpresa(ruc);
        Sucursal sucursal = daoSucursal.buscaSucursal(idsucursal);
    	
		Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("datasource", datasource);
		parametros.put("condicion", filtro);
		parametros.put("orden", _orden);
        parametros.put("empresa", empresa);
        parametros.put("sucursal", sucursal);
		if (formato==1) {
			if (reporte==1) {
		        PdfOcufut _reporte = new PdfOcufut(parametros);
		        data = _reporte.creaReporte();
			}
		} else if (formato==2) {
			if (reporte==1) {
		        XlsOcufut _reporte = new XlsOcufut(parametros);
		        data = _reporte.creaReporte();
			}
		}
		return new ResponseEntity<byte[]>(data, HttpStatus.OK);
	}

    public void validate(Object obj) {
        Ocufut ocufut = (Ocufut) obj;
        StringBuilder msg = new StringBuilder();
        
//        System.out.println("ocupacion Futura.-> " + ocufut.toString());
        if (ocufut.getEdad_m() > 11) {
        	msg.append("Numero de meses errado");
        }

        if (ocufut.getEdad_d() > 29) {
            msg.append("Numero de dias errado");
        }

        if (ocufut.getApepat() == null || ocufut.getApepat().length() == 0) {
            msg.append("Apellido paterno del Ocupante no ingresado");
        }
        if (ocufut.getApemat() == null || ocufut.getApemat().length() == 0) {
            msg.append("Apellido materno del ocupante no ingresado");
        }
        if (ocufut.getNombres() == null || ocufut.getNombres().length() == 0) {
        	msg.append("Nombre del ocupante no ingresado");
        }
        if (ocufut.getSexo() == null || ocufut.getSexo().length() == 0 || ocufut.getSexo().equals("0")) {
            msg.append("Sexo del difunto no ingresado");
        }
        if (ocufut.getCementerio().getCodcem() <= 0) {
            msg.append("Debe Ingresar Cementerio");
        }

        if (ocufut.getCuartel().getCodcuar() <= 0) {
            msg.append("Debe Ingresar Cuartel");
        }
        if (ocufut.getNicho() == null) {
            msg.append("Nicho no definido");
        } else {
            if (ocufut.getNicho().getFila1() <= 0) {
                msg.append("Fila no Definida");
            }
            if (ocufut.getNicho().getCol1() <= 0) {
                msg.append("Columna no Definida");
            }
        }
        if (Integer.valueOf(ocufut.getCliente().getTipdoccli().getCodigo())<0){
            msg.append("Tipo de Cliente no definido");
        } else if (Integer.valueOf(ocufut.getCliente().getTipdoccli().getCodigo())>0) {
            if (ocufut.getCliente().getDoccli()==null || ocufut.getCliente().getDoccli().length()==0) {
                msg.append("Documento no definido");
            }
            if (ocufut.getCliente().getNomcli()==null || ocufut.getCliente().getNomcli().length()==0) {
                msg.append("Nombre del Cliente no Definido");
            }
        }
    }
	
}
