package com.rosist.difunto.venta.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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

import com.google.gson.stream.JsonReader;
import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Difunto;
import com.rosist.difunto.venta.model.Cliente;
import com.rosist.difunto.venta.model.Credito;
import com.rosist.difunto.venta.model.Donacion;
import com.rosist.difunto.venta.model.Subvencion;
import com.rosist.difunto.venta.model.Venta;
import com.rosist.difunto.venta.repo.IVentaRepo;
import com.rosist.difunto.venta.service.IVentaService;

//import jakarta.validation.Valid;

@RestController
@RequestMapping("/venta")
public class VentaController {
	
	@Autowired
	private IVentaRepo repo;
	
	@Autowired
	private IVentaService service;

	private static final Logger log = LoggerFactory.getLogger(VentaController.class);

	@GetMapping
	public ResponseEntity<List<Venta>> listar(
			@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes,
			@RequestParam(value = "cliente", defaultValue = "") String cliente,
			@RequestParam(value = "estado", defaultValue = "") String estado
			) throws Exception {
//		return new ResponseEntity<List<Venta>>(service.listar(), HttpStatus.OK);
		return new ResponseEntity<List<Venta>>(service.listarVentas(anno, mes, cliente, estado, -1, 0), HttpStatus.OK);
	}

	@GetMapping("/pageable")
	public ResponseEntity<Map<String, Object>> getAllParametros(
			@RequestParam(value = "anno", defaultValue = "0") Integer anno,
			@RequestParam(value = "mes", defaultValue = "0") Integer mes,
			@RequestParam(value = "cliente", defaultValue = "") String cliente,
			@RequestParam(value = "estado", defaultValue = "") String estado,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size) throws Exception {

		List<Venta> content = new ArrayList<Venta>();
		String condicion = "";

		int inicio = page * size;
		Integer totalReg = repo.countVenta(anno, mes, cliente, estado);
		long totalPages = (size > 0 ? (totalReg - 1) / size + 1 : 0);
		boolean first = (page == 0 ? true : false);
		boolean last = (totalPages - 1 == page ? true : false);

		content = service.listarVentas(anno, mes, cliente, estado, page, size);

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
	public ResponseEntity<Venta> listarPorId(@PathVariable("id") String id) throws Exception {
		Venta obj = service.listarPorId(id);
		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}

		return new ResponseEntity<>(obj, HttpStatus.OK);
	}

	
	@PostMapping
	public ResponseEntity<Venta> registrar(@Valid @RequestBody Venta venta) throws Exception {
//		Cementerio obj = daoCementerio.insertaCementerio(cementerio);
		// localhost:8080/medicos/1
//		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getCodcem())
//				.toUri();
//		return ResponseEntity.created(location).build();
		validate(venta);
		return new ResponseEntity<>(service.registraTransaccion(venta), HttpStatus.CREATED);		// 201
	}

	@PutMapping
	public ResponseEntity<Venta> modificar(@Valid @RequestBody Venta venta) throws Exception {
		String cError = "";
		validate(venta);
		return new ResponseEntity<>(service.modificaTransaccion(venta), HttpStatus.OK);
	}
	
	@PostMapping(value = "/migra") // APPLICATION_PDF_VALUE //APPLICATION_OCTET_STREAM_VALUE
	public ResponseEntity<Integer> Migra() throws Exception {
		log.info("migrando...");
		int ret = 0;
		JsonReader reader;
		try {
			reader = new JsonReader(
					new FileReader("c:\\trabajo\\desarrollo\\documentos\\ventas\\mig\\migVentas.json"));
			reader.beginArray();
			while (reader.hasNext()) {
				reader.beginObject();
				Venta venta = new Venta();
				Cliente cliente = new Cliente();
				Cliente aval = new Cliente();
				Credito credito = new Credito();
				Donacion donacion = new Donacion();
				while (reader.hasNext()) {
					String name = reader.nextName();
//log.info("name:" + name);					
					if (name.equals("codvta")) {
						venta.setCodvta(reader.nextString());
					} else if (name.equals("fecvta")) {
						venta.setFecvta(LocalDate.parse(reader.nextString()));
					} else if (name.equals("tipvta")) {
						venta.setTipvta(reader.nextString());
					} else if (name.equals("convta")) {
						venta.setConvta(reader.nextString());
					} else if (name.equals("compag")) {
						venta.setCompag(reader.nextString());
					} else if (name.equals("serie")) {
						venta.setSerie(reader.nextString());
					} else if (name.equals("codcp")) {
						venta.setCodcp(reader.nextString());
					} else if (name.equals("feccp")) {
						venta.setFeccp(LocalDate.parse(reader.nextString()));
					} else if (name.equals("cliente")) {
						reader.beginObject();
						while (reader.hasNext()) {
							String nameCliente = reader.nextName();
//log.info("nameCliente:" + nameCliente );							
							if (nameCliente.equals("codcli")) {
								cliente.setCodcli(reader.nextInt());
							} else if (nameCliente.equals("nomcli")) {
								cliente.setNomcli(reader.nextString());
							} else if (nameCliente.equals("dircli")) {
								cliente.setDircli(reader.nextString());
							} else if (nameCliente.equals("tipdoccli")) {
								cliente.setTipdoccli(reader.nextString());
							} else if (nameCliente.equals("doccli")) {
								cliente.setDoccli(reader.nextString());
							} else {// unexpected value, skip it or generate error
								reader.skipValue();
							}
						}
//log.info("cliente:" + cliente );							
						venta.setCliente(cliente);
						reader.endObject();
					} else if (name.equals("aval")) {
						reader.beginObject();
						while (reader.hasNext()) {
							String nameAval = reader.nextName();
//log.info("nameAval:" + nameAval );							
							if (nameAval.equals("codcli")) {
								aval.setCodcli(reader.nextInt());
							} else if (nameAval.equals("nomcli")) {
								aval.setNomcli(reader.nextString());
							} else if (nameAval.equals("dircli")) {
								aval.setDircli(reader.nextString());
							} else if (nameAval.equals("tipdoccli")) {
								aval.setTipdoccli(reader.nextString());
							} else if (nameAval.equals("doccli")) {
								aval.setDoccli(reader.nextString());
							} else {// unexpected value, skip it or generate error
								reader.skipValue();
							}
						}
//log.info("aval:" + aval );							
						venta.setAval(aval);
						reader.endObject();
					} else if (name.equals("bcredito")) {
						venta.setBcredito(reader.nextBoolean());
					} else if (name.equals("credito")) {
						reader.beginObject();
						while (reader.hasNext()) {
							String nameCredito = reader.nextName();
							if (nameCredito.equals("codcre")) {
								credito.setCodcre(reader.nextString());
							} else if (nameCredito.equals("feccre")) {
								credito.setFeccre(LocalDate.parse(reader.nextString()));
							} else if (nameCredito.equals("mtocre")) {
								credito.setMtocre(Double.parseDouble(reader.nextString()));
							} else {// unexpected value, skip it or generate error
								reader.skipValue();
							}
						}
//log.info("credito:" + credito );							
						venta.setCredito(credito);
						reader.endObject();
					} else if (name.equals("bdonacion")) {
						venta.setBdonacion(reader.nextBoolean());
					} else if (name.equals("donacion")) {
						reader.beginObject();
						while (reader.hasNext()) {
							String nameDonacion = reader.nextName();
//log.info("nameDinacion:" + nameDonacion );							
							if (nameDonacion.equals("coddon")) {
								donacion.setCoddon(reader.nextString());
							} else if (nameDonacion.equals("fecdon")) {
								donacion.setFecdon(LocalDate.parse(reader.nextString()));
							} else if (nameDonacion.equals("docref")) {
								donacion.setDocref(reader.nextString());
							} else if (nameDonacion.equals("numref")) {
								donacion.setNumref(reader.nextString());
							} else if (nameDonacion.equals("fecref")) {
								donacion.setFecref(LocalDate.parse(reader.nextString()));
							} else if (nameDonacion.equals("expediente")) {
								donacion.setExpediente(reader.nextString());
							} else if (nameDonacion.equals("mtodon")) {
								donacion.setMtodon(Double.parseDouble(reader.nextString()));
							} else {// unexpected value, skip it or generate error
								reader.skipValue();
							}
						}
//log.info("donacion:" + donacion );							
						venta.setDonacion(donacion);
						reader.endObject();
					} else if (name.equals("bsubvencion")) {
						venta.setBsubvencion(reader.nextBoolean());
					} else if (name.equals("subvenciones")) {
						reader.beginArray();
						List<Subvencion> subvenciones = new ArrayList<>();
						while (reader.hasNext()) {
							reader.beginObject();
							Subvencion subvencion = new Subvencion();
							while (reader.hasNext()) {
								String nameSubvencion = reader.nextName();
//log.info("namesubvencion:" + nameSubvencion );							
								if (nameSubvencion.equals("codsub")) {
									subvencion.setCodsub(reader.nextString());
								} else if (nameSubvencion.equals("fecsub")) {
									subvencion.setFecsub(LocalDate.parse(reader.nextString()));
								} else if (nameSubvencion.equals("codent")) {
									subvencion.setCodent(reader.nextString());
								} else if (nameSubvencion.equals("docref")) {
									subvencion.setDocref(reader.nextString());
								} else if (nameSubvencion.equals("numref")) {
									subvencion.setNumref(reader.nextString());
								} else if (nameSubvencion.equals("fecref")) {
									subvencion.setFecref(LocalDate.parse(reader.nextString()));
								} else if (nameSubvencion.equals("expediente")) {
									subvencion.setExpediente(reader.nextString());
								} else if (nameSubvencion.equals("mtosub")) {
									subvencion.setMtosub(Double.parseDouble(reader.nextString()));
								} else {// unexpected value, skip it or generate error
									reader.skipValue();
								}
							}
//log.info("subvencion:" + subvencion );							
							subvenciones.add(subvencion);
							reader.endObject();
						}
						venta.setSubvenciones(subvenciones);
						reader.endArray();
					} else if (name.equals("mtovta")) {
						venta.setMtovta(reader.nextDouble());
					} else if (name.equals("mtocan")) {
						venta.setMtocan(reader.nextDouble());
					} else if (name.equals("mtocre")) {
						venta.setMtocre(reader.nextDouble());
					} else if (name.equals("mtodon")) {
						venta.setMtodon(reader.nextDouble());
					} else if (name.equals("mtosub")) {
						venta.setMtosub(reader.nextDouble());
					} else if (name.equals("observ")) {
						venta.setObserv(reader.nextString());
					} else if (name.equals("codcem")) {
						venta.setCodcem(reader.nextString());
					} else if (name.equals("nomben")) {
						venta.setNomben(reader.nextString());
					} else if (name.equals("cuartel")) {
						venta.setCuartel(reader.nextString());
					} else if (name.equals("nicho")) {
						venta.setNicho(reader.nextString());
					} else {// unexpected value, skip it or generate error
						reader.skipValue();
					}
				}
//log.info("Venta:" + venta);
				service.registraTransaccion(venta);
				reader.endObject();
			}
			reader.endArray();
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ret = 1;
		return new ResponseEntity<Integer>(ret, HttpStatus.OK);
	}
	
	@GetMapping("/hateoas/{id}")
	public EntityModel<Venta> listarHateoasPorId(@PathVariable("id") String id) throws Exception {
		Venta obj = (Venta)service.listarPorId(id);

		if (obj == null) {
			throw new ModelNotFoundException("ID NO ENCONTRADO " + id);
		}

		EntityModel<Venta> recurso = EntityModel.of(obj);
		// localhost:8080/medicos/hateoas/1
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).listarHateoasPorId(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(this.getClass()).listarHateoasPorId(id));
		recurso.add(link1.withRel("Paciente-recurso1"));
		recurso.add(link2.withRel("Paciente-recurso2"));

		return recurso;
	}

	public void validate(Object obj) throws Exception {
		Venta venta = (Venta)obj;
		log.info("venta:" + venta);
		StringBuilder msg = new StringBuilder();

		if (venta.getFecvta()==null) {
            msg.append(" Fecha de venta no definida.");
		}
		
        if (venta.isBcredito()==false) {
        	if (venta.getMtocre()>0.0) {
                msg.append(" Error en monto de credito.");
        	}
        } else {
        	if (venta.getAval()==null) {
                msg.append(" No se ha registrado aval.");
        	}
        	if (venta.getCredito()==null) {
                msg.append(" No se ha definido el credito.");
        	} else {
       			if (venta.getCredito().getFeccre()==null) {
                    msg.append(" No se ha definido fecha del credito.");
       			}
       			if (venta.getCredito().getMtocre()<=0.0) {
                    msg.append(" Monto del Credito no definido o mal definido.");
       			}
       			double c1 = venta.getMtocre();
       			double c2 = venta.getCredito().getMtocre();
       			
       			if (c1!=c2) {
                    msg.append(" Monto del Credito no coinciden.");
       			}
        	}
        }
        
        if (venta.isBdonacion()==false) {
        	if (venta.getMtodon()>0.0) {
                msg.append(" Error en monto de donacion.");
        	}
        } else {
        	if (venta.getDonacion()==null) {
                msg.append(" No se ha definido la Donacion.");
        	} else {
       			if (venta.getDonacion().getFecdon()==null) {
                    msg.append(" No se ha definido fecha de la donacion.");
       			}
       			if (venta.getDonacion().getDocref()==null ||venta.getDonacion().getDocref().length()==0) {
                    msg.append(" Documento de referencia de la Donacion no definido.");
       			}
       			if (venta.getDonacion().getNumref()==null ||venta.getDonacion().getNumref().length()==0) {
                    msg.append(" Numero de referencia de la Donacion no definido.");
       			}
       			if (venta.getDonacion().getFecref()==null) {
                    msg.append(" No se ha definido fecha de referencia de donacion.");
       			}
       			if (venta.getDonacion().getExpediente()==null|| venta.getDonacion().getExpediente().length()==0) {
                    msg.append(" Expediente de la referencia de donacion no se ha definido.");
       			}
       			
       			double c1 = venta.getMtodon();
       			double c2 = venta.getDonacion().getMtodon();
       			if (c1!=c2) {
                    msg.append(" Monto de donacion no coinciden." + venta.getMtodon() + " - " + venta.getDonacion().getMtodon());
       			}
        	}
        }
		
        if (venta.isBsubvencion()==false) {
        	if (venta.getMtosub()>0.0) {
                msg.append(" Error en monto de subvencion.");
        	}
        } else {
        	if (venta.getSubvenciones()==null) {
                msg.append(" No se ha definido Subvencion(es).");
        	} else {
        		double c1=0.0, c2=0.0;
        		venta.getSubvenciones().forEach(reg -> {
        			if (reg.getFecsub()==null) {
        				msg.append(" No se ha definido fecha de la subvención.");
        			}
        			if (reg.getDocref()==null ||reg.getDocref().length()==0) {
        				msg.append(" Documento de referencia de la subvención no definido.");
        			}
        			if (reg.getNumref()==null ||reg.getNumref().length()==0) {
        				msg.append(" Numero de referencia de la subvencion no definido.");
        			}
        			if (reg.getFecref()==null) {
        				msg.append(" No se ha definido fecha de referencia de subvencion.");
        			}
        			if (reg.getExpediente()==null|| reg.getExpediente().length()==0) {
        				msg.append(" Expediente de la referencia de subvencion no se ha definido.");
        			}
        		});
    			c1 = venta.getMtosub();
        		c2 += venta.getSubvenciones().stream()
        				.mapToDouble(Subvencion::getMtosub)
        				.sum();
       			if (c1!=c2) {
                    msg.append(" Monto de subvencion no coinciden." + c1 + " - " + c2);
       			}
        	}
        }
        
        if (venta.getCodcem()==null || venta.getCodcem().length()==0) {
            msg.append("Codigo de Cementerio no definido.");
        }
        
        if (venta.getCodcp()==null || venta.getCodcp().length()==0) {
            msg.append("Codigo de Comprobante de pago no definido.");
        }
        
        if (venta.getCompag()==null || venta.getCompag().length()==0) {
            msg.append("Codigo de Cementerio no definido.");
        }
        
        if (venta.getConvta()==null || venta.getConvta().length()==0) {
            msg.append("Codigo de Cementerio no definido.");
        }
        
        if (venta.getCuartel()==null || venta.getCuartel().length()==0) {
            msg.append("Codigo de Cementerio no definido.");
        }
        
        if (venta.getMtocan()==null) {
            msg.append("Monto cancelado no definido.");
        }
        
       	double mtocan = venta.getMtocan();
       	double mtocre = venta.getMtocre();
       	double mtodon = venta.getMtodon();
       	double mtosub = venta.getMtosub();
       	double mtovta = venta.getMtovta();
//log.info("mtocan:" + mtocan + " mtocre:" + mtocre + " mtodon:" + mtodon + " mtosub:" + mtosub + " mtovta:" + mtovta);        	
       	if (mtovta != (mtocan+mtocre+mtodon+mtosub)) {
            msg.append("Error en montos asignados.");
       	}
        
		if (!msg.isEmpty()) {
			throw new Exception(msg.toString());
		}

	}
}
