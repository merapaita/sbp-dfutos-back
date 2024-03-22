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

public class PdfServicio {

	private DriverManagerDataSource datasource;
	private Connection conexion;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	private PdfFont fontContenido;
	private PdfWriter pdfWriter = null;
	private PdfDocument pdfDoc;
	private Document documento;
	private int pagina;
	private int linea;
	private Table tReporte;
	private Cell cell;
	
	private String sql = "";

	static final Logger logger = LoggerFactory.getLogger(PdfServicio.class);
	
	public PdfServicio(Map<String, Object> parametros) {
		datasource = (DriverManagerDataSource) parametros.get("datasource");
	}

	public byte[] creaReporte() throws Exception {
		
		sql = " select s.idser, s.tipser, s.correl, ts.descri destipser, s.desser, s.tipope, top.descri destipope, s.mtomed, s.mtosbp, s.mtotot, "
				+ "        s.igv, s.tartra, s.codpart, p.descri descodpart, enable "
				+ "   from servicios s left join parmae ts on ts.tipo='TIPSER' AND ts.codigo=s.tipser"
				+ "                    left join partida p on s.codpart=p.codpart"
				+ "                    left join parmae top on top.tipo='TIPOPE' and s.tipope=top.codigo"
				+ "    where 1=1 "
				+ " order by s.idser ";
		
		conexion = datasource.getConnection();
        pstmt = conexion.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
      		  ResultSet.CONCUR_UPDATABLE);

		rs = pstmt.executeQuery (sql);
logger.info("creaReporte...");		
        
//		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//		String fondo = classloader.getResource("fondo.png").getFile();

//		Image imagen = new Image(ImageDataFactory.create(fondo));  
//		Map<String, Object> parametros = new HashMap<String, Object>();
        ByteArrayOutputStream docBytes = new ByteArrayOutputStream();
        pdfWriter = new PdfWriter(docBytes);

		pdfDoc = new PdfDocument(pdfWriter);
		documento = new Document(pdfDoc, PageSize.A4); // , new PageSize(envelope)
        documento.setMargins(75, 36, 75, 36);
logger.info("creaReporte...1");		
//        Empresa empresa = 
        EventoPagina evento = new EventoPagina(documento);
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, evento);
        
		plantilla();	// writer
		tReporte = new Table(new float[]{30, 200, 40, 40, 40, 30, 30, 30, 30, 30, 20});
		tReporte.setWidth(PageSize.A4.getWidth()-50);
		cabecera();
        
		if(rs.first()){
            rs.beforeFirst();
			pagina=0;
			linea=0;
			String tipser = "";
            while (rs.next()) {
				if (  (!(tipser.equals(rs.getString("tipser"))) )  ){     // || (linea==3)
					tgTipSer();
				}
				detalle(rs);
				tipser  =rs.getString("tipser");
//				rs.next();
//				if (rs.isAfterLast()){
//					rs.last();
//				} else {
//					if (  !(tipser.equals(rs.getInt("tipser")))  ){
//						rs.previous();
//					} else rs.previous();
//				}
//
//				if (rs.isLast()){
//					documento.close();
//				}
//                
////    			if (linea % 30 == 0) {
////    				tReporte.flush();
////    			}
            }
        }
		documento.add(tReporte);
		documento.close();
        return docBytes.toByteArray();
        
	}

    private void tgTipSer() throws SQLException {     // , DocumentException,   Document doc
//    	tTipoServicio = new Table(new float[]{520});
//    	tTipoServicio.setWidth(PageSize.A4.getWidth()-60);
/**/    
    	
        String dato = "Tipo Servicio : " + rs.getString("tipser") + " " + rs.getString("destipser");
        cell = new Cell(1,11)
        		.add(new Paragraph(dato).setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
//        document.add(tTipoServicio);
        linea++;
    }
	
    private void detalle(ResultSet rs) throws SQLException {     //Document doc, 
//    	tDetalle = new Table(new float[]{40, 160, 30, 40, 40, 40, 40, 40, 40, 50});
//
//    	tDetalle = new Table(new float[]{40, 150, 30, 40, 40, 40, 40, 40, 60});
//    	tDetalle.setWidth(PageSize.A4.getWidth()-60);
//        tDetalle.setWidthPercent(100);
//        tDetalle.setLockedWidth(true);
    	
        cell = new Cell()
        		.add(new Paragraph(rs.getString("idser")).setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs.getString("desser")).setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs.getString("tipope")).setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        String xMtoSbp = String.format("%.02f", rs.getDouble("mtosbp"));
        cell = new Cell()
        		.add(new Paragraph(xMtoSbp).setTextAlignment(TextAlignment.RIGHT))
        		.setFont(fontContenido)
        		.setFontSize(8);
//        cell.setPaddingRight(5);
        tReporte.addCell(cell);
        
        String xMtoMed = String.format("%.02f", rs.getDouble("mtomed"));
        cell = new Cell()
        		.add(new Paragraph(xMtoMed).setTextAlignment(TextAlignment.RIGHT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        String xIGV = String.format("%.02f", rs.getDouble("igv"));
        cell = new Cell()
        		.add(new Paragraph(xIGV).setTextAlignment(TextAlignment.RIGHT))
        		.setFont(fontContenido)
        		.setFontSize(8);
//        cell.setPaddingRight(5);
        tReporte.addCell(cell);
        
        String xMtoTot = String.format("%.02f", rs.getDouble("mtotot"));
        cell = new Cell()
        		.add(new Paragraph(xMtoTot).setTextAlignment(TextAlignment.RIGHT))
        		.setFont(fontContenido)
        		.setFontSize(8);
//        cell.setPaddingRight(5);
        tReporte.addCell(cell);
        
        String xTarTra = String.format("%.02f", rs.getDouble("tartra"));
        cell = new Cell()
        		.add(new Paragraph(xTarTra).setTextAlignment(TextAlignment.RIGHT))
        		.setFont(fontContenido)
        		.setFontSize(8);
//        cell.setPaddingRight(5);
        tReporte.addCell(cell);
        
        String xTarSSF = String.format("%.02f", rs.getDouble("tartra"));
        cell = new Cell()
        		.add(new Paragraph(xTarSSF).setTextAlignment(TextAlignment.RIGHT))
        		.setFont(fontContenido)
        		.setFontSize(8);
//        cell.setPaddingRight(5);
        tReporte.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs.getString("codpart")).setTextAlignment(TextAlignment.RIGHT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph(rs.getString("enable")).setTextAlignment(TextAlignment.RIGHT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addCell(cell);
        

//        System.out.println(rs.getString("nomcli") + ", " + cCP + ", " + rs.getDouble("mtosbp") + ", " + rs.getDouble("mtomed") + ", " + rs.getDouble("mtodonsbp") + ", " + rs.getDouble("mtodonmed"));
//        document.add(tDetalle);
        
        linea++;
    }
	
    private void cabecera() throws Exception {      //Document doc
        cell = null;
//        tCabecera = new Table(new float[]{10, 80, 10});
//        tCabecera.setWidth(PageSize.A4.getWidth()-60);
//        tCabecera.setWidths(new int[]{30, 18, 13, 13, 13, 13});
//        tCabecera.setLockedWidth(true);
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
        cell = new Cell(1, 11)
        		.add(new Paragraph("LISTADO DE SERVICIOS BRINDADOS")
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
/////////        
//        tTitulo = new Table(new float[]{40, 160, 30, 40, 40, 40, 40, 40, 40, 50});
        
//        tTitulo.setWidth(PageSize.A4.getWidth()-60);
//        tTitulo.setLockedWidth(true);
        
        cell = new Cell()
        		.add(new Paragraph("Codigo").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setWidth(8)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Servicio").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
//        		.setWidth(40)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("T. Op.").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);

        cell = new Cell()
        		.add(new Paragraph("M. SBP").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("M. Med").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("I.G.V.").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Total").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("M. Trab.").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("SSF.").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph("Partida").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
        
        cell = new Cell()
        		.add(new Paragraph().setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
        tReporte.addHeaderCell(cell);
///////////
//        cell = new Cell(1,3).add(tTitulo);
//        tCabecera.addCell(cell);
        
//        document.add(tCabecera);
//        document.add(tTitulo);
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
