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
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

public class PdfCuartel {

	private DriverManagerDataSource datasource;
	private Connection conexion;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql = "";
	
	String condicion = "";
	String reporte = "";
	boolean lProcesa = false;
	
	private PdfWriter pdfWriter = null;
	private PdfDocument pdfDocument;
	private Document document;
	private PdfFont fontContenido;
	private Table tReporte;
	private Cell cell;
	
	int pagina = 0;
	int linea = 0;

	static final Logger logger = LoggerFactory.getLogger(PdfServicio.class);
	
	public PdfCuartel(Map<String, Object> parametros) {
		datasource = (DriverManagerDataSource) parametros.get("datasource");
		condicion = (String) parametros.get("condicion");
//		reporte = (String) parametros.get("reporte");
	}
	
	public byte[] creaReporte() throws Exception {
		
        sql = "SELECT cu.codcem, ce.nomcem, cu.codcuar, cu.nomcuar, cu.tipcuar, tc.descri destipcua, "
                + "          cu.filas, cu.columnas, cu.orden, cu.estado, e.descri desest" 
                + "     FROM cuartel cu LEFT JOIN cementerio ce ON cu.codcem=ce.codcem"
                + "                     LEFT JOIN parmae tc     ON tc.tipo='TIPCUA' AND tc.codigo=cu.tipcuar"
                + "                     LEFT JOIN parmae e      ON  e.tipo='ESTCUA' AND  e.codigo=cu.estado"
                + "    WHERE 1=1 " + condicion;
        int codcem =0;
        int codcuar=0;
		
logger.info("sql:" + sql);        
		conexion = datasource.getConnection();
        pstmt = conexion.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
      		  ResultSet.CONCUR_UPDATABLE);

		rs = pstmt.executeQuery (sql);
		
        ByteArrayOutputStream docBytes = new ByteArrayOutputStream();
        pdfWriter = new PdfWriter(docBytes);

		pdfDocument = new PdfDocument(pdfWriter);
		document = new Document(pdfDocument, PageSize.A4); // , new PageSize(envelope)
        document.setMargins(75, 36, 75, 36);
        
        EventoPagina evento = new EventoPagina(document);
        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, evento);
        
        plantilla();
		tReporte = new Table(new float[]{70, 240, 70, 70, 70});
		tReporte.setWidth(PageSize.A4.getWidth()-50);
		cabecera();
        
        if (rs.first()){
        	rs.beforeFirst();
        	lProcesa = true;
        	codcem = 0;
            while (rs.next()) {
				if (  (!(codcem == rs.getInt("codcem"))) ){     // || (linea==3)
					gCementerio(rs);
				}
				detalle(rs);
				codcem  =rs.getInt("codcem");
            }
        }
		document.add(tReporte);
		document.close();
        return docBytes.toByteArray();
	}
	
    private void gCementerio(ResultSet rs) throws SQLException {
    	
    	String dato = "CEMENTERIO:" + rs.getInt("codcem") + rs.getString("nomcem");
    	
        cell = new Cell(1,5)
        		.add(new Paragraph(dato)
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Codigo")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Cuartel")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Tipo")
        		.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("F x C")
        		.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Estado")
        		.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
    }
	
    private void detalle(ResultSet rs) throws SQLException {
    	
        cell = new Cell()
        		.add(new Paragraph(String.valueOf(rs.getInt("codcuar")))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs.getString("nomcuar"))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs.getString("destipcua"))
        		.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        String fxc = String.valueOf(rs.getInt("filas")) + " X " + String.valueOf(rs.getInt("columnas"));
        cell = new Cell()
        		.add(new Paragraph(fxc)
        		.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs.getString("desest"))
        		.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        linea++;
    }
		
	private void plantilla() throws IOException {
		fontContenido = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
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
        cell = new Cell(1, 5)
        		.add(new Paragraph("LISTADO DE CUARTELES")
        				.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell(1, 11)
        		.add(new Paragraph(" ")
        				.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE);
        tReporte.addHeaderCell(cell);
        linea = 5;
    }

    private void bordes(Cell cell, Color arriba, Color abajo, Color izquierda, Color derecha) {
//      cell.setUseVariableBorders(true);
      cell.setBorderTop(new SolidBorder(arriba , 1));
      cell.setBorderBottom(new SolidBorder(abajo , 1));
      cell.setBorderLeft(new SolidBorder(izquierda , 1));
      cell.setBorderRight(new SolidBorder(derecha , 1));
  }

}
