package com.rosist.difunto.reports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.rosist.difunto.modelSbp.Vtafco;

public class PdfReciboVtaFcoNuevo {

    PdfWriter pdfWriter = null;
    Table table;
    Cell cell;
    PdfFont fontContenido;
    
    static final Logger logger = LoggerFactory.getLogger(PdfReciboVtaFcoNuevo.class);
    
	Vtafco vtafco;
	public PdfReciboVtaFcoNuevo(Map<String, Object> parametros) {
		vtafco = (Vtafco)parametros.get("vtafco");
	}

	public byte[] creaPDF() throws MalformedURLException  {
        try {
			fontContenido = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
		} catch (IOException e) {
			e.printStackTrace();
		}

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//		String encabezado = classloader.getResource("encabezado.png").getFile();
//		String firma      = classloader.getResource("firma.png").getFile();

//		Image imgEncabezado = new Image(ImageDataFactory.create(encabezado));  
//		Image imgFirma      = new Image(ImageDataFactory.create(firma));
		Map<String, Object> parametros = new HashMap<String, Object>();
		
        ByteArrayOutputStream docBytes = new ByteArrayOutputStream();
        pdfWriter = new PdfWriter(docBytes);
        
        PdfDocument pdfDoc = new PdfDocument(pdfWriter);
//        Rectangle envelope = new Rectangle(222, 793);
        Document documento = new Document(pdfDoc, PageSize.A4);	// , new PageSize(envelope)
//        documento.setMargins(10f, 10f, 10f, 10f);

//        documento.add(imgEncabezado);
    	table = new Table(new float[]{150, 130, 120, 120});
        table.setWidth(PageSize.A4.getWidth()-60);
        
    	String cNombre = "Paciente: " ;
//    	String cNombre = "Paciente: " + analisis.getPaciente().getApellidos() + ", " + analisis.getPaciente().getNombres();
        cell = new Cell(1,2)
        		.add(new Paragraph(cNombre).setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        		
        table.addCell(cell);
        
        int edad = 5;
//        int edad = calculaEdad(analisis.getPaciente().getFecnac(), analisis.getFecha());
        cell = new Cell(1,2)
        		.add(new Paragraph("Edad:" + edad).setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        	
        		.setFontSize(8);
        table.addCell(cell);
        
        cell = new Cell(1,2)
        		.add(new Paragraph("Codigo: " ).setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        table.addCell(cell);
        
        cell = new Cell(1,2)
        		.add(new Paragraph("Fecha: " ))
        		.setFont(fontContenido)
        		.setFontSize(8);
        table.addCell(cell);

       	cNombre = "Medico: " ;
        cell = new Cell(1,2)
        		.add(new Paragraph(cNombre).setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        table.addCell(cell);
        
        cell = new Cell(1,2)
        		.add(new Paragraph("Sexo: " ))
        		.setFont(fontContenido)
        		.setFontSize(8);
        table.addCell(cell);
        
        cell = new Cell(1,4)
        		.add(new Paragraph(" ").setTextAlignment(TextAlignment.LEFT))
        		.setFont(fontContenido)
        		.setFontSize(8);
        table.addCell(cell);
        
        cell = new Cell(1,4)
        		.add(new Paragraph())
        		.setFont(fontContenido)
        		.setFontSize(8);
        table.addCell(cell);

//        if (!analisis.getDescripcion().isEmpty()) {
//            cell = new Cell(1,4)
//            		.add(new Paragraph(analisis.getDescripcion()).setTextAlignment(TextAlignment.CENTER))
//            		.setFont(fontContenido)
//            		.setFontSize(8);
//            table.addCell(cell);
//        }
        
        cell = new Cell()
        		.add(new Paragraph("EXAMEN REALIZADO").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
//        bordes(cell,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.WHITE);
        table.addCell(cell);
        cell = new Cell()
        		.add(new Paragraph("RESULTADO").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
//        bordes(cell,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.WHITE,ColorConstants.WHITE);
        table.addCell(cell);
        cell = new Cell()
        		.add(new Paragraph("VAL. REFERENCIAL").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
//        bordes(cell,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.WHITE,ColorConstants.WHITE);
        table.addCell(cell);
        cell = new Cell()
        		.add(new Paragraph("UNIDAD").setTextAlignment(TextAlignment.CENTER))
        		.setFont(fontContenido)
        		.setFontSize(8);
//        bordes(cell,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.WHITE,ColorConstants.BLACK);
        table.addCell(cell);
        
//        String cTipo = "";
//        List<Iteana> tempo = analisis.getDetiteana();
//        if (analisis.getCatmue().getIdCatmue()==4||analisis.getCatmue().getIdCatmue()==32||analisis.getCatmue().getIdCatmue()==33) {
//        	// ordena resultado
//        	List<Iteana> _tmpRess = new ArrayList<>(), _tmpOtros= new ArrayList<>();
//        	analisis.getDetiteana().forEach(det -> {
//        		if (det.getResultado()!=null) {
//                	if (det.getExamen().getTipo().getIdTipo()==5||det.getExamen().getTipo().getIdTipo()==24) {
//                		_tmpRess.add(det);
//                	} else {
//                		_tmpOtros.add(det);
//                	}
//        		}
//        	});
//        	_tmpRess.forEach(det -> {
//           		if (det.getResultado().trim().toUpperCase().substring(0, 1).equals("S")) det.setTmp("1");
//           		if (det.getResultado().trim().toUpperCase().substring(0, 1).equals("I")) det.setTmp("2");
//           		if (det.getResultado().trim().toUpperCase().substring(0, 1).equals("R")) det.setTmp("3");
//        	});
//            Collections.sort(_tmpRess, (x, y) -> x.getTmp().compareToIgnoreCase(y.getTmp()));
////            Collections.sort(_tmpRess, new Comparator<Iteana>() {
////            	@Override
////            	public int compare(Iteana p1, Iteana p2) {
////            		return p1.getResultado().compareToIgnoreCase(p2.getResultado());
////            	}
////            });
//            _tmpRess.forEach(det -> {
//            	_tmpOtros.add(det);
//            });
//            tempo = _tmpOtros;
//        }
        
//        for (Iteana registro : tempo) {		// analisis.getDetiteana()
//        	if (cTipo != registro.getExamen().getTipo().getDescripcion()) {
//                cell = new Cell(1,4)
//                		.add(new Paragraph(" ").setTextAlignment(TextAlignment.CENTER))
//                		.setFont(fontContenido)
//                		.setFontSize(8);
//                table.addCell(cell);
//                
//                cell = new Cell(1,4)
//                		.add(new Paragraph(registro.getExamen().getTipo().getDescripcion()).setTextAlignment(TextAlignment.CENTER))
//                		.setFont(fontContenido)
//                		.setFontSize(8);
//                bordes(cell,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.BLACK);
//                table.addCell(cell);
//        	}
//        	if (registro.getResultado()!=null) {
//                cell = new Cell()
//                		.add(new Paragraph(registro.getExamen().getDescripcion()).setTextAlignment(TextAlignment.LEFT))
//                		.setPadding(1f)
//                		.setFont(fontContenido)
////                		.setHeight(10)
//                		.setFontSize(7);
//                bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.BLACK,ColorConstants.WHITE);
//                table.addCell(cell);
//                cell = new Cell()
//                		.add(new Paragraph(registro.getResultado()).setTextAlignment(TextAlignment.CENTER))
//                		.setPadding(1f)
//                		.setFont(fontContenido)
////                		.setHeight(10)
//                		.setFontSize(7);
//                bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE);
//                table.addCell(cell);
//                cell = new Cell()
//                		.add(new Paragraph((registro.getExamen().getValref()!=null?registro.getExamen().getValref():"")).setTextAlignment(TextAlignment.CENTER))
//                		.setPadding(1f)
//                		.setFont(fontContenido)
////                		.setHeight(10)
//                		.setFontSize(7);
//                bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE);
//                table.addCell(cell);
//                cell = new Cell()
//                		.add(new Paragraph((registro.getExamen().getUnidad()!=null?registro.getExamen().getUnidad():"")).setTextAlignment(TextAlignment.CENTER))
//                		.setPadding(1f)
//                		.setFont(fontContenido)
////                		.setHeight(10)
//                		.setFontSize(7);
//                bordes(cell,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.WHITE,ColorConstants.BLACK);
//                table.addCell(cell);
//        	}
//            
//            cTipo = registro.getExamen().getTipo().getDescripcion();
//        }
        
//        if (analisis.getObserv()!=null) {
//            cell = new Cell(1,4)
//            		.add(new Paragraph("OBSERVACIONES:" + analisis.getObserv()).setTextAlignment(TextAlignment.LEFT))
//            		.setFont(fontContenido)
//            		.setFontSize(8);
//            bordes(cell,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.BLACK);
//            table.addCell(cell);
//        }
//
//        cell = new Cell(1,4)
//           		.add(new Paragraph(":").setTextAlignment(TextAlignment.LEFT))
//           		.setFont(fontContenido)
//           		.setFontSize(8);
//        bordes(cell,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.BLACK);
//        table.addCell(cell);
//        
//        cell = new Cell(1,3)
////        		.add(new Paragraph("OBSERVACIONES:" + analisis.getObserv()).setTextAlignment(TextAlignment.LEFT))
//        		.setFont(fontContenido)
//           		.setFontSize(8);
//        bordes(cell,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.WHITE);
//        table.addCell(cell);
//        cell = new Cell()
//          		.add(imgFirma.scaleToFit(120, 110).setTextAlignment(TextAlignment.LEFT))
//           		.setFont(fontContenido)
//           		.setFontSize(8);
//        bordes(cell,ColorConstants.BLACK,ColorConstants.BLACK,ColorConstants.WHITE,ColorConstants.BLACK);
//        table.addCell(cell);
        
        documento.add(table);
        documento.close();
        return docBytes.toByteArray();
	}
	
    private int calculaEdad(LocalDate fecnac, LocalDate fecact) {
    	int edad = fecact.getYear() - fecnac.getYear();
    	if (fecact.getDayOfYear() < fecnac.getDayOfYear()) {
    		edad--;
    	}
		return edad;
	}

//	private void bordes(Cell cell, Color arriba, Color abajo, Color izquierda, Color derecha) {
////      cell.setUseVariableBorders(true);
//      cell.setBorderTop(new SolidBorder(arriba , 1));
//      cell.setBorderBottom(new SolidBorder(abajo , 1));
//      cell.setBorderLeft(new SolidBorder(izquierda , 1));
//      cell.setBorderRight(new SolidBorder(derecha , 1));
//  }
    
}
