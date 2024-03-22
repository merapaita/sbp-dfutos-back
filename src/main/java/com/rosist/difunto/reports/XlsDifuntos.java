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

public class XlsDifuntos {

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
    private int nDifuntoCuartel;
    private int nDifuntoCementerio;
	
	static final Logger logger = LoggerFactory.getLogger(XlsMausoleo.class);

	public XlsDifuntos(Map<String, Object> parametros) {
		datasource = (DriverManagerDataSource) parametros.get("datasource");
		condicion = (String) parametros.get("condicion");
		order = (String) parametros.get("order");
		empresa   =   (Empresa)parametros.get("empresa");
		sucursal   =   (Sucursal)parametros.get("sucursal");
		fila = 0;
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
		
        String dato = "Total de Difuntos en Cementerio : " + rs2.getString("nomcem") + " " + nDifuntoCementerio;
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(dato);
        headerCell.setCellStyle(headerStyle);
		nDifuntoCementerio = 0;
	}

	private void pieCuartel(ResultSet rs2) throws SQLException {
		fila +=1;
        Row header = sheet.createRow(fila);
		
        String dato = "Total de Difuntos en cuartel " + rs2.getString("nomcuar") + ": " + nDifuntoCuartel;
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(dato);
        headerCell.setCellStyle(headerStyle);
		nDifuntoCuartel = 0;
	}

	private void detalle(ResultSet rs2) throws SQLException {
		fila +=1;
        Row header = sheet.createRow(fila);
		
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(String.valueOf(rs2.getInt("codcem")));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(1);
        headerCell.setCellValue(rs2.getString("fecfall"));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(2);
        headerCell.setCellValue(rs2.getString("fecsep"));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(3);
        headerCell.setCellValue(rs2.getString("nomdif"));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(4);
        headerCell.setCellValue(rs2.getString("sexo"));
        headerCell.setCellStyle(style);
        
        String edad = rs2.getInt("edad_a") + "a. - " + rs2.getInt("edad_m") + "m. - " + rs2.getInt("edad_d") + "d.";
        headerCell = header.createCell(5);
        headerCell.setCellValue(edad);
        headerCell.setCellStyle(style);
        
        String nicho = rs2.getString("fila2") + "-" + rs2.getInt("columna2");
        headerCell = header.createCell(6);
        headerCell.setCellValue(nicho);
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(7);
        headerCell.setCellValue(String.valueOf(rs2.getInt("codmau")));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(8);
        headerCell.setCellValue(rs2.getString("desestado"));
        headerCell.setCellStyle(style);
        
		nDifuntoCuartel++;
		nDifuntoCementerio++;
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
        headerCell.setCellValue("F.Fall");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(2);
        headerCell.setCellValue("F.Sep");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(3);
        headerCell.setCellValue("Difunto");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(4);
        headerCell.setCellValue("Sexo");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(5);
        headerCell.setCellValue("Edad");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(6);
        headerCell.setCellValue("Nicho");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(7);
        headerCell.setCellValue("Mausoleo");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(8);
        headerCell.setCellValue("Estado");
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
        sheet.setColumnWidth(7, 8000);
        sheet.setColumnWidth(8, 8000);
        
        fila +=1;
        Row header = sheet.createRow(fila);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("empresa");
//        headerCell.setCellValue(empresa.getRazsoc());
        headerCell.setCellStyle(headerStyle);
        
        fila +=1;
        header = sheet.createRow(fila);
        headerCell = header.createCell(0);
        headerCell.setCellValue("rz");
//        headerCell.setCellValue(sucursal.getDescri());
        headerCell.setCellStyle(headerStyle);
        
        fila +=1;
        header = sheet.createRow(fila);
        headerCell = header.createCell(0);
        headerCell.setCellValue("REPORTE DE DIFUNTOS");
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