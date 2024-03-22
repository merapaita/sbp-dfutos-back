package com.rosist.difunto.reports;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.property.TextAlignment;
import com.rosist.difunto.modelSbp.Empresa;
import com.rosist.difunto.modelSbp.Sucursal;

public class PdfOcufut {

	private DriverManagerDataSource datasource;
	private Connection conexion;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql = "";

	String condicion = "";
	String order = "";
	String reporte = "";
	boolean lProcesa = false;

	private PdfWriter pdfWriter = null;
	private PdfDocument pdfDocument;
	private Document document;
	private PdfFont fontContenido;
//	private Table tReporte;
	private Table tCabCuartel;
	private Table tDetalle;
	private Table tPieCementerio;
	private Table tPieCuartel;
	private Cell cell;
	private Sucursal sucursal;
	private Empresa empresa;
	private int nOcupanteCuartel;
	private int nOcupanteCementerio;

	static final Logger logger = LoggerFactory.getLogger(PdfMausoleo.class);

	public PdfOcufut(Map<String, Object> parametros) {
		datasource = (DriverManagerDataSource) parametros.get("datasource");
		condicion = (String) parametros.get("condicion");
		order = (String) parametros.get("order");
		sucursal = (Sucursal) parametros.get("sucursal");
		empresa = (Empresa) parametros.get("empresa");
	}

	public byte[] creaReporte() throws Exception {

		sql = "SELECT of.codocu, CONCAT(TRIM(of.apepat),' ',TRIM(of.apemat),', ',TRIM(of.nombres)) nomocu, of.apepat, of.apemat, of.nombres, of.edad_a, of.edad_m, of.edad_d, "
				+ "          of.sexo, cl.nomcli, of.codcem, cem.nomcem, cem.local, of.codcuar, cu.nomcuar, cu.tipcuar, "
				+ "          cu.filas, cu.columnas, cu.estado estcua, of.codnic, of.fila1, of.fila2, of.columna1, "
				+ "          of.columna2, of.estado, e.descri desestado, of.coddif, d.fecfall, d.fecsep, d.estado estdif, "
				+ "          of.codtrn, of.coddif, of.estvta, ev.descri desestvta, of.tipdoccli, of.doccli, of.nomcli, cl.dircli"
				+ "     FROM ocufut of LEFT JOIN clientesunat cl on  cl.tipdoc=of.tipdoccli and cl.doccli=of.doccli"
				+ "                    LEFT JOIN cementerio cem ON of.codcem=cem.codcem"
				+ "                    LEFT JOIN parmae e  ON of.estado=e.codigo AND e.tipo='ESTOCF'"
				+ "                    LEFT JOIN parmae ev ON of.estado=ev.codigo AND ev.tipo='ESTVTA'"
				+ "                    LEFT JOIN cuartel cu ON of.codcem=cu.codcem AND of.codcuar=cu.codcuar"
				+ "                    LEFT JOIN difunto d ON of.codcem=d.codcem AND of.coddif=d.coddif"
				+ "    where 1=1 " + condicion + "    order by codcem,codcuar,columna1,fila1";

System.out.println("sql;" + sql);		
		conexion = datasource.getConnection();
		pstmt = conexion.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		rs = pstmt.executeQuery();

		ByteArrayOutputStream docBytes = new ByteArrayOutputStream();

		pdfWriter = new PdfWriter(docBytes);
		pdfDocument = new PdfDocument(pdfWriter);
		document = new Document(pdfDocument, PageSize.A4); // , new PageSize(envelope)
		document.setMargins(75, 36, 75, 36);

		EventoPagina evento = new EventoPagina(document);
		pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, evento);

		plantilla();
		cabecera();

		if (rs.first()) {
			rs.beforeFirst();
			int codcem = 0;
			int codcuar = 0;
			while (rs.next()) {
				if ((!(codcem == rs.getInt("codcem")))) { // || (linea==3)
					cabCementerio(rs);
				}
				if ((!(codcem == rs.getInt("codcem") && codcuar == rs.getInt("codcuar")))) { // || (linea==3)
					cabCuartel(rs);
				}
				detalle(rs);
				codcem = rs.getInt("codcem");
				codcuar = rs.getInt("codcuar");
				rs.next();
				if (rs.isAfterLast()) {
					rs.last();
				} else if (rs.isLast()) {
					rs.previous();
				} else if (!(codcem == rs.getInt("codcem") && codcuar == rs.getInt("codcuar"))) {
					rs.previous();
					pieCuartel(rs);
//					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
				} else if (!(codcem == rs.getInt("codcem"))) {
					rs.previous();
					pieCementerio(rs);
				} else {
					rs.previous();
				}
			}
		}
//		document.add(tReporte);
		document.close();
		return docBytes.toByteArray();

	}

	private void pieCementerio(ResultSet rs2) throws SQLException {
		tPieCementerio = new Table(new float[]{ 40, 200, 40, 40, 110, 60, 30});
		tPieCementerio.setWidth(PageSize.A4.getWidth()-50);
        String dato = "Total de Ocupantes en Cementerio : " + rs2.getString("nomcem") + " " + nOcupanteCementerio;
        cell = new Cell(1,7)
        		.add(new Paragraph(dato)
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tPieCementerio.addCell(cell);
		document.add(tPieCementerio);
        nOcupanteCementerio=0;
	}

	private void pieCuartel(ResultSet rs2) throws SQLException {
		String dato = "Total de Ocupaciones en cuartel : " + rs2.getString("nomcuar") + " " + nOcupanteCuartel;
		cell = new Cell(1, 7).add(new Paragraph(dato).setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addCell(cell);
		document.add(tDetalle);
		nOcupanteCuartel = 0;
	}

	private void detalle(ResultSet rs2) throws SQLException {
		cell = new Cell().add(new Paragraph(String.valueOf(rs2.getInt("codocu"))).setTextAlignment(TextAlignment.LEFT))
				.setFont(fontContenido).setFontSize(8);
		tDetalle.addCell(cell);

		cell = new Cell().add(new Paragraph(rs2.getString("nomocu")).setTextAlignment(TextAlignment.LEFT))
				.setFont(fontContenido).setFontSize(8);
		tDetalle.addCell(cell);

		String edad = rs.getString("edad_a") + "a " + rs.getString("edad_m") + "m " + rs.getString("edad_d") + "d";
		cell = new Cell().add(new Paragraph(edad).setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addCell(cell);

		cell = new Cell().add(new Paragraph(rs2.getString("sexo")).setTextAlignment(TextAlignment.LEFT))
				.setFont(fontContenido).setFontSize(8);
		tDetalle.addCell(cell);

		String cliente = (rs2.getString("nomcli")!=null?rs2.getString("nomcli"):"");
		cell = new Cell().add(new Paragraph(cliente).setTextAlignment(TextAlignment.LEFT))
				.setFont(fontContenido).setFontSize(8);
		tDetalle.addCell(cell);

		String nicho = rs.getString("fila2") + "-" + rs.getString("columna2");
		cell = new Cell().add(new Paragraph(nicho).setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addCell(cell);

		cell = new Cell().add(new Paragraph(rs2.getString("desEstado")).setTextAlignment(TextAlignment.LEFT))
				.setFont(fontContenido).setFontSize(8);
		tDetalle.addCell(cell);
		nOcupanteCuartel++;
		nOcupanteCementerio++;
	}

	private void cabCuartel(ResultSet rs2) throws SQLException {
		tDetalle = new Table(new float[] { 40, 200, 40, 40, 110, 60, 30 });
		tDetalle.setWidth(PageSize.A4.getWidth() - 50);

		cell = new Cell(1, 7)
				.add(new Paragraph("LISTADO DE OCUPACIONES FUTURAS").setTextAlignment(TextAlignment.CENTER))
				.setFont(fontContenido).setFontSize(8);
//        bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE);
		tDetalle.addHeaderCell(cell);

		String dato = "CEMENTERIO:" + rs2.getInt("codcem") + " - " + rs2.getString("nomcem");
		cell = new Cell(1, 7).add(new Paragraph(dato).setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addHeaderCell(cell);

		dato = "CUARTEL:" + rs2.getInt("codcuar") + " - " + rs2.getString("nomcuar");
//    	System.out.println(dato);    	
		cell = new Cell(1, 7).add(new Paragraph(dato).setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addHeaderCell(cell);

		cell = new Cell().add(new Paragraph("ID").setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addHeaderCell(cell);

		cell = new Cell().add(new Paragraph("OCUPANTE").setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addHeaderCell(cell);

		cell = new Cell().add(new Paragraph("EDAD").setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addHeaderCell(cell);

		cell = new Cell().add(new Paragraph("SEXO").setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addHeaderCell(cell);

		cell = new Cell().add(new Paragraph("CLIENTE").setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addHeaderCell(cell);

		cell = new Cell().add(new Paragraph("NICHO").setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addHeaderCell(cell);

		cell = new Cell().add(new Paragraph("ESTADO").setTextAlignment(TextAlignment.LEFT)).setFont(fontContenido)
				.setFontSize(8);
		tDetalle.addHeaderCell(cell);

	}

	private void cabCementerio(ResultSet rs2) throws SQLException {
		String dato = "CEMENTERIO:" + rs2.getInt("codcem") + " - " + rs2.getString("nomcem");
//System.out.println(dato);    	
//        cell = new Cell(1,9)
//        		.add(new Paragraph(dato)
//        		.setTextAlignment(TextAlignment.LEFT))
//        		.setFont(fontContenido)
//        		.setFontSize(8);
//        tReporte.addHeaderCell(cell);
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
//        cell = new Cell(1, 9)
//        		.add(new Paragraph("LISTADO DE DIFUNTOS")
//        				.setTextAlignment(TextAlignment.CENTER))
//        		.setFont(fontContenido)
//        		.setFontSize(8);
////        bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE);
//        tReporte.addHeaderCell(cell);

//        cell = new Cell(1, 9)
//        		.add(new Paragraph(" ")
//        				.setTextAlignment(TextAlignment.CENTER))
//        		.setFont(fontContenido)
//        		.setFontSize(8);
////        bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE);
//        tReporte.addHeaderCell(cell);
//        linea = 5;
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

}
