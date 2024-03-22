package com.rosist.difunto.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;

public class PdfCuartelNichosEstados {

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

	static final Logger logger = LoggerFactory.getLogger(PdfServicio.class);

	public PdfCuartelNichosEstados(Map<String, Object> parametros) {
		datasource = (DriverManagerDataSource) parametros.get("datasource");
		condicion = (String) parametros.get("condicion");
//      empresa = (Empresa)model.get("empresa");
//      sucursal = (Sucursal)model.get("sucursal");
	}

	public byte[] creaReporte() throws Exception {

		sql = "SELECT t.codcem, c.nomcem, t.codcuar, cu.nomcuar, fila1, fila2, col1, col2 "
				+ "  FROM nicho_t t LEFT JOIN cementerio c ON t.codcem=c.codcem "
				+ "                 LEFT JOIN cuartel cu ON t.codcem=cu.codcem AND t.codcuar= cu.codcuar"
				+ "  where 1=1 " + condicion;

System.out.println("sql:" + sql);

		conexion = datasource.getConnection();
		pstmt = conexion.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		rs = pstmt.executeQuery();

		ByteArrayOutputStream docBytes = new ByteArrayOutputStream();

		pdfWriter = new PdfWriter(docBytes);
		pdfDocument = new PdfDocument(pdfWriter);
		document = new Document(pdfDocument, PageSize.A4); // , new PageSize(envelope)
		document.setMargins(75, 36, 75, 36);

		procesa = false;

		if (rs.first()) {
			rs.beforeFirst();
			procesa = true;

			int codcem = 0;
			int codcuar = 0;
			int fila = 0;
			Paragraph paragraph = new Paragraph();
			while (rs.next()) {
				if (!(codcem == rs.getInt("codcem"))) {
					cabCementerio(rs);
				}
				if (!(codcem == rs.getInt("codcem") && codcuar == rs.getInt("codcuar"))) {
					cabCuartel(rs);
				}
				if (!(codcem == rs.getInt("codcem") && codcuar == rs.getInt("codcuar") && fila == rs.getInt("fila1"))) {
					cabFila(rs);
				}
				
				paragraph.add(detalle(rs));
				
				codcem = rs.getInt("codcem");
				codcuar = rs.getInt("codcuar");
				fila = rs.getInt("fila1");
				rs.next();
				if (rs.isAfterLast()) {
					rs.last();
				} else if (rs.isLast()) {
					rs.previous();
				} else if (!(codcem == rs.getInt("codcem") && codcuar == rs.getInt("codcuar")
						&& fila == rs.getInt("fila1"))) {
					document.add(paragraph);
					paragraph = new Paragraph();
					rs.previous();
				} else if (!(codcem == rs.getInt("codcem") && codcuar == rs.getInt("codcuar"))) {
					rs.previous();
				} else if (!(codcem == rs.getInt("codcem"))) {
					rs.previous();
				} else {
					rs.previous();
				}
			}
		}
		document.close();
//		pdfWriter.close();
		return docBytes.toByteArray();
	}

	private Text detalle(ResultSet rs2) throws SQLException {
		return new Text(rs2.getString("col2") + " ");
//		Paragraph().add(title);		
//		System.out.print(rs2.getString("col2") + ", ");
	}

	private void cabFila(ResultSet rs2) throws SQLException {
		Paragraph paragraph = new Paragraph("Fila:" + rs2.getString("fila2"));
		document.add(paragraph);
		paragraph = new Paragraph(" Nicho:");
//		System.out.print("Fila:" + rs2.getString("fila2"));
//		System.out.print(" Nicho:");
	}

	private void cabCuartel(ResultSet rs2) throws SQLException {
		String dato = "Cuartel:" + rs2.getInt("codcuar") + " - " + rs2.getString("nomcuar");
		Paragraph paragraph = new Paragraph(dato);
		document.add(paragraph);
	}

	private void cabCementerio(ResultSet rs2) throws SQLException {
		String dato = "Cementerio:" + rs2.getInt("codcem") + " - " + rs2.getString("nomcem");
		Paragraph paragraph = new Paragraph(dato);
		document.add(paragraph);
	}

}
