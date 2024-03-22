package com.rosist.difunto.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.rosist.difunto.modelSbp.Empresa;
import com.rosist.difunto.modelSbp.Sucursal;

public class XlsOcufut {

	private DriverManagerDataSource datasource;
	private Connection conexion;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql = "";
	
	private Empresa empresa;
	private Sucursal sucursal;

    private Workbook workbook;
    private Sheet sheet;
    private CellStyle headerStyle;
    private CellStyle style;
    private XSSFFont font;
    
	private String condicion = "";
	private String order = "";
	private Integer fila;
	int codcem = 0;
    private int nOcupanteCuartel;
    private int nOcupanteCementerio;
    
	static final Logger logger = LoggerFactory.getLogger(XlsMausoleo.class);

	public XlsOcufut(Map<String, Object> parametros) {
		datasource = (DriverManagerDataSource) parametros.get("datasource");
		condicion = (String) parametros.get("condicion");
//		order = (String) parametros.get("order");
		empresa   =   (Empresa)parametros.get("empresa");
		sucursal   =   (Sucursal)parametros.get("sucursal");
		fila = 0;
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
                + "    where 1=1 " + condicion
                + "    order by codcem,codcuar,columna1,fila1";

System.out.println("sql:" + sql);        
		conexion = datasource.getConnection();
        pstmt = conexion.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
      		  ResultSet.CONCUR_UPDATABLE);
		rs = pstmt.executeQuery ();
		
        ByteArrayOutputStream docBytes = new ByteArrayOutputStream();

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Mausoleos");
        
        plantilla();
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
				} else if (!(codcem == rs.getInt("codcem"))) {
					rs.previous();
					pieCementerio(rs);
				} else {
					rs.previous();
				}
            }
		}
        workbook.write(docBytes);
        workbook.close();

        return docBytes.toByteArray();
        
	}
	
	private void pieCementerio(ResultSet rs2) throws SQLException  {
		fila +=1;
        Row header = sheet.createRow(fila);
		
        String dato = "Total de Difuntos en Cementerio : " + rs2.getString("nomcem") + " " + nOcupanteCementerio;
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(dato);
        headerCell.setCellStyle(headerStyle);
		nOcupanteCementerio = 0;
	}

	private void pieCuartel(ResultSet rs2) throws SQLException {
		fila +=1;
        Row header = sheet.createRow(fila);
		
        String dato = "Total de Ocupantes en cuartel " + rs2.getString("nomcuar") + ": " + nOcupanteCuartel;
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(dato);
        headerCell.setCellStyle(headerStyle);
		nOcupanteCuartel = 0;
	}

	private void detalle(ResultSet rs2) throws SQLException {
		fila +=1;
        Row header = sheet.createRow(fila);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(String.valueOf(rs2.getInt("codocu")));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(1);
        headerCell.setCellValue(rs2.getString("nomocu"));
        headerCell.setCellStyle(style);
        
        String edad = rs.getString("edad_a") + "a " + rs.getString("edad_m") + "m " + rs.getString("edad_d") + "d" ;
        headerCell = header.createCell(2);
        headerCell.setCellValue(edad);
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(3);
        headerCell.setCellValue(rs2.getString("sexo"));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(4);
        headerCell.setCellValue(rs2.getString("nomcli"));
        headerCell.setCellStyle(style);
        
        String nicho = rs.getString("fila2") + "-" + rs.getString("columna2");
        headerCell = header.createCell(5);
        headerCell.setCellValue(nicho);
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(6);
        headerCell.setCellValue("desestado");
        headerCell.setCellStyle(style);
        
		nOcupanteCuartel++;
		nOcupanteCementerio++;
	}
	
	
	private void cabCuartel(ResultSet rs2) throws SQLException {
		fila +=1;
        Row header = sheet.createRow(fila);
		
    	String dato = "CUARTEL:" + rs2.getInt("codcuar") + rs2.getString("nomcuar");
        
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(dato);
        headerCell.setCellStyle(headerStyle);
        
		fila +=1;
        header = sheet.createRow(fila);
        headerCell = header.createCell(0);
        headerCell.setCellValue("ID");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(1);
        headerCell.setCellValue("OCUPANTE");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(2);
        headerCell.setCellValue("EDAD");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(3);
        headerCell.setCellValue("SEXO");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(4);
        headerCell.setCellValue("CLIENTE");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(5);
        headerCell.setCellValue("NICHO");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(6);
        headerCell.setCellValue("ESTADO");
        headerCell.setCellStyle(headerStyle);
        
	}

	private void cabCementerio(ResultSet rs2) throws SQLException {

		fila +=1;
        Row header = sheet.createRow(fila);

    	String dato = "CEMENTERIO:" + rs2.getInt("codcem") + rs2.getString("nomcem");
        
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(dato);
        headerCell.setCellStyle(headerStyle);
	}
	
	private void cabecera() {
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 2000);
        sheet.setColumnWidth(3, 8000);
        sheet.setColumnWidth(4, 8000);
        sheet.setColumnWidth(5, 8000);
        sheet.setColumnWidth(6, 8000);
        
        fila +=1;
        Row header = sheet.createRow(fila);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(empresa.getRazsoc());
//        headerCell.setCellValue(empresa.getRazsoc());
        headerCell.setCellStyle(headerStyle);
        
        fila +=1;
        header = sheet.createRow(fila);
        headerCell = header.createCell(0);
        headerCell.setCellValue(sucursal.getDescri());
//        headerCell.setCellValue(sucursal.getDescri());
        headerCell.setCellStyle(headerStyle);
        
        fila +=1;
        header = sheet.createRow(fila);
        headerCell = header.createCell(0);
        headerCell.setCellValue("REPORTE DE OCUPACIONES FUTURAS");
        headerCell.setCellStyle(headerStyle);
        
	}

	private void plantilla() {
        headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
        font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        headerStyle.setFont(font);
        
        style = workbook.createCellStyle();
        style.setWrapText(true);
	}
	
}
