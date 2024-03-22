package com.rosist.difunto.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
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

import com.rosist.difunto.dao.DifuntoDao;
import com.rosist.difunto.dao.EmpresaDao;
import com.rosist.difunto.dao.MausoleoDao;
import com.rosist.difunto.dao.SucursalDao;
import com.rosist.difunto.modelSbp.Difunto;
import com.rosist.difunto.modelSbp.Empresa;
import com.rosist.difunto.modelSbp.Mausoleo;
import com.rosist.difunto.modelSbp.Sucursal;
import com.rosist.difunto.reports.PdfDifuntos;
import com.rosist.difunto.reports.PdfMausoleo;
import com.rosist.difunto.reports.PdfMausoleoDifuntos;
import com.rosist.difunto.reports.XlsDifuntos;
import com.rosist.difunto.reports.XlsMausoleo;
import com.rosist.difunto.reports.XlsMausoleoDifuntos;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/difunto")
public class DifuntoController {

	@Autowired
	private DifuntoDao daoDifunto;
	
	@Autowired
	private EmpresaDao daoEmpresa;

	@Autowired
	private SucursalDao daoSucursal;

	@Autowired
	private MausoleoDao daoMausoleo;
	
    @Autowired
    private DriverManagerDataSource datasource;

//    public static final String EXCEL_HEADER="attachment; filename=reporte.xlsx";
//    public static final String EXCEL_MEDIA_TYPE="application/nvd.ms-excel";
    
    String ruc = "20147082861";
    Integer idsucursal = 6;
    
	private Logger logger = LoggerFactory.getLogger(DifuntoController.class);
	
	@GetMapping
	public ResponseEntity<List<Difunto>> listar(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "coddif", defaultValue = "0") Integer coddif,
			@RequestParam(value = "estado", defaultValue = "") String estado,
			@RequestParam(value = "tipent", defaultValue = "") String tipent,
			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar,
			@RequestParam(value = "fila1", defaultValue = "0") Integer fila1,
			@RequestParam(value = "columna1", defaultValue = "0") Integer columna1,
			@RequestParam(value = "apepat", defaultValue = "") String apepat,
			@RequestParam(value = "apemat", defaultValue = "") String apemat,
			@RequestParam(value = "nombres", defaultValue = "") String nombres
			) throws Exception {
		List<Difunto> difuntos = new ArrayList<>();
		String condicion = (codcem!=0 ? " and d.codcem=" + codcem : "");
		condicion += (coddif != 0 ? " and d.coddif=" + coddif : "");
		condicion += (!estado.equals("") ? " and d.estado = '" + estado + "'" : "");
		condicion += (!tipent.equals("") ? " and d.tipent = '" + tipent + "'" : "");
		condicion += (codcuar != 0 ? " and d.codcuar=" + codcuar : "");
		condicion += (fila1 != 0 ? " and d.fila1=" + fila1 : "");
		condicion += (columna1 != 0 ? " and d.columna1=" + columna1 : "");
		condicion += (!apepat.equals("") ? " and d.apepat like '%" + apepat + "%'" : "");
		condicion += (!apemat.equals("") ? " and d.apemat like '%" + apemat + "%'" : "");
		condicion += (!nombres.equals("") ? " and d.nombres like '%" + nombres + "%'" : "");

		difuntos = daoDifunto.listaDifunto(condicion, "", "");
		return new ResponseEntity<List<Difunto>>(difuntos, HttpStatus.OK);
	}
	
	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "coddif", defaultValue = "0") Integer coddif,
			@RequestParam(value = "estado", defaultValue = "") String estado,
			@RequestParam(value = "tipent", defaultValue = "") String tipent,
			@RequestParam(value = "codcuar", defaultValue = "0") Integer codcuar,
			@RequestParam(value = "fila1", defaultValue = "0") Integer fila1,
			@RequestParam(value = "columna1", defaultValue = "0") Integer columna1,
			@RequestParam(value = "apepat", defaultValue = "") String apepat,
			@RequestParam(value = "apemat", defaultValue = "") String apemat,
			@RequestParam(value = "nombres", defaultValue = "") String nombres,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) {

		List<Difunto> content = new ArrayList<Difunto>();
		String condicion = (codcem!=0 ? " and d.codcem=" + codcem : "");
		condicion += (coddif != 0 ? " and d.coddif=" + coddif : "");
		condicion += (!estado.equals("") ? " and d.estado = '" + estado + "'" : "");
		condicion += (!tipent.equals("") ? " and d.tipent = '" + tipent + "'" : "");
		condicion += (codcuar != 0 ? " and d.codcuar=" + codcuar : "");
		condicion += (fila1 != 0 ? " and d.fila1=" + fila1 : "");
		condicion += (columna1 != 0 ? " and d.columna1=" + columna1 : "");
		condicion += (!apepat.equals("") ? " and d.apepat like '%" + apepat + "%'" : "");
		condicion += (!apemat.equals("") ? " and d.apemat like '%" + apemat + "%'" : "");
		condicion += (!nombres.equals("") ? " and d.nombres like '%" + nombres + "%'" : "");

		int inicio = page * size;
		Integer totalReg = daoDifunto.getDifuntoCount(condicion);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = daoDifunto.listaDifunto(condicion, " limit  " + inicio + ", " + size, "");

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
	
	@GetMapping("/idDifunto")
	public ResponseEntity<Difunto> buscaPorId(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "coddif", defaultValue = "0") Integer coddif) {
		Difunto obj = daoDifunto.buscaDifunto(codcem, coddif);
		return new ResponseEntity<>(obj, HttpStatus.OK);
	}
	
	@PostMapping
	public ResponseEntity<Difunto> registrar(@Valid @RequestBody Difunto difunto) throws Exception {
//		Cliente obj = daoCliente.insertaCliente(cliente);
		// localhost:8080/clientes/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdmed()).toUri();
//		return ResponseEntity.created(location).build();
		validate(difunto);
		return new ResponseEntity<>(daoDifunto.insertaDifunto(difunto), HttpStatus.CREATED); // 201
	}
	
	@PutMapping
	public ResponseEntity<Difunto> modificar(@Valid @RequestBody Difunto difunto) throws Exception {
		return new ResponseEntity<>(daoDifunto.modificaDifunto(difunto), HttpStatus.OK);
	}
	
	@DeleteMapping("/elimina")
	public ResponseEntity<Void> eliminar(
			@RequestParam(value = "codcem", defaultValue = "0") Integer codcem,
			@RequestParam(value = "coddif", defaultValue = "0") Integer coddif) throws Exception {
		daoDifunto.eliminaDifunto(codcem, coddif);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@GetMapping(value = "/reporteDifuntos", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE) // APPLICATION_PDF_VALUE
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
		        PdfDifuntos _reporte = new PdfDifuntos(parametros);
		        data = _reporte.creaReporte();
			}
		} else if (formato==2) {
			if (reporte==1) {
		        XlsDifuntos _reporte = new XlsDifuntos(parametros);
		        data = _reporte.creaReporte();
			}
		}
//		return ResponseEntity.ok()
//				.header(HttpHeaders.CONTENT_DISPOSITION, EXCEL_HEADER)
//				.contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
//				.cacheControl(CacheControl.noCache())
//				.body(data);
		return new ResponseEntity<byte[]>(data, HttpStatus.OK);
	}

	public void validate(Object obj) throws Exception {
		Difunto difunto = (Difunto)obj;
		StringBuilder msg = new StringBuilder();
		
        if (difunto.getReservado()==null || difunto.getReservado().length()==0) {
            msg.append(" Reserva no definida.");
        } else {
        	if (difunto.getReservado().equals("-1")){
        		msg.append(" Reserva no especificada.");
        	} else {
        		if (difunto.getReservado().equals("S")){
        			if (difunto.getOcufut().getCodocu()<=0) {
        				msg.append(" Ocupacion Futura no definida.");
        			}
        		}
        		
        	}
        }
        
        if (difunto.getCementerio()==null) {
            msg.append(" cementerio no definido.");
        } else {
        	if (difunto.getCementerio().getCodcem()<=0) {
        		msg.append(" Debe Ingresar Cementerio.");
        	}
        }
        
        if (difunto.getTipo_entierro()==null) {
        	msg.append(" tipo de entierro no definido.");
        } else {
        	int xTipEnt = Integer.parseInt(difunto.getTipo_entierro().getCodigo());
        	switch (xTipEnt) {
        	case 1:     // en cuatel
//        		System.out.println("primer caso.-=> " );
        		if (difunto.getCuartel().getCodcuar()<=0) {
        			msg.append(" Debe Ingresar Cuartel.");
        		}
        		if (difunto.getNicho().getFila1()<=0) {
        			msg.append(" Fila no Definida.");
        		}
        		if (difunto.getNicho().getCol1()<=0) {
        			msg.append(" Columna no Definida.");
        		}
        		break;
        	case 2:     // en mausoleo
//        		System.out.println("segundo caso.-=> en mausoleos" );
        		Mausoleo mausoleo = null;
        		if (difunto.getMausoleo()==null) {
        			msg.append("mausoleo no definido");
            		System.out.println("es nulo" );
        		} else {
//            		System.out.println("no es nulo" );
        			if (difunto.getMausoleo().getCodmau()<=0) {
        				msg.append(" Mausoleo no especificado.");
        			} else {
//                		System.out.println("buscando mausoleo" );
        				int xCementerio = difunto.getCementerio().getCodcem();
        				int xCodMau = difunto.getMausoleo().getCodmau();
        				mausoleo = daoMausoleo.buscaMausoleo(xCementerio, xCodMau);
//                		System.out.println("buscando mausoleo" + mausoleo );
        				if (!(mausoleo.getTotdif()>mausoleo.getNumdif())){
        					msg.append(" No se puede definir. numero de difuntos excedido.");
        				}
        			}
        		}
        		break;
        	case 3:
        		break;
        	default:
        		msg.append(" Tipo de entierro mal definido.");
        	}
        }
        if (difunto.getNombres()==null || difunto.getNombres().length()==0) {
            msg.append(" Nombre del difunto no ingresado.");
        }
        if (difunto.getApepat()==null || difunto.getApepat().length()==0) {
            msg.append(" Apellido paterno no ingresado.");
        }
        if (difunto.getApemat()==null || difunto.getApemat().length()==0) {
            msg.append(" Apellido materno no ingresado.");
        }
        if (difunto.getSexodif()==null || difunto.getSexodif().length()==0|| difunto.getSexodif().equals("0")) {
            msg.append(" Sexo no definido.");
        }
        if (difunto.getFecfall()==null || difunto.getFecfall().length()==0) {
            msg.append(" Fecha de fallecimiento no ingresada.");
        }
        if (difunto.getFecsep()==null || difunto.getFecsep().length()==0) {
            msg.append(" Fecha de sepelio no definida.");
        }
        if (difunto.getCliente()==null) {
        	msg.append(" Cliente no definido.");
        } else {
        	if (difunto.getCliente().getTipdoccli()==null) {
        		msg.append(" Tipo de documento de Cliente no definido.");
        	} else {
        		if (Integer.valueOf(difunto.getCliente().getTipdoccli().getCodigo())<0){
        			msg.append(" Tipo de documento de Cliente no especificado.");
        		} else if (Integer.valueOf(difunto.getCliente().getTipdoccli().getCodigo())>0) {
        			if (difunto.getCliente().getDoccli()==null || difunto.getCliente().getDoccli().length()==0) {
        				msg.append(" Documento no definido.");
        			}
        			if (difunto.getCliente().getNomcli()==null || difunto.getCliente().getNomcli().length()==0) {
        				msg.append(" Nombre del Cliente no Definido.");
        			}
        		}
        		
        	}
        }

		if (difunto.getArchivo() != null) {
			String cArchivo = difunto.getArchivo().getOriginalFilename();
			int ubicpto = cArchivo.indexOf(".");
			if (difunto.getArchivo().getSize() == 0) {
//				errors.rejectValue("archivo", "missing.file");
			} else {
				if (difunto.getArchivo().getSize() >= 5242880) {
					msg.append(" missing.over.");
				}
				if (!cArchivo.substring(ubicpto, cArchivo.length()).toUpperCase().equals(".PDF")) {
					msg.append(" missing.pdf.");
				}
			}
		}
//		System.out.println("validacion satisfactoria" );
		if (!msg.isEmpty()) {
			throw new Exception(msg.toString());
		}
	}
}
