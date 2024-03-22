package com.rosist.difunto.reports;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.rosist.difunto.modelSbp.Sucursal;

public class XlsMausoleo {

	private DriverManagerDataSource datasource;
	private Connection conexion;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private String sql = "";
	
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
	
	static final Logger logger = LoggerFactory.getLogger(XlsMausoleo.class);
	
	public XlsMausoleo(Map<String, Object> parametros) {
		datasource = (DriverManagerDataSource) parametros.get("datasource");
		condicion = (String) parametros.get("condicion");
		order = (String) parametros.get("order");
		sucursal   =   (Sucursal)parametros.get("sucursal");
		fila = 0;
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

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Mausoleos");        
        plantilla();
        cabecera();
        
        if (rs.first()){
        	rs.beforeFirst();
        	codcem = 0;
            while (rs.next()) {
				if (  (!(codcem == rs.getInt("codcem"))) ){     // || (linea==3)
					gCementerio(rs);
				}
				detalle(rs);
				codcem  =rs.getInt("codcem");
            }
        }

        workbook.write(docBytes);
        
        workbook.close();

        return docBytes.toByteArray();
	}
	
	private void cabecera() {
        Row cabecera = sheet.createRow(fila);
        
        Cell cabeceraCell = cabecera.createCell(0);
        cabeceraCell.setCellValue("REPORTE DE MAUSOLEOS");
        cabeceraCell.setCellStyle(headerStyle);
        fila += 1;
	}

	private void detalle(ResultSet rs2) throws SQLException {
		
        Row row = sheet.createRow(fila);
        
        Cell cell = row.createCell(0);
        cell.setCellValue(rs2.getInt("codmau"));
        cell.setCellStyle(style);
        
        cell = row.createCell(1);
        cell.setCellValue(rs2.getString("familia"));
        cell.setCellStyle(style);
        
        cell = row.createCell(2);
        cell.setCellValue(rs2.getString("destipmau"));
        cell.setCellStyle(style);
        
        cell = row.createCell(3);
        cell.setCellValue(rs2.getString("ubicacion"));
        cell.setCellStyle(style);
        
        cell = row.createCell(4);
        cell.setCellValue(rs2.getString("nomlote"));
        cell.setCellStyle(style);
        
        String cArea = "Adq: " + rs2.getString("area_adq") + " , Const: " + rs2.getString("area_cons") + " Cerc: " + rs2.getString("area_cerc");
        cell = row.createCell(5);
        cell.setCellValue(cArea);
        cell.setCellStyle(style);
        
        cell = row.createCell(6);
        cell.setCellValue(rs2.getString("observ"));
        cell.setCellStyle(style);
        fila += 1;
        
	}

	private void gCementerio(ResultSet rs2) throws SQLException {
    	
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 2000);
        sheet.setColumnWidth(3, 8000);
        sheet.setColumnWidth(4, 8000);
        sheet.setColumnWidth(5, 8000);
        sheet.setColumnWidth(6, 6000);
        
    	String dato = "CEMENTERIO:" + rs2.getInt("codcem") + rs2.getString("nomcem");
    	
        Row header = sheet.createRow(fila);
        
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Codigo");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(1);
        headerCell.setCellValue("Familia");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(2);
        headerCell.setCellValue("Tipo");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(3);
        headerCell.setCellValue("Ubicacion");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(4);
        headerCell.setCellValue("Lote");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(5);
        headerCell.setCellValue("Area");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(6);
        headerCell.setCellValue("Observación");
        headerCell.setCellStyle(headerStyle);
        
		fila += 2;
	}

	private void plantilla() throws IOException {
		
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
