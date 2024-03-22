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
import com.itextpdf.kernel.color.ColorConstants;
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

public class PdfDifuntos {

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
	private Table tDetalle;
	private Table tPieCementerio;
	private Cell cell;
	private Sucursal sucursal;
	private Empresa empresa;
    private int nDifuntoCuartel;
    private int nDifuntoCementerio;
	
	int pagina = 0;
	int linea = 0;

	static final Logger logger = LoggerFactory.getLogger(PdfMausoleo.class);
	
	public PdfDifuntos(Map<String,Object> parametros) {
		datasource = (DriverManagerDataSource) parametros.get("datasource");
		condicion = (String) parametros.get("condicion");
		order = (String) parametros.get("order");
		sucursal   =   (Sucursal)parametros.get("sucursal");
		empresa   =   (Empresa)parametros.get("empresa");
	}
	
	public byte[] creaReporte() throws Exception {
        sql = "select d.codcem, cem.nomcem, cem.local, d.codcuar, cu.nomcuar, cu.tipcuar, p.descri destipcuar, cu.filas, cu.columnas,"
                + "              d.codmau, m.tipomau, p3.descri destipmau, m.lotizado, m.nomlote, m.familia, m.ubicacion,"
                + "              d.tipent, p2.descri destipent, d.coddif, date_format(d.fecfall,'%d/%m/%Y') fecfall, date_format(d.fecsep,'%d/%m/%Y') fecsep, "
                + "              d.apepat, d.apemat, d.nombres, CONCAT(TRIM(d.apepat),' ',TRIM(d.apemat),', ',TRIM(d.nombres)) nomdif, "
                + "              d.edad_a, d.edad_m, d.edad_d, d.sexo, d.reservado, d.codocu, d.codnic, d.fila1, d.fila2, d.columna1, d.columna2, "
                + "              d.estado, ed.descri desestado, d.estvta, ev.descri desestvta, d.observ "
                + "   from difunto d left join cementerio cem on d.codcem =cem.codcem"
                + "                  left join cuartel    cu  on d.codcem=cu.codcem and d.codcuar=cu.codcuar"
                + "                  left join mausoleo   m   on d.codmau=m.codmau"
                + "                  left join parmae p  on p.tipo='TIPCUA'  and cu.tipcuar=p.codigo"
                + "                  left join parmae p2 on p2.tipo='TIPENT' and d.tipent=p2.codigo"
                + "                  left join parmae p3 on p3.tipo='TIPMAU' and m.tipomau=p3.codigo"
                + "                  left join parmae ed on ed.tipo='ESTDIF' and d.estado=ed.codigo"
                + "                  left join parmae ev on ev.tipo='ESTVTA' and d.estvta=ev.codigo"
                + "  where 1=1 " + condicion
                + "  order by codcem,codcuar,columna1,fila1";
		
//System.out.println("sql:" + sql);        
		conexion = datasource.getConnection();
        pstmt = conexion.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
      		  ResultSet.CONCUR_UPDATABLE);
		rs = pstmt.executeQuery ();
		
        ByteArrayOutputStream docBytes = new ByteArrayOutputStream();
        
        pdfWriter = new PdfWriter(docBytes);
		pdfDocument = new PdfDocument(pdfWriter);
		document = new Document(pdfDocument, PageSize.A4); // , new PageSize(envelope)
        document.setMargins(75, 36, 75, 36);
        
        EventoPagina evento = new EventoPagina(document);
        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, evento);
		
        plantilla();
//		tReporte = new Table(new float[]{40, 40, 40, 200, 30, 60, 30, 40, 40});
//		tReporte.setWidth(PageSize.A4.getWidth()-50);
		cabecera();
        
		if (rs.first()) {
			rs.beforeFirst();
            int codcem = 0;
            int codcuar = 0;
            while (rs.next()) {
				if (  (!(codcem == rs.getInt("codcem"))) ){     // || (linea==3)
					cabCementerio(rs);
				}
				if (  (!(codcem == rs.getInt("codcem") && codcuar == rs.getInt("codcuar"))) ){     // || (linea==3)
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
				} else if (!(codcem == rs.getInt("codcem") && codcuar == rs.getInt("codcuar")	)) {
					rs.previous();
					pieCuartel(rs);
					document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
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
		tPieCementerio = new Table(new float[]{40, 40, 40, 200, 30, 60, 30, 40, 40});
		tPieCementerio.setWidth(PageSize.A4.getWidth()-50);
        String dato = "Total de Difuntos en Cementerio : " + rs2.getString("nomcem") + " " + nDifuntoCementerio;
//        System.out.println(dato);    	
        cell = new Cell(1,9)
        		.add(new Paragraph(dato)
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tPieCementerio.addCell(cell);
		document.add(tPieCementerio);
        nDifuntoCementerio=0;
        
	}

	private void pieCuartel(ResultSet rs2) throws SQLException {
//		tPieCuartel = new Table(new float[]{40, 40, 40, 200, 30, 60, 30, 40, 40});
//		tPieCuartel.setWidth(PageSize.A4.getWidth()-50);
        String dato = "Total de Difuntos en cuartel : " + rs2.getString("nomcuar") + " " + nDifuntoCuartel;
//        System.out.println(dato);    	
        cell = new Cell(1,9)
        		.add(new Paragraph(dato)
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addCell(cell);
		document.add(tDetalle);
        nDifuntoCuartel = 0;
        
	}

	private void detalle(ResultSet rs2) throws SQLException {
		
//		tDetalle = new Table(new float[]{40, 40, 40, 200, 30, 60, 30, 40, 40});
//		tDetalle.setWidth(PageSize.A4.getWidth()-50);
		
        cell = new Cell()
        		.add(new Paragraph(String.valueOf(rs2.getInt("coddif")))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs2.getString("fecfall"))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs2.getString("fecsep"))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs2.getString("nomdif"))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs2.getString("sexo"))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addCell(cell);
        
        String edad = rs2.getInt("edad_a") + "a. - " + rs2.getInt("edad_m") + "m. - " + rs2.getInt("edad_d") + "d.";
        cell = new Cell()
        		.add(new Paragraph(edad)
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addCell(cell);
        
        String nicho = rs2.getString("fila2") + "-" + rs2.getInt("columna2");
//		System.out.println(nicho);
        cell = new Cell()
        		.add(new Paragraph(nicho)
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(String.valueOf(rs2.getInt("codmau")))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs2.getString("desestado"))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addCell(cell);
//		document.add(tDetalle);
        
        nDifuntoCuartel++;
        nDifuntoCementerio++;
	}

	private void cabCuartel(ResultSet rs2) throws SQLException {

		tDetalle = new Table(new float[]{40, 40, 40, 200, 30, 60, 30, 40, 40});
		tDetalle.setWidth(PageSize.A4.getWidth()-50);
		
        cell = new Cell(1, 9)
        		.add(new Paragraph("LISTADO DE DIFUNTOS")
        				.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
//        bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE);
        tDetalle.addHeaderCell(cell);
		
    	String dato = "CEMENTERIO:" + rs2.getInt("codcem") + " - " + rs2.getString("nomcem");
        cell = new Cell(1,9)
        		.add(new Paragraph(dato)
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addHeaderCell(cell);
		
    	dato = "CUARTEL:" + rs2.getInt("codcuar") + " - " + rs2.getString("nomcuar");
//    	System.out.println(dato);    	
        cell = new Cell(1,9)
        		.add(new Paragraph(dato)
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("ID")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("F. Fall")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("F. Sep")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Nombre")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Sexo")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Edad")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Nicho")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Mausoleo")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Estado")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tDetalle.addHeaderCell(cell);
        
//		document.add(tCabCuartel);
        
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

	private void cabecera() throws Exception {      //Document doc
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
      cell.setBorderTop(new SolidBorder(arriba , 1));
      cell.setBorderBottom(new SolidBorder(abajo , 1));
      cell.setBorderLeft(new SolidBorder(izquierda , 1));
      cell.setBorderRight(new SolidBorder(derecha , 1));
  }
	
}