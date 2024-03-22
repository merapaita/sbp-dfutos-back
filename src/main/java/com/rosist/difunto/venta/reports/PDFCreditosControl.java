package com.rosist.difunto.venta.reports;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.rosist.difunto.reports.EventoPagina;
import com.rosist.difunto.venta.model.Cliente;
import com.rosist.difunto.venta.model.Credito;
import com.rosist.difunto.venta.model.Empresa;
import com.rosist.difunto.venta.model.Sucursal;

public class PDFCreditosControl {

	private Document document;
	private PdfDocument pdfDocument;
	private PdfWriter pdfWriter = null;
	private PdfFont fontContenido, fontContenidoTitulo;
	private Table table;
	private Cell cell;
//	private int pagina = 0;
//	private int linea = 0;
	private List<Credito> creditos;

	private Sucursal sucursal = null;

	static final Logger log = LoggerFactory.getLogger(PDFCreditosControl.class);

	@SuppressWarnings("unchecked")
	public PDFCreditosControl(Map<String, Object> parametros) {
		creditos = (List<Credito>) parametros.get("creditos");
		sucursal = (Sucursal) parametros.get("sucursal");
	}

	public byte[] creaReporte() throws IOException, SQLException {

		ByteArrayOutputStream docBytes = new ByteArrayOutputStream();
		pdfWriter = new PdfWriter(docBytes);

		pdfDocument = new PdfDocument(pdfWriter);
		document = new Document(pdfDocument, PageSize.A4); // , new PageSize(envelope)
		fontContenido = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
		fontContenidoTitulo = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
		DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		
		EventoPagina evento = new EventoPagina(document, sucursal);
		pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, evento);

		document.setMargins(90, 26, 45, 26);

		creditos.forEach(credito -> {
			table = new Table(new float[] { 50, 50, 50, 50, 50, 50, 45, 45, 45, 50, 45 }, true);
			cell = new Cell(1,11).add(new Paragraph("CONTROL DE CREDITO:" + credito.getCodcre())
					.setTextAlignment(TextAlignment.CENTER))
					.setFont(fontContenidoTitulo)
					.setFontSize(10);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.WHITE, ColorConstants.BLACK, ColorConstants.BLACK);
			table.addCell(cell);
			cell = new Cell(1,4).add(new Paragraph("Credito :" + credito.getCodcre())
					.setTextAlignment(TextAlignment.LEFT))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.WHITE, ColorConstants.BLACK, ColorConstants.WHITE);
			table.addCell(cell);
			cell = new Cell(1,4).add(new Paragraph("Monto :" + credito.getMtocre())
					.setTextAlignment(TextAlignment.LEFT))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.WHITE, ColorConstants.BLACK, ColorConstants.WHITE);
			table.addCell(cell);
			cell = new Cell(1,3).add(new Paragraph("Venta :" + credito.getVenta().getCodvta())
					.setTextAlignment(TextAlignment.LEFT))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.WHITE, ColorConstants.BLACK, ColorConstants.BLACK);
			table.addCell(cell);
/**/			
			Cliente _cliente = credito.getVenta().getCliente();
			if (credito.getVenta().getClienteOtro()!=null) {
				_cliente = credito.getVenta().getClienteOtro();
			}
			cell = new Cell(1,6).add(new Paragraph("Cliente :" + _cliente.getNomcli())
					.setTextAlignment(TextAlignment.LEFT))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.WHITE, ColorConstants.BLACK, ColorConstants.WHITE);
			table.addCell(cell);
			cell = new Cell(1,5).add(new Paragraph("Direccion :" + _cliente.getDircli())
					.setTextAlignment(TextAlignment.LEFT))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.WHITE, ColorConstants.BLACK, ColorConstants.BLACK);
			table.addCell(cell);
/**/				
			cell = new Cell(1,6).add(new Paragraph("Aval :" + credito.getVenta().getAval().getNomcli())
					.setTextAlignment(TextAlignment.LEFT))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.WHITE);
			table.addCell(cell);
			cell = new Cell(1,5).add(new Paragraph("Direccion :" + credito.getVenta().getAval().getDircli())
					.setTextAlignment(TextAlignment.LEFT))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
			table.addCell(cell);
/**/
			cell = new Cell().add(new Paragraph("FECHA")
					.setTextAlignment(TextAlignment.CENTER))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
			table.addCell(cell);
			cell = new Cell(1,2).add(new Paragraph("MOVIMIENTO")
					.setTextAlignment(TextAlignment.CENTER))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
			table.addCell(cell);
			cell = new Cell().add(new Paragraph(String.valueOf("CREDITO"))
					.setTextAlignment(TextAlignment.CENTER))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
			table.addCell(cell);
			cell = new Cell(1,2).add(new Paragraph(String.valueOf("INTERES"))
					.setTextAlignment(TextAlignment.CENTER))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
			table.addCell(cell);
			cell = new Cell(1,2).add(new Paragraph(String.valueOf("AMOTIZACION"))
					.setTextAlignment(TextAlignment.CENTER))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
			table.addCell(cell);
			cell = new Cell(1,2).add(new Paragraph(String.valueOf("AJUSTE"))
					.setTextAlignment(TextAlignment.CENTER))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
			table.addCell(cell);
			cell = new Cell().add(new Paragraph(String.valueOf("SALDO"))
					.setTextAlignment(TextAlignment.CENTER))
					.setFont(fontContenido)
					.setFontSize(7);
//			bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
			table.addCell(cell);
			
/**/			
			credito.getKarcre().forEach(items -> {
				cell = new Cell().add(new Paragraph(items.getFecha().format(f))
						.setTextAlignment(TextAlignment.CENTER))
						.setFont(fontContenido)
						.setFontSize(7);
//				bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
				table.addCell(cell);
				cell = new Cell().add(new Paragraph(items.getTipmov())
						.setTextAlignment(TextAlignment.CENTER))
						.setFont(fontContenido)
						.setFontSize(7);
//				bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
				table.addCell(cell);
				cell = new Cell().add(new Paragraph(items.getCodmov())
						.setTextAlignment(TextAlignment.CENTER))
						.setFont(fontContenido)
						.setFontSize(7);
//				bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
				table.addCell(cell);
				cell = new Cell().add(new Paragraph(String.format("%.2f", items.getMtocre()))
						.setTextAlignment(TextAlignment.RIGHT))
						.setFont(fontContenido)
						.setFontSize(7);
//				bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
				table.addCell(cell);
				cell = new Cell().add(new Paragraph(String.format("%.2f", items.getMtoint()))
						.setTextAlignment(TextAlignment.RIGHT))
						.setFont(fontContenido)
						.setFontSize(7);
//				bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
				table.addCell(cell);
				cell = new Cell().add(new Paragraph(String.format("%.2f", items.getTotint()))
						.setTextAlignment(TextAlignment.RIGHT))
						.setFont(fontContenido)
						.setFontSize(7);
//				bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
				table.addCell(cell);
				cell = new Cell().add(new Paragraph(String.format("%.2f", items.getMtoamo()))
						.setTextAlignment(TextAlignment.RIGHT))
						.setFont(fontContenido)
						.setFontSize(7);
//				bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
				table.addCell(cell);
				cell = new Cell().add(new Paragraph(String.format("%.2f", items.getTotamo()))
						.setTextAlignment(TextAlignment.RIGHT))
						.setFont(fontContenido)
						.setFontSize(7);
//				bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
				table.addCell(cell);
				cell = new Cell().add(new Paragraph(String.format("%.2f", items.getMtoaju()))
						.setTextAlignment(TextAlignment.RIGHT))
						.setFont(fontContenido)
						.setFontSize(7);
//				bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
				table.addCell(cell);
				cell = new Cell().add(new Paragraph(String.format("%.2f", items.getTotaju()))
						.setTextAlignment(TextAlignment.RIGHT))
						.setFont(fontContenido)
						.setFontSize(7);
//				bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
				table.addCell(cell);
				cell = new Cell().add(new Paragraph(String.format("%.2f", items.getSaldo()))
						.setTextAlignment(TextAlignment.RIGHT))
						.setFont(fontContenido)
						.setFontSize(7);
//				bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
				table.addCell(cell);
			});
//			table.setBorder(new SolidBorder(ColorConstants.GREEN, 2));

//			cell = new Cell(1,11);
	//			bordes(cell, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK, ColorConstants.BLACK);
//			table.addCell(cell);
			document.add(table);
			document.add(new Paragraph(""));
			document.add(new Paragraph(""));
		});
		
		document.close();

		return docBytes.toByteArray();
	}

	private void bordes(Cell cell, Color arriba, Color abajo, Color izquierda, Color derecha) {
		cell.setBorderTop(new SolidBorder(arriba, 1));
		cell.setBorderBottom(new SolidBorder(abajo, 1));
		cell.setBorderLeft(new SolidBorder(izquierda, 1));
		cell.setBorderRight(new SolidBorder(derecha, 1));
	}
}

//			table.complete();
//			if (i % 30 == 0) {
//				table.flush();
//			}