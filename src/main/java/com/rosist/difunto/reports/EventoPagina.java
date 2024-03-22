package com.rosist.difunto.reports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.rosist.difunto.venta.model.Empresa;
import com.rosist.difunto.venta.model.Sucursal;

public class EventoPagina implements IEventHandler {
    private final Document documento;
    private Sucursal sucursal;
    
	static final Logger logger = LoggerFactory.getLogger(EventoPagina.class);
    
    public EventoPagina(Document doc) {
        documento = doc;
    }
    
    public EventoPagina(Document doc, Sucursal suc) {
        documento = doc;
        sucursal = suc;
    }
     
    /**
     * Crea el rectangulo donde pondremos el encabezado
     * @param docEvent Evento de documento
     * @return Area donde colocaremos el encabezado
     */
    private Rectangle crearRectanguloEncabezado(PdfDocumentEvent docEvent) {
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();        
         
        float xEncabezado = pdfDoc.getDefaultPageSize().getX() + documento.getLeftMargin();
        float yEncabezado = pdfDoc.getDefaultPageSize().getTop() - documento.getTopMargin();
        float anchoEncabezado = page.getPageSize().getWidth() - 72;
        float altoEncabezado = 50F;
 
        Rectangle rectanguloEncabezado = new Rectangle(xEncabezado, yEncabezado, anchoEncabezado, altoEncabezado);
         
        return rectanguloEncabezado;        
    }
     
    /**
     * Crea el rectangulo donde pondremos el pie de pagina
     * @param docEvent Evento del documento
     * @return Area donde colocaremos el pie de pagina
     */
    private Rectangle crearRectanguloPie(PdfDocumentEvent docEvent) {
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
         
        float xPie = pdfDoc.getDefaultPageSize().getX() + documento.getLeftMargin();
        float yPie = pdfDoc.getDefaultPageSize().getBottom() ;
        float anchoPie = page.getPageSize().getWidth() - 72;
        float altoPie = 50F;
 
        Rectangle rectanguloPie = new Rectangle(xPie, yPie, anchoPie, altoPie);
         
        return rectanguloPie;
    }
     
    /**
     * Crea la tabla que contendra el mensaje del encabezado
     * @param mensaje Mensaje que desplegaremos
     * @return Tabla con el mensaje de encabezado
     */
    private Table crearTablaEncabezado(String mensaje) {
        float[] anchos = {1F,1F};
        Table tablaEncabezado = new Table(anchos);
        tablaEncabezado.setWidth(680F);
//        tablaEncabezado.setWidth(527F);
        tablaEncabezado.addCell(mensaje);
        return tablaEncabezado;
    }
     
    private Table crearTablaEncabezado(Sucursal sucursal, PdfDocumentEvent docEvent) {
        PdfPage page = docEvent.getPage();
        float anchoEncabezado = page.getPageSize().getWidth() - 42;
        float[] anchos = {680F,80F};
//        float[] anchos = {680F,80F};
        Table tablaEncabezado = new Table(anchos);
        tablaEncabezado.setWidth(anchoEncabezado-10);
 
        tablaEncabezado.addCell(sucursal.getEmpresa().getNombreComercial());
        Integer pageNum = docEvent.getDocument().getPageNumber(page);
        tablaEncabezado.addCell("Pagina " + pageNum);
        tablaEncabezado.addCell(sucursal.getDescri());
        tablaEncabezado.addCell("");
         
        return tablaEncabezado;
    }

    /**
     * Crea la tabla de pie de pagina, con el numero de pagina
     * @param docEvent Evento del documento
     * @return Pie de pagina con el numero de pagina
     */
    private Table crearTablaPie(PdfDocumentEvent docEvent) {
        PdfPage page = docEvent.getPage();
        float anchoPie = page.getPageSize().getWidth() - 72;
        float[] anchos = {1F};
        Table tablaPie = new Table(anchos);
        tablaPie.setWidth(527F);
        tablaPie.setWidth(anchoPie-10);
        Integer pageNum = docEvent.getDocument().getPageNumber(page);
         
//        tablaPie.addCell("___________________________");
        tablaPie.addCell("Pagina " + pageNum);
         
        return tablaPie;
    }
     
 
    /**
     * Manejador del evento de cambio de pagina, agrega el encabezado y pie de pagina
     * @param event Evento de pagina
     */
    @Override
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
         
        Table tablaEncabezado = this.crearTablaEncabezado(sucursal,docEvent);
        Rectangle rectanguloEncabezado = this.crearRectanguloEncabezado(docEvent);
        Canvas canvasEncabezado = new Canvas(canvas, pdfDoc, rectanguloEncabezado);
        canvasEncabezado.add(tablaEncabezado);
 
        Table tablaNumeracion = this.crearTablaPie(docEvent);
        Rectangle rectanguloPie = this.crearRectanguloPie(docEvent);
        Canvas canvasPie = new Canvas(canvas, pdfDoc, rectanguloPie);
        canvasPie.add(tablaNumeracion);
    }

}
