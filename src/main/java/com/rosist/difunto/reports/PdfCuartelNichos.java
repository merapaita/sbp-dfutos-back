package com.rosist.difunto.reports;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.ColorConstants;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cuartel;
import com.rosist.difunto.modelSbp.Nicho_e;
import com.rosist.difunto.modelSbp.Nicho_n;

public class PdfCuartelNichos {

	private DriverManagerDataSource datasource;
	private Connection conexion;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql = "";

	String condicion = "";
	String reporte = "";
	boolean procesa = false;

	private PdfWriter pdfWriter = null;
	private PdfDocument pdfDocument;
	private Document document;
	private PdfFont fontContenido;
	private Table tReporte;
	private Cell cell;

	int pagina = 0;
	int linea = 0;

	static final Logger logger = LoggerFactory.getLogger(PdfServicio.class);

	public PdfCuartelNichos(Map<String, Object> parametros) {
		datasource = (DriverManagerDataSource) parametros.get("datasource");
		condicion = (String) parametros.get("condicion");
	}

	public byte[] creaReporte() throws Exception {

		String columnasn = "", columnase = "";
		for (int i = 1; i <= 200; i++)
			columnasn += " nn.col" + String.format("%1$03d", i) + ",";
		for (int i = 1; i <= 200; i++)
			columnase += " ne.col" + String.format("%1$03d", i) + " ecol" + String.format("%1$03d", i) + ",";
		columnase = columnase.substring(0, columnase.length() - 1);
		String sql = "SELECT cu.codcem, ce.nomcem, cu.codcuar, cu.nomcuar, cu.tipcuar, tc.descri destipcua, "
				+ "       cu.filas, cu.columnas, cu.orden, cu.estado, e.descri desest, nn.fila1, nn.fila2," + columnasn
				+ columnase + "  FROM cuartel cu LEFT JOIN cementerio ce ON cu.codcem=ce.codcem"
				+ "                  LEFT JOIN parmae tc     ON tc.tipo='TIPCUA' AND tc.codigo=cu.tipcuar"
				+ " 			     LEFT JOIN parmae e      ON  e.tipo='ESTCUA' AND  e.codigo=cu.estado"
				+ "                  LEFT JOIN nicho_n nn    ON nn.codcem=cu.codcem AND nn.codcuar=cu.codcuar"
				+ "                  LEFT JOIN nicho_e ne    ON ne.codcem=nn.codcem AND ne.codcuar=nn.codcuar AND ne.fila1=nn.fila1"
				+ " WHERE 1=1 " + condicion;

		logger.info("sql:" + sql);
		conexion = datasource.getConnection();
		pstmt = conexion.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

		rs = pstmt.executeQuery(sql);

		ByteArrayOutputStream docBytes = new ByteArrayOutputStream();
		pdfWriter = new PdfWriter(docBytes);

		pdfDocument = new PdfDocument(pdfWriter);
		document = new Document(pdfDocument, PageSize.A4); // , new PageSize(envelope)
		document.setMargins(75, 36, 75, 36);

//		EventoPagina evento = new EventoPagina(document);
//		pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, evento);

		plantilla();
		tReporte = new Table(new float[] { 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 });
		tReporte.setWidth(PageSize.A4.getWidth() - 50);
//		cabecera();

		List<Cuartel> cuarteles = new ArrayList<>();
		List<Nicho_n> nichosn = new ArrayList<>();
		List<Nicho_e> nichose = new ArrayList<>();
		if (rs.first()) {
			rs.beforeFirst();
			procesa = true;
			int filas = 0;
			int columnas = 0;
			int codcem = 0;
			int codcuar = 0;
			Cementerio cementerio = new Cementerio();
			cuarteles = new ArrayList<>();
			nichosn = new ArrayList<>();
			nichose = new ArrayList<>();
			System.out.println("empezando...");
			Cuartel cuartel = new Cuartel();

			while (rs.next()) {
				codcem = rs.getInt("codcem");
				codcuar = rs.getInt("codcuar");
				boolean lEncontrado = false;
				for (Cuartel registro : cuarteles) {
					if (registro.getCementerio().getCodcem() == codcem && registro.getCodcuar() == codcuar) {
						lEncontrado = true;
					}
				}
				if (lEncontrado == false) {
					nichosn = new ArrayList<>();
					nichose = new ArrayList<>();
					cementerio = new Cementerio();
					cementerio.setCodcem(rs.getInt("codcem"));
					cementerio.setNomcem(rs.getString("nomcem"));
					cuartel = new Cuartel();
					cuartel.setCementerio(cementerio);
					cuartel.setCodcuar(rs.getInt("codcuar"));
					cuartel.setNomcuar(rs.getString("nomcuar"));
					cuartel.setFilas(rs.getInt("filas"));
					cuartel.setColumnas(rs.getInt("columnas"));
					cuartel.setNichos_n(nichosn);
					cuartel.setNichos_e(nichose);
					cuarteles.add(cuartel);
				}
			}
			System.out.println("cuateles terminados:");
			rs.beforeFirst();
			while (rs.next()) {
				codcem = rs.getInt("codcem");
				codcuar = rs.getInt("codcuar");
				columnas = rs.getInt("columnas");
				System.out.print(codcem + " - " + codcuar + " - " + " - " + rs.getString("fila2") + columnas);
				for (Cuartel registro : cuarteles) {
					if (registro.getCementerio().getCodcem() == codcem && registro.getCodcuar() == codcuar) {
						System.out.print(" encontre...");
						Nicho_n nichon = new Nicho_n();
						nichon.setFila1(rs.getInt("fila1"));
						nichon.setFila2(rs.getString("fila2"));

						Nicho_e nichoe = new Nicho_e();
						nichoe.setFila1(rs.getInt("fila1"));
						nichoe.setFila2(rs.getString("fila2"));
						for (int l = 1; l <= columnas; l++) {
							String campon = "setCol" + String.format("%1$03d", l);
							String daton = "col" + String.format("%1$03d", l);
							setCampo(nichon, campon, rs.getInt(daton));

							String campoe = "setCol" + String.format("%1$03d", l);
							String datoe = "ecol" + String.format("%1$03d", l);
							setCampo(nichoe, campoe, rs.getString(datoe));
						}
						registro.getNichos_n().add(nichon);
						registro.getNichos_e().add(nichoe);
						System.out.println(" agregue!!!...");
					}
				}
			}
		}

//		Nicho_n nichon = new Nicho_n();
//		nichon.setFila1(rs.getInt("fila1"));
//		nichon.setFila2(rs.getString("fila2"));
//
//		Nicho_e nichoe = new Nicho_e();
//		nichoe.setFila1(rs.getInt("fila1"));
//		nichoe.setFila2(rs.getString("fila2"));
//		for (int l = 1; l <= columnas; l++) {
//			String campon = "setCol" + String.format("%1$03d", l);
//			String daton = "col" + String.format("%1$03d", l);
//			setCampo(nichon, campon, rs.getInt(daton));
//
//			String campoe = "setCol" + String.format("%1$03d", l);
//			String datoe = "col" + String.format("%1$03d", l);
//			setCampo(nichoe, campoe, rs.getString(datoe));
//		}
//		registro.getNichos_n().add(nichon);
//		registro.getNichos_e().add(nichoe);

//		nichosn = new ArrayList<>();
//		nichose = new ArrayList<>();
//
//		Nicho_n nichon = new Nicho_n();
//		nichon.setFila1(rs.getInt("fila1"));
//		nichon.setFila2(rs.getString("fila2"));
//
//		Nicho_e nichoe = new Nicho_e();
//		nichoe.setFila1(rs.getInt("fila1"));
//		nichoe.setFila2(rs.getString("fila2"));
//
//		for (int l = 1; l <= columnas; l++) {
//			String campon = "setCol" + String.format("%1$03d", l);
//			String daton = "col" + String.format("%1$03d", l);
//			setCampo(nichon, campon, rs.getInt(daton));
//
//			String campoe = "setCol" + String.format("%1$03d", l);
//			String datoe = "col" + String.format("%1$03d", l);
//			setCampo(nichoe, campoe, rs.getString(datoe));
//		}
//		nichosn.add(nichon);
//		nichose.add(nichoe);
//		cuartel.setNichos_n(nichosn);
//		cuartel.setNichos_e(nichose);

//		logger.info("finalice cuarteles:" + cuarteles);

//		if (procesa == true) {
//			for (Cuartel cuartel : cuarteles) {
//				System.out.print(cuartel.getCementerio().getCodcem() + " - " + cuartel.getCodcuar());
//				String dato = cuartel.getCementerio().getCodcem() + " - " + cuartel.getCodcuar();
//				cell = new Cell(1, 21).add(new Paragraph(dato).setTextAlignment(TextAlignment.LEFT))
//						.setFont(fontContenido).setFontSize(8);
//				tReporte.addCell(cell);
//				for (Nicho_n nom : cuartel.getNichos_n()) {
//					dato =nom.getFila1() + " - " + nom.getFila2() + " - ";
//					cell = new Cell(1, 21).add(new Paragraph(dato).setTextAlignment(TextAlignment.LEFT))
//							.setFont(fontContenido).setFontSize(8);
//					tReporte.addCell(cell);
//				}
//			}
//		}
		if (procesa == true) {
			int nEstDes, nEstOF, nEstOcup, nEstDesTrs;
//			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//			String ocupado = classloader.getResource("ocupado.png").getFile();
//			String desoc   = classloader.getResource("desoc.png").getFile();
//			String ocufut  = classloader.getResource("ocufut.png").getFile();
//			String dscxtrs = classloader.getResource("dscxtrs.png").getFile();
//			String error   = classloader.getResource("dscxtrs.png").getFile();

			for (Cuartel _cuartel : cuarteles) {
				nEstDes = 0;
				nEstOF = 0;
				nEstOcup = 0;
				nEstDesTrs = 0;
				String dato = "CEMENTERIO:" + _cuartel.getCementerio().getCodcem() + " - "
						+ _cuartel.getCementerio().getNomcem();
				cell = new Cell(1, 21).add(new Paragraph(dato).setTextAlignment(TextAlignment.LEFT))
						.setFont(fontContenido).setFontSize(8);
				tReporte.addCell(cell);

				dato = "CUARTEL:" + _cuartel.getCodcuar() + " - " + _cuartel.getNomcuar();
				cell = new Cell(1, 21).add(new Paragraph(dato).setTextAlignment(TextAlignment.LEFT))
						.setFont(fontContenido).setFontSize(8);
				tReporte.addCell(cell);

				int filas = _cuartel.getFilas();
				int columnas = _cuartel.getColumnas();
				int xc = 1;
				for (int i = xc; i <= columnas; i++) {
					int ii = i;
					for (int j = 0; j < filas; j++) {
						i = ii;
						if (j == 0) {
//							System.out.print("Titulos por Bloque");
							cell = new Cell().add(new Paragraph("").setTextAlignment(TextAlignment.LEFT))
									.setFont(fontContenido).setFontSize(8);
							tReporte.addCell(cell);
							for (int l = 1; l <= 20; l++) {
								if (i + l - 1 > columnas) {
									System.out.print("Exeso va vacio");
									cell = new Cell().add(new Paragraph("").setTextAlignment(TextAlignment.LEFT))
											.setFont(fontContenido).setFontSize(8);
									tReporte.addCell(cell);
								} else {
									System.out.println("El Titulo");
									String campo = "getCol" + String.format("%1$03d", i + l - 1);
									dato = String.valueOf((Integer) getCampo(_cuartel.getNichos_n().get(j), campo, 2));
									System.out.print(dato + " ");
									cell = new Cell().add(new Paragraph(dato).setTextAlignment(TextAlignment.LEFT))
											.setFont(fontContenido).setFontSize(8);
									tReporte.addCell(cell);
								}
							}
////**System.out.println("SS ");		                        
							dato = _cuartel.getNichos_n().get(j).getFila2();
							cell = new Cell().add(new Paragraph(dato).setTextAlignment(TextAlignment.LEFT))
									.setFont(fontContenido).setFontSize(8);
							tReporte.addCell(cell);
							for (int l = 1; l <= 20; l++) {
								if (i + l - 1 > columnas) {
//									System.out.print("");
									cell = new Cell().add(new Paragraph("").setTextAlignment(TextAlignment.LEFT))
											.setFont(fontContenido).setFontSize(8);
									tReporte.addCell(cell);
								} else {
									String campo = "getCol" + String.format("%1$03d", i);
									dato = String.valueOf((Integer) getCampo(_cuartel.getNichos_n().get(j), campo, 2));
									String dato1 = String
											.valueOf((String) getCampo(_cuartel.getNichos_e().get(j), campo, 1));
//									String xDato1 = "";
//									String cFile = "";
									if (dato1.equals("1")) {
////		                                    cFile = desoc;
//										xDato1 += "_";
										nEstDes += 1;
									} else if (dato1.equals("2")) {
////		                                    cFile = ocufut;
//										xDato1 += "OF";
										nEstOF += 1;
									} else if (dato1.equals("3")) {
////		                                    cFile = ocupado;
//										xDato1 += "XX";
										nEstOcup += 1;
									} else if (dato1.equals("4")) {
////		                                    cFile = dscxtrs;
//										xDato1 += "des";
										nEstDesTrs += 1;
									} else {
////		                                    cFile = error;
									}
////		                                Image img = Image.getInstance(cFile);
//									System.out.print(dato + "_" + dato1 + " ");
									cell = new Cell().add(new Paragraph(dato1) // img
											.setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
											.setFontSize(8);
									tReporte.addCell(cell);
								}
////								i++;
							}	// for filas de 1 al 20
//							System.out.println();
						} else {
							cell = new Cell().add(new Paragraph(_cuartel.getNichos_n().get(j).getFila2()) // img
									.setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido).setFontSize(8);
							tReporte.addCell(cell);
							for (int k = 1; k <= 20; k++) {
								if (i > columnas) {
//									// pinta vacio
////	                            System.out.print(" *");
									cell = new Cell().add(new Paragraph("") // img
											.setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
											.setFontSize(8);
									tReporte.addCell(cell);
								} else {
//									// pinta nicho
									String campo = "getCol" + String.format("%1$03d", i);
									dato = String.valueOf((Integer) getCampo(_cuartel.getNichos_n().get(j), campo, 2));
									String dato1 = String
											.valueOf((String) getCampo(_cuartel.getNichos_e().get(j), campo, 1));
////	                            System.out.print(" " + dato);
//									String xDato1 = "";
//									String cFile = "";
									if (dato1.equals("1")) {
////	                                cFile = "imagenes\\desoc.png";
////	                                    cFile = desoc;
////	                                    cFile = drive + "\\desarrollo\\imagenes\\desoc.png";
//										xDato1 += "_";
										nEstDes += 1;
									} else if (dato1.equals("2")) {
////	                                    cFile = ocufut;
////	                                    cFile = drive +"\\desarrollo\\imagenes\\ocufut.png";
//										xDato1 += "OF";
										nEstOF += 1;
									} else if (dato1.equals("3")) {
////	                                    cFile = ocupado;
////	                                    cFile = drive + "\\desarrollo\\imagenes\\ocupado.png";
//										xDato1 += "XX";
										nEstOcup += 1;
									} else if (dato1.equals("4")) {
////	                                    cFile = dscxtrs;
////	                                    cFile = drive + "\\desarrollo\\imagenes\\dscxtrs.png";
//										xDato1 += "des";
										nEstDesTrs += 1;
									} else {
////	                                    cFile = error;
									}
////	                                logger.info("cFile.-> " + cFile + " - dato1.-> " + dato1);
//////	                                if (!cFile.isEmpty()) {
////	                                    Image img = Image.getInstance(cFile);
////	                                    cell = new PdfPCell(img);
//////	                                } 
////	                            cell = new PdfPCell(new Phrase(dato+"_"+xDato1, fontContenido));
									cell = new Cell().add(new Paragraph(dato1) // img
											.setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
											.setFontSize(8);
									tReporte.addCell(cell);
////	                                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
////	                                bordes(cell, BaseColor.WHITE, BaseColor.WHITE, BaseColor.WHITE, BaseColor.WHITE);
////	                                tNicho.addCell(cell);
								}
								i++;
							}
						}
						if (j < filas) {
							i = ii;
						}
					} // fir for primer bloque
//					cell = new Cell(1,21).add(new Paragraph(" ") // img
//							.setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
//							.setFontSize(8);
//					tReporte.addCell(cell);
					if (i < columnas) {
						i = i + 20 - 1;
					}
				}
//				System.out.println();
// ???		            
//				} // fin del for
				String cLeyenda = "Ocupados:" + nEstOcup + " Desocupados:" + nEstDes + " Ocu. Futura:" + nEstOF
						+ " Des. x Trasl.:" + nEstDesTrs;
				cell = new Cell(1, 21).add(new Paragraph(cLeyenda) // img
						.setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido).setFontSize(8);
				tReporte.addCell(cell);
				
//				Esto reemplazarlo por una nueva tabla;
//				cell = new Cell(1,21).add(new Paragraph(" ") // img
//						.setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
//						.setFontSize(8);
//				tReporte.addCell(cell);
//				cell = new Cell(1,21).add(new Paragraph(" ") // img
//						.setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
//						.setFontSize(8);
//				tReporte.addCell(cell);
				
//				if (_cuartel.getCodcuar() > 3) {
//					break;
//				}
////				document.add(tReporte);
////				document.close();
////		        return docBytes.toByteArray();
			}
		}

		document.add(tReporte);
		document.close();
		return docBytes.toByteArray();
	}

	private void detalle(ResultSet rs) throws SQLException {
		String dato = rs.getInt("codcem") + " - " + rs.getInt("codcuar");
		cell = new Cell().add(new Paragraph(dato).setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tReporte.addCell(cell);

//        cell = new Cell()
//        		.add(new Paragraph(String.valueOf(rs.getInt("codcuar")))
//        		.setTextAlignment(TextAlignment.LEFT))
//        		.setFont(fontContenido)
//        		.setFontSize(8);
//        tReporte.addCell(cell);

	}

	private void cabecera() throws Exception { // Document doc
		cell = null;
		/**/
//        cell = new Cell(1,2)
//        		.add(new Paragraph("SOCIEDAD DE BENEFICENCIA PUBLICA DE PIURA")
//        				.setTextAlignment(TextAlignment.LEFT))
//        		.setFont(fontContenido)
//        		.setFontSize(8);
//
//        bordes(cell, ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE);
//        tCabecera.addCell(cell);
//        
//        String cPagina = "Pag.: " + String.format("%1$010d", ++pagina);
//        cell = new Cell()
//        		.add(new Paragraph(cPagina)
//        				.setTextAlignment(TextAlignment.LEFT))
//        		.setFont(fontContenido)
//        		.setFontSize(8);
//        bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE);
//        tCabecera.addCell(cell);
		/**/
//        cell = new Cell(1,3)
//        		.add(new Paragraph("POLICLINICO EL BUEN SAMARITANO")
//        				.setTextAlignment(TextAlignment.LEFT))
//        		.setFont(fontContenido)
//        		.setFontSize(8);
//        bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE);
//        tCabecera.addCell(cell);
		/**/
		cell = new Cell(1, 5)
				.add(new Paragraph("LISTADO DE CUARTELES CON NICHOS").setTextAlignment(TextAlignment.CENTER))
				.setFont(fontContenido).setFontSize(8);
		bordes(cell, ColorConstants.WHITE, ColorConstants.WHITE, ColorConstants.WHITE, ColorConstants.WHITE);
		tReporte.addHeaderCell(cell);

		cell = new Cell(1, 11).add(new Paragraph(" ").setTextAlignment(TextAlignment.CENTER)).setFont(fontContenido)
				.setFontSize(8);
		bordes(cell, ColorConstants.WHITE, ColorConstants.WHITE, ColorConstants.WHITE, ColorConstants.WHITE);
		tReporte.addHeaderCell(cell);
		linea = 5;
	}

	private void plantilla() throws IOException {
		fontContenido = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
	}

	private void bordes(Cell cell, Color arriba, Color abajo, Color izquierda, Color derecha) {
//      cell.setUseVariableBorders(true);
		cell.setBorderTop(new SolidBorder(arriba, 1));
		cell.setBorderBottom(new SolidBorder(abajo, 1));
		cell.setBorderLeft(new SolidBorder(izquierda, 1));
		cell.setBorderRight(new SolidBorder(derecha, 1));
	}

	private void setCampo(Object objeto, String campo, String dato)
			throws IllegalArgumentException, InvocationTargetException {
		Class clase;
		Method metodoSet;
		Class[] clasesParamSet;
		Object[] paramSet;
		try {
			// Cargamos la clase
			clase = objeto.getClass();
			// clase = Class.forName("com.spring.bean.Ejecucion");
			// Instanciamos un objeto de la clase
			try {
				// objeto = (Object)ejecucion;
				try {
					// Accedemos al metodo setEjeMes, con un parametro (Double) pra modificar
					clasesParamSet = new Class[1];
					clasesParamSet[0] = Class.forName("java.lang.String");
					metodoSet = clase.getMethod(campo, clasesParamSet);
					paramSet = new Object[1];
					////////////////////
					paramSet[0] = dato;
					///////////////////
					metodoSet.invoke(objeto, paramSet);
				} catch (NoSuchMethodException e) {
					System.out.println("Error al acceder al metodo. " + e);
				} catch (SecurityException e) {
					System.out.println("Error al acceder al metodo. " + e);
				} catch (InvocationTargetException e) {
					System.out.println("Error al ejecutar el metodo. " + e);
				}
				// } catch (InstantiationException e) {
				// System.out.println("Error al instanciar el objeto. " + e);
			} catch (IllegalAccessException e) {
				System.out.println("Error al instanciar el objeto. " + e);
			}
		} catch (ClassNotFoundException e) {
			System.out.println("No se ha encontrado la clase. " + e);
		}
	}

	private void setCampo(Object objeto, String campo, Integer dato) {
		Class clase;
		Method metodoSet;
		Class[] clasesParamSet;
		Object[] paramSet;
		try {
			// Cargamos la clase
			clase = objeto.getClass();
			// clase = Class.forName("com.spring.bean.Ejecucion");
			// Instanciamos un objeto de la clase
			try {
				// objeto = (Object)ejecucion;
				try {
					// Accedemos al metodo setEjeMes, con un parametro (Double) pra modificar
					clasesParamSet = new Class[1];
					clasesParamSet[0] = Class.forName("java.lang.Integer");
					metodoSet = clase.getMethod(campo, clasesParamSet);
					paramSet = new Object[1];
					////////////////////
					paramSet[0] = dato;
					///////////////////
					metodoSet.invoke(objeto, paramSet);
				} catch (NoSuchMethodException e) {
					System.out.println("Error al acceder al metodo. " + e);
				} catch (SecurityException e) {
					System.out.println("Error al acceder al metodo. " + e);
				} catch (InvocationTargetException e) {
					System.out.println("Error al ejecutar el metodo. " + e);
				}
				// } catch (InstantiationException e) {
				// System.out.println("Error al instanciar el objeto. " + e);
			} catch (IllegalAccessException e) {
				System.out.println("Error al instanciar el objeto. " + e);
			}
		} catch (ClassNotFoundException e) {
			System.out.println("No se ha encontrado la clase. " + e);
		}
	}

	private String getCampo(Object objeto, String campo) {
		Class clase;
		Method metodoGet;
		String dato = "";
		// try {
		// Cargamos la clase
		clase = objeto.getClass();
		// clase = Class.forName("com.spring.bean.Ejecucion");
		// Instanciamos un objeto de la clase
		try {
			// objeto = (Object)ejecucion;
			try {
				// Accedemos al metodo getEjeMes, sin parametros
				metodoGet = clase.getMethod(campo, null);
				dato = (String) metodoGet.invoke(objeto, null);
			} catch (NoSuchMethodException e) {
				System.out.println("Error al acceder al metodo. " + e);
			} catch (SecurityException e) {
				System.out.println("Error al acceder al metodo. " + e);
			} catch (InvocationTargetException e) {
				System.out.println("Error al ejecutar el metodo. " + e);
			}
			// } catch (InstantiationException e) {
			// System.out.println("Error al instanciar el objeto. " + e);
		} catch (IllegalAccessException e) {
			System.out.println("Error al instanciar el objeto. " + e);
		}
		// } catch (ClassNotFoundException e) {
		// System.out.println("No se ha encontrado la clase. " + e);
		// }
		return dato;
	}

	private Object getCampo(Object objeto, String campo, int tipo) {
		Class clase;
		Method metodoGet;
		Object dato = null;
		// Cargamos la clase
		clase = objeto.getClass();
		// Instanciamos un objeto de la clase
		try {
			// objeto = (Object)ejecucion;
			try {
				// Accedemos al metodo getEjeMes, sin parametros
				metodoGet = clase.getMethod(campo, null);
				switch (tipo) {
				case 1:
					dato = (String) metodoGet.invoke(objeto, null);
					break;
				case 2:
					dato = (Integer) metodoGet.invoke(objeto, null);
					break;
				case 3:
					dato = (Double) metodoGet.invoke(objeto, null);
					break;
				}
			} catch (NoSuchMethodException e) {
				System.out.println("Error al acceder al metodo. " + e);
			} catch (SecurityException e) {
				System.out.println("Error al acceder al metodo. " + e);
			} catch (InvocationTargetException e) {
				System.out.println("Error al ejecutar el metodo. " + e);
			}
		} catch (IllegalAccessException e) {
			System.out.println("Error al instanciar el objeto. " + e);
		}
		return dato;
	}

}
