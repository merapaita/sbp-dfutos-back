package com.rosist.difunto;

import static org.junit.jupiter.api.Assertions.assertTrue;

//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
//
//import com.google.gson.stream.JsonReader;
//import com.rosist.difunto.venta.model.Cliente;
//import com.rosist.difunto.venta.model.Credito;
//import com.rosist.difunto.venta.model.Donacion;
//import com.rosist.difunto.venta.model.Subvencion;
//import com.rosist.difunto.venta.model.Venta;
//import com.rosist.difunto.venta.service.IVentaService;

@SpringBootTest
class SbpDifuntos17ApplicationTests {
	
	@Test
	void contextLoads() {
		assertTrue(true);
	}

//	@Autowired
//	private IVentaService service;
	
//	private static final Logger log = LoggerFactory.getLogger(SbpDifuntos17ApplicationTests.class);

//	@Test
//	void migraVentas() {
//		JsonReader reader;
//		try {
//			reader = new JsonReader(
//					new FileReader("c:\\trabajo\\desarrollo\\documentos\\servisepf\\mig\\MigContratoProbas.json"));
//			reader.beginArray();
//			while (reader.hasNext()) {
//				reader.beginObject();
//				Venta venta = new Venta();
//				Cliente cliente = new Cliente();
//				Cliente aval = new Cliente();
//				Credito credito = new Credito();
//				Donacion donacion = new Donacion();
//				while (reader.hasNext()) {
//					String name = reader.nextName();
////logger.info("name:" + name);					
//					if (name.equals("codvta")) {
//						venta.setCodvta(reader.nextString());
//					} else if (name.equals("fecvta")) {
//						venta.setFecvta(LocalDate.parse(reader.nextString()));
//					} else if (name.equals("tipvta")) {
//						venta.setTipvta(reader.nextString());
//					} else if (name.equals("convta")) {
//						venta.setConvta(reader.nextString());
//					} else if (name.equals("compag")) {
//						venta.setCompag(reader.nextString());
//					} else if (name.equals("serie")) {
//						venta.setSerie(reader.nextString());
//					} else if (name.equals("codcp")) {
//						venta.setCodcp(reader.nextString());
//					} else if (name.equals("feccp")) {
//						venta.setFeccp(LocalDate.parse(reader.nextString()));
//					} else if (name.equals("cliente")) {
//						reader.beginObject();
//						while (reader.hasNext()) {
//							String nameCliente = reader.nextName();
//							if (nameCliente.equals("codcli")) {
//								cliente.setCodcli(reader.nextInt());
//							} else if (nameCliente.equals("nomcli")) {
//								cliente.setNomcli(reader.nextString());
//							} else if (nameCliente.equals("dircli")) {
//								cliente.setDircli(reader.nextString());
//							} else if (nameCliente.equals("tipdoccli")) {
//								cliente.setTipdoccli(reader.nextString());
//							} else if (nameCliente.equals("doccli")) {
//								cliente.setDoccli(reader.nextString());
//							} else {// unexpected value, skip it or generate error
//								reader.skipValue();
//							}
//						}
//						venta.setCliente(cliente);
//						reader.endObject();
//					} else if (name.equals("aval")) {
//						reader.beginObject();
//						while (reader.hasNext()) {
//							String nameAval = reader.nextName();
//							if (nameAval.equals("codcli")) {
//								aval.setCodcli(reader.nextInt());
//							} else if (nameAval.equals("nomcli")) {
//								aval.setNomcli(reader.nextString());
//							} else if (nameAval.equals("dircli")) {
//								aval.setDircli(reader.nextString());
//							} else if (nameAval.equals("tipdoccli")) {
//								aval.setTipdoccli(reader.nextString());
//							} else if (nameAval.equals("doccli")) {
//								aval.setDoccli(reader.nextString());
//							} else {// unexpected value, skip it or generate error
//								reader.skipValue();
//							}
//						}
//						venta.setAval(aval);
//						reader.endObject();
//					} else if (name.equals("bcredito")) {
//						venta.setBcredito(Boolean.parseBoolean(reader.nextString()));
//					} else if (name.equals("credito")) {
//						reader.beginObject();
//						while (reader.hasNext()) {
//							String nameCredito = reader.nextName();
//							if (nameCredito.equals("codcre")) {
//								credito.setCodcre(reader.nextString());
//							} else if (nameCredito.equals("feccre")) {
//								credito.setFeccre(LocalDate.parse(reader.nextString()));
//							} else if (nameCredito.equals("mtocre")) {
//								credito.setMtocre(Double.parseDouble(reader.nextString()));
//							} else {// unexpected value, skip it or generate error
//								reader.skipValue();
//							}
//						}
//						venta.setCredito(credito);
//						reader.endObject();
//					} else if (name.equals("bdonacion")) {
//						venta.setBdonacion(Boolean.parseBoolean(reader.nextString()));
//					} else if (name.equals("donacion")) {
//						reader.beginObject();
//						while (reader.hasNext()) {
//							String nameDonacion = reader.nextName();
//							if (nameDonacion.equals("coddon")) {
//								donacion.setCoddon(reader.nextString());
//							} else if (nameDonacion.equals("fecdon")) {
//								donacion.setFecdon(LocalDate.parse(reader.nextString()));
//							} else if (nameDonacion.equals("docref")) {
//								donacion.setDocref(reader.nextString());
//							} else if (nameDonacion.equals("numref")) {
//								donacion.setNumref(reader.nextString());
//							} else if (nameDonacion.equals("fecref")) {
//								donacion.setFecref(LocalDate.parse(reader.nextString()));
//							} else if (nameDonacion.equals("expediente")) {
//								donacion.setExpediente(reader.nextString());
//							} else if (nameDonacion.equals("mtodon")) {
//								donacion.setMtodon(Double.parseDouble(reader.nextString()));
//							} else {// unexpected value, skip it or generate error
//								reader.skipValue();
//							}
//						}
//						venta.setDonacion(donacion);
//						reader.endObject();
//					} else if (name.equals("bsubvencion")) {
//						venta.setBdonacion(Boolean.parseBoolean(reader.nextString()));
//					} else if (name.equals("subvencion")) {
//						reader.beginArray();
//						List<Subvencion> subvenciones = new ArrayList<>();
//						while (reader.hasNext()) {
//							reader.beginObject();
//							Subvencion subvencion = new Subvencion();
//							while (reader.hasNext()) {
//								String nameSubvencion = reader.nextName();
//								if (nameSubvencion.equals("codsub")) {
//									subvencion.setCodsub(reader.nextString());
//								} else if (nameSubvencion.equals("fecsub")) {
//									subvencion.setFecsub(LocalDate.parse(reader.nextString()));
//								} else if (nameSubvencion.equals("codent")) {
//									subvencion.setCodent(reader.nextString());
//								} else if (nameSubvencion.equals("docref")) {
//									subvencion.setDocref(reader.nextString());
//								} else if (nameSubvencion.equals("numref")) {
//									subvencion.setNumref(reader.nextString());
//								} else if (nameSubvencion.equals("fecref")) {
//									subvencion.setFecref(LocalDate.parse(reader.nextString()));
//								} else if (nameSubvencion.equals("expediente")) {
//									subvencion.setExpediente(reader.nextString());
//								} else if (nameSubvencion.equals("mtosub")) {
//									subvencion.setMtosub(Double.parseDouble(reader.nextString()));
//								} else {// unexpected value, skip it or generate error
//									reader.skipValue();
//								}
//							}
//							subvenciones.add(subvencion);
//							reader.endObject();
//						}
//						venta.setSubvenciones(subvenciones);
//						reader.endArray();
//					} else {// unexpected value, skip it or generate error
//						reader.skipValue();
//					}
//				}
//				service.registraTransaccion(venta);
//				reader.endObject();
//			}
//			reader.endArray();
//			reader.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		assertTrue(true);
//	}
}