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
import com.rosist.difunto.modelSbp.Sucursal;

public class PdfMausoleo {

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
	private Table tReporte;
	private Cell cell;
	private Sucursal sucursal;
	
	int pagina = 0;
	int linea = 0;

	static final Logger logger = LoggerFactory.getLogger(PdfMausoleo.class);
	
	public PdfMausoleo(Map<String, Object> parametros) {
		datasource = (DriverManagerDataSource) parametros.get("datasource");
		condicion = (String) parametros.get("condicion");
		order = (String) parametros.get("order");
		sucursal   =   (Sucursal)parametros.get("sucursal");
	}
	
	public byte[] creaReporte() throws Exception {
		
        sql = "SELECT ma.codcem, cem.nomcem, cem.local, ma.codmau, ma.lotizado, ma.nomlote, "
                + "          ma.tipomau, p.descri destipmau, ma.ubicacion, ma.familia, ma.tipdoccli, ma.doccli, "
                + "          cl.nomcli, ma.estado, ma.totdif, ma.numdif, ma.estvta, ma.area_adq, ma.area_cons, ma.area_cerc, ma.observ"
                + "     FROM mausoleo ma LEFT JOIN cementerio cem ON ma.codcem=cem.codcem"
                + "                      LEFT JOIN clientesunat cl ON ma.tipdoccli=cl.tipdoc and ma.doccli=cl.doccli"
                + "                      LEFT JOIN parmae p ON ma.tipomau=p.codigo AND p.tipo='TIPMAU'"
                + "                      LEFT JOIN parmae e ON ma.estado=p.codigo AND p.tipo='ESTMAU'"
                + "    where 1=1 " + condicion 
                + " order by " + order;
		
		conexion = datasource.getConnection();
        pstmt = conexion.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
      		  ResultSet.CONCUR_UPDATABLE);
		rs = pstmt.executeQuery ();
		
        ByteArrayOutputStream docBytes = new ByteArrayOutputStream();
        
        pdfWriter = new PdfWriter(docBytes);
		pdfDocument = new PdfDocument(pdfWriter);
		document = new Document(pdfDocument, PageSize.A4.rotate()); // , new PageSize(envelope)
        document.setMargins(75, 36, 75, 36);
        
        EventoPagina evento = new EventoPagina(document);
        pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, evento);
        
        plantilla();
		tReporte = new Table(new float[]{40, 130, 40, 130, 130, 130, 70});
		tReporte.setWidth(PageSize.A4.rotate().getWidth()-50);
		cabecera();
        
        int codcem =0;
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

    private void detalle(ResultSet rs) throws SQLException {
        cell = new Cell()
        		.add(new Paragraph(String.valueOf(rs.getInt("codmau")))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs.getString("familia"))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs.getString("destipmau"))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);

        cell = new Cell()
        		.add(new Paragraph(rs.getString("ubicacion"))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);

        cell = new Cell()
        		.add(new Paragraph(rs.getString("nomlote"))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        String cArea = "Adq: " + rs.getString("area_adq") + " , Const: " + rs.getString("area_cons") + " Cerc: " + rs.getString("area_cerc");
        cell = new Cell()
        		.add(new Paragraph(cArea)
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs.getString("observ"))
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
                
    }
	
    private void gCementerio(ResultSet rs) throws SQLException {
    	
    	String dato = "CEMENTERIO:" + rs.getInt("codcem") + rs.getString("nomcem");
    	
        cell = new Cell(1,7)
        		.add(new Paragraph(dato)
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
//**//        
        cell = new Cell()
        		.add(new Paragraph("Codigo")
        		.setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Familia")
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
        		.add(new Paragraph("Ubicación")
        		.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Lote")
        		.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Area")
        		.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Observación")
        		.setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
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
        		.add(new Paragraph("LISTADO DE MAUSOLEO")
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
