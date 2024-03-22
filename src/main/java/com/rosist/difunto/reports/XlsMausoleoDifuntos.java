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

public class XlsMausoleoDifuntos {

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
	
	static final Logger logger = LoggerFactory.getLogger(XlsMausoleo.class);

	public XlsMausoleoDifuntos(Map<String, Object> parametros) {
		datasource = (DriverManagerDataSource) parametros.get("datasource");
		condicion = (String) parametros.get("condicion");
		order = (String) parametros.get("order");
		empresa   =   (Empresa)parametros.get("empresa");
		sucursal   =   (Sucursal)parametros.get("sucursal");
		fila = 0;
	}
	
	public byte[] creaReporte() throws Exception {
		
        sql = "SELECT ma.codcem, cem.nomcem, cem.local, ma.codmau, ma.lotizado, ma.nomlote, "
                + "          ma.tipomau, p.descri destipmau, ma.ubicacion, ma.familia, ma.tipdoccli, ma.doccli, "
                + "          cl.nomcli, cl.doccli, ma.estado, ma.totdif, ma.numdif, ma.estvta, d.coddif, IFNULL(d.fecfall,'0001-01-01') fecfall,"
                + "          concat(trim(d.apepat),' ',trim(d.apemat),', ',trim(d.nombres)) nomdif"
                + "     FROM mausoleo ma LEFT JOIN cementerio cem ON ma.codcem=cem.codcem"
                + "                      left join difunto d on ma.codcem=d.codcem and ma.codmau=d.codmau and d.tipent=2"
                + "                      LEFT JOIN clientesunat cl ON ma.tipdoccli=cl.tipdoc and ma.doccli=cl.doccli"
                + "                      LEFT JOIN parmae p ON ma.tipomau=p.codigo AND p.tipo='TIPMAU'"
                + "                      LEFT JOIN parmae e ON ma.estado=p.codigo AND p.tipo='ESTMAU'"
                + "    where 1=1 " + condicion 
                + " order by " + order;
        
//        System.out.println(sql);
        
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
            int codmau = 0;
            while (rs.next()) {
				if (  (!(codcem == rs.getInt("codcem"))) ){     // || (linea==3)
					gCementerio(rs);
				}
				if (  (!(codcem == rs.getInt("codcem") && codmau == rs.getInt("codmau"))) ){     // || (linea==3)
					gMausoleo(rs);
				}
                detalle(rs);
				codcem = rs.getInt("codcem");
				codmau = rs.getInt("codmau");
				rs.next();
				if (rs.isAfterLast()) {
					rs.last();
				} else if (rs.isLast()) {
					rs.previous();
				} else if (!(codcem == rs.getInt("codcem") && codmau == rs.getInt("codmau")	)) {
//					document.add(paragraph);
//					paragraph = new Paragraph();
					rs.previous();
				} else if (!(codcem == rs.getInt("codcem"))) {
					rs.previous();
				} else {
					rs.previous();
				}
            }
		}
        workbook.write(docBytes);
        workbook.close();

        return docBytes.toByteArray();
        
	}

	private void detalle(ResultSet rs2) throws SQLException {
        fila +=1;
        Row header = sheet.createRow(fila);
        Cell headerCell = header.createCell(1);
        headerCell.setCellValue(rs2.getString("nomdif"));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(2);
        headerCell.setCellValue(rs2.getString("fecfall"));
        headerCell.setCellStyle(style);
	}

	private void gMausoleo(ResultSet rs2) throws SQLException {
        fila +=1;
        Row header = sheet.createRow(fila);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(rs2.getInt("codmau"));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(1);
        headerCell.setCellValue(rs2.getString("familia"));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(2);
        headerCell.setCellValue(rs2.getString("destipmau"));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(3);
        headerCell.setCellValue(rs2.getString("ubicacion"));
        headerCell.setCellStyle(style);
        
        headerCell = header.createCell(4);
        headerCell.setCellValue(rs2.getString("nomlote"));
        headerCell.setCellStyle(style);
	}

	private void gCementerio(ResultSet rs2) throws SQLException {
		
        fila +=1;
        Row header = sheet.createRow(fila);
        
    	String dato = "CEMENTERIO:" + rs2.getInt("codcem") + rs2.getString("nomcem");
        
        fila +=1;
        header = sheet.createRow(fila);
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(dato);
        headerCell.setCellStyle(headerStyle);
        
        fila +=1;
        header = sheet.createRow(fila);
        headerCell = header.createCell(0);
        headerCell.setCellValue("CODIGO");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(1);
        headerCell.setCellValue("FAMILIA");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(2);
        headerCell.setCellValue("TIPO");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(3);
        headerCell.setCellValue("UBICACION");
        headerCell.setCellStyle(headerStyle);
        
        headerCell = header.createCell(4);
        headerCell.setCellValue("LOTE");
        headerCell.setCellStyle(headerStyle);
        
	}

	private void cabecera() {
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 2000);
        sheet.setColumnWidth(3, 8000);
        sheet.setColumnWidth(4, 8000);
        
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
        headerCell.setCellValue("REPORTE DE MAUSOLEOS CON DIFUNTOS");
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
