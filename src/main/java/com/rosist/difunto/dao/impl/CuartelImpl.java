package com.rosist.difunto.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.dao.CuartelDao;
import com.rosist.difunto.dao.DifuntoDao;
import com.rosist.difunto.dao.NichonDao;
import com.rosist.difunto.dao.OcuFutDao;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cuartel;
import com.rosist.difunto.modelSbp.Difunto;
import com.rosist.difunto.modelSbp.Nicho_e;
import com.rosist.difunto.modelSbp.Nicho_f;
import com.rosist.difunto.modelSbp.Nicho_n;
import com.rosist.difunto.modelSbp.Ocufut;
import com.rosist.difunto.modelSbp.Parmae;
import com.rosist.difunto.reports.PdfCuartel;
import com.rosist.difunto.reports.PdfCuartelNichos;

/**
 *
 * @author Administrador
 */

@Repository
public class CuartelImpl implements CuartelDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DifuntoDao daoDifunto;

	@Autowired
	private OcuFutDao daoOcufut;

	@Autowired
	private NichonDao daoNichon;

    @Autowired
    private DriverManagerDataSource datasource;
    
	private static final Logger logger = LoggerFactory.getLogger(CuartelImpl.class);

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public int getCuartelCount() {
		String sql = "select count(*) as count from cuartel";
		int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nNumReg;
	}

	@Override
	public int getCuartelCount(String condicion) {
		String sql = "select count(*) as count from cuartel where 1=1 " + condicion;
		int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nNumReg;
	}

	@Override
	public int getNewIdCuartel(int codcem) {
		String sql = "select ifnull(max(codcuar),0)+1 from cuartel where codcem=" + codcem;
		int nCorrel = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nCorrel;
	}

	@Override
	public Cuartel insertaCuartel(Cuartel cuartel) {
		int resp = 0, filas = 0, columnas = 0;
		String strCol = "", strVal = "", sqlNic_N = "";

		String sql = "insert into cuartel(codcem, codcuar, nomcuar, tipcuar, filas, columnas, orden, estado) values(?,?,?,?,?,?,?,?)";
		int xCodCuar = this.getNewIdCuartel(cuartel.getCementerio().getCodcem());
		cuartel.setCodcuar(xCodCuar);
		String cEstado = "00";

		resp = jdbcTemplate.update(sql,
				new Object[] { cuartel.getCementerio().getCodcem(), cuartel.getCodcuar(), cuartel.getNomcuar(),
						cuartel.getTipcuar().getCodigo(), cuartel.getFilas(), cuartel.getColumnas(), cuartel.getOrden(),
						cEstado });

		if (resp > 0) {
			filas = cuartel.getFilas();
			columnas = cuartel.getColumnas();
			for (int i = 1; i <= columnas; i++) {
				strCol += " ,col" + String.format("%1$03d", i);
				strVal += ",?";
			}

			sqlNic_N = "insert into nicho_n(codcem, codcuar, fila1, fila2" + strCol + ")" + " values(?,?,?,?" + strVal
					+ ")";
			for (Nicho_n nichoN : cuartel.getNichos_n()) {
				getJdbcTemplate().update(sqlNic_N, new PreparedStatementSetter() {
					public void setValues(PreparedStatement stmt) throws SQLException {
						stmt.setInt(1, cuartel.getCementerio().getCodcem()); // codcem
						stmt.setInt(2, cuartel.getCodcuar()); // codcuar
						stmt.setInt(3, nichoN.getFila1()); // fila1
						stmt.setString(4, nichoN.getFila2()); // fila2
						for (int k = 1; k <= cuartel.getColumnas(); k++) {
							String getColN = "getCol" + String.format("%1$03d", k);
							int colN = (Integer) getCampo(nichoN, getColN, 2); // col00?
							stmt.setInt((k + 4), colN);
						}
					}
				});
			}

			String sqlNic_E = "insert into nicho_e(codcem, codcuar, fila1, fila2" + strCol + ")" + " values(?,?,?,?"
					+ strVal + ")";
			for (Nicho_n nichoN : cuartel.getNichos_n()) {
				getJdbcTemplate().update(sqlNic_E, new PreparedStatementSetter() {
					public void setValues(PreparedStatement stmt) throws SQLException {
						stmt.setInt(1, cuartel.getCementerio().getCodcem()); // codcem
						stmt.setInt(2, cuartel.getCodcuar()); // codcuar
						stmt.setInt(3, nichoN.getFila1()); // fila1
						stmt.setString(4, nichoN.getFila2()); // fila2
						for (int k = 1; k <= cuartel.getColumnas(); k++) {
							String getColN = "getCol" + String.format("%1$03d", k);
							int colN = (Integer) getCampo(nichoN, getColN, 2); // col00?
							stmt.setInt((k + 4), 1);
						}
					}
				});
			}

			String sqlNic_F = "insert into nicho_f(codcem, codcuar, fila1, fila2" + strCol + ")" + " values(?,?,?,?"
					+ strVal + ")";
			for (Nicho_f nichoF : cuartel.getNichos_f()) {
				getJdbcTemplate().update(sqlNic_F, new PreparedStatementSetter() {
					public void setValues(PreparedStatement stmt) throws SQLException {
						stmt.setInt(1, cuartel.getCementerio().getCodcem()); // codcem
						stmt.setInt(2, cuartel.getCodcuar()); // codcuar
						stmt.setInt(3, nichoF.getFila1()); // fila1
						stmt.setString(4, nichoF.getFila2()); // fila2
						for (int k = 1; k <= cuartel.getColumnas(); k++) {
							String getColF = "getCol" + String.format("%1$03d", k);
							int colN = 0; // valor col00?
//							int colN = (Integer) getCampo(nichoF, getColF, 2);	// col00?
							stmt.setInt((k + 4), colN);
						}
					}
				});
			}

			for (int i = 0; i < filas; i++) {
				for (int j = 0; j < cuartel.getColumnas(); j++) {
					String getColN = "getCol" + String.format("%1$03d", j + 1);
					int colN = (Integer) getCampo(cuartel.getNichos_n().get(i), getColN, 2);
					String sqlNic_T = "insert into nicho_t(codcem, codcuar, fila1, fila2, col1, col2, estado) values(?,?,?,?,?,?,?)";
					resp = jdbcTemplate.update(sqlNic_T,
							new Object[] { cuartel.getCementerio().getCodcem(), cuartel.getCodcuar(),
									cuartel.getNichos_n().get(i).getFila1(), cuartel.getNichos_n().get(i).getFila2(),
									(j + 1), colN, "1" });
				}
			}
		}
		return (resp>0?buscaCuartel(cuartel.getCementerio().getCodcem(), cuartel.getCodcuar()):null);
	}

	@Override
	public Cuartel modificaCuartel(Cuartel cuartel) {
		int resp = 0;
		String sql = "update cuartel set nomcuar=?, tipcuar=? where codcem=? and codcuar=?";
		System.out.println("cuartel en implementa -=> " + cuartel.toString());
		resp = jdbcTemplate.update(sql, new Object[] { cuartel.getNomcuar(), cuartel.getTipcuar().getCodigo(),
				cuartel.getCementerio().getCodcem(), cuartel.getCodcuar() });
		return (resp>0?buscaCuartel(cuartel.getCementerio().getCodcem(), cuartel.getCodcuar()):null);
	}

	@Override
	public String eliminaCuartel(int codcem, int codcuar) throws Exception {
		int resp = 0;
		String sql = "";
		String mRet = "";

		if (codcem==0) {
			mRet += " No se ha definido cementerio.";
		}
		if (codcuar==0) {
			mRet += " No se ha definido cuartel.";
		}
		String cCondicion = " and d.codcem=" + codcem + " and d.codcuar=" + codcuar;
		List<Difunto> lDifunto = daoDifunto.listaDifunto(cCondicion, "", "");
		if (lDifunto != null) {
			mRet += " El Cuartel ya tiene difuntos registrados.";
		}
		cCondicion += " and of.codcem=" + codcem + " and of.codcuar=" + codcuar;
		List<Ocufut> lOcuFut = daoOcufut.listaOcuFut(cCondicion,"","");
		if (lOcuFut != null) {
			mRet += " El Cuartel ya tiene ocupaciones futuras registrados.";
		}
		if (!mRet.isEmpty()) {
			throw new Exception(mRet);
		}
		sql = "delete from cuartel where codcem=? and codcuar=?";
		resp = jdbcTemplate.update(sql, new Object[] { codcem, codcuar });
		if (resp > 0) {
			sql = "delete from nicho_n where codcem=? and codcuar=?";
			resp = jdbcTemplate.update(sql, new Object[] { codcem, codcuar });
			sql = "delete from nicho_e where codcem=? and codcuar=?";
			resp = jdbcTemplate.update(sql, new Object[] { codcem, codcuar });
			sql = "delete from nicho_f where codcem=? and codcuar=?";
			resp = jdbcTemplate.update(sql, new Object[] { codcem, codcuar });
			sql = "delete from nicho_t where codcem=? and codcuar=?";
			resp = jdbcTemplate.update(sql, new Object[] { codcem, codcuar });
		}
		if (resp > 0) {
			mRet = "ok";
		}
		return mRet;
	}

	@Override
	public Cuartel buscaCuartel(int codcem, int codcuar) {
		String sql = "select cu.codcem, cem.nomcem, cem.local, cu.codcuar, cu.nomcuar, cu.tipcuar, p.descri destipcuar, cu.filas, cu.columnas, cu.orden, cu.estado, e.descri desestado"
				+ "   from cuartel cu left join cementerio cem on cu.codcem=cem.codcem"
				+ "                   left join parmae p on cu.tipcuar=p.codigo and p.tipo='TIPCUA'"
				+ "                   LEFT JOIN parmae e ON cu.estado=e.codigo AND e.tipo='ESTCUA'"
				+ " where cu.codcem=? and cu.codcuar=?";

		List<Cuartel> lCuartel = jdbcTemplate.query(sql, new RowMapper<Cuartel>() {
			@Override
			public Cuartel mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
				Cementerio cementerio = new Cementerio(rs.getInt("codcem"), rs.getString("nomcem"),
						rs.getBoolean("local"));
				Parmae clTipCuar = new Parmae("TIPCUA", rs.getString("tipcuar"), "", rs.getString("destipcuar"));
				Parmae clEstado = new Parmae("ESTCUA", rs.getString("estado"), "", rs.getString("desestado"));
				String strColN = "", strColE = "", sqlNicN = "", sqlNicE = "";

				Cuartel cuartel = new Cuartel();
				cuartel.setCementerio(cementerio);
				cuartel.setCodcuar(rs.getInt("codcuar"));
				cuartel.setNomcuar(rs.getString("nomcuar"));
				cuartel.setTipcuar(clTipCuar);
				cuartel.setFilas(rs.getInt("filas"));
				cuartel.setColumnas(rs.getInt("columnas"));
				cuartel.setEstado(clEstado);

				int columnas = cuartel.getColumnas();

				for (int i = 1; i <= columnas; i++) {
					strColN += " ,col" + String.format("%1$03d", i);
					strColE += " ,col" + String.format("%1$03d", i);
				}
				strColN += " from nicho_n ";
				strColE += " from nicho_e ";
				strColN += " where codcem=? and codcuar=?";
				strColE += " where codcem=? and codcuar=?";

				sqlNicN = "select codcem, codcuar, fila1, fila2" + strColN;
				sqlNicE = "select codcem, codcuar, fila1, fila2" + strColE;

				List<Nicho_n> lNichoN = jdbcTemplate.query(sqlNicN, new RowMapper<Nicho_n>() {
					@Override
					public Nicho_n mapRow(ResultSet rsNicN, int rowNum) throws SQLException, DataAccessException {
						Nicho_n nicho_n = new Nicho_n();
						nicho_n.setFila1(rsNicN.getInt("fila1"));
						nicho_n.setFila2(rsNicN.getString("fila2"));
						for (int k = 1; k <= columnas; k++) {
							String setColN = "setCol" + String.format("%1$03d", k); // antes estaba j
							String sValCam = "col" + String.format("%1$03d", k); // antes estaba j
							int nValCam = rsNicN.getInt(sValCam);
							setCampo(nicho_n, setColN, nValCam);
						}
						return nicho_n;
					}
				}, new Object[] { rs.getInt("codcem"), rs.getInt("codcuar") });
				cuartel.setNichos_n(lNichoN);

				List<Nicho_e> lNichoE = jdbcTemplate.query(sqlNicE, new RowMapper<Nicho_e>() {
					@Override
					public Nicho_e mapRow(ResultSet rsNicE, int rowNum) throws SQLException, DataAccessException {
						Nicho_e nicho_e = new Nicho_e();
						nicho_e.setFila1(rsNicE.getInt("fila1"));
						nicho_e.setFila2(rsNicE.getString("fila2"));
						for (int k = 1; k <= columnas; k++) {
							String setColE = "setCol" + String.format("%1$03d", k); // antes estaba j
							String sValCamE = "col" + String.format("%1$03d", k); // antes estaba j
							String nValCam = rsNicE.getString(sValCamE);
							setCampoS(nicho_e, setColE, nValCam);
						}
						return nicho_e;
					}
				}, new Object[] { rs.getInt("codcem"), rs.getInt("codcuar") });
				cuartel.setNichos_e(lNichoE);
				return cuartel;
			}
		}, new Object[] { codcem, codcuar });
		return lCuartel.size() > 0 ? (Cuartel) lCuartel.get(0) : null;
	}

	@Override
	public List<Cuartel> listaCuartel(String condicion, String limit, String order) {
		String sql = "select cu.codcem, cem.nomcem, cem.local, cu.codcuar, cu.nomcuar, cu.tipcuar, p.descri destipcuar, cu.filas, cu.columnas, cu.orden, cu.estado, e.descri desestado"
				+ "   from cuartel cu left join cementerio cem on cu.codcem=cem.codcem"
				+ "                   left join parmae p on cu.tipcuar=p.codigo and p.tipo='TIPCUA'"
				+ "                   LEFT JOIN parmae e ON cu.estado=e.codigo AND e.tipo='ESTCUA'" + " where 1=1 "
                + (!condicion.isEmpty()? condicion: "")
                + (!order.isEmpty() ? " order by " + order : "")
                + (!limit.isEmpty() ? limit : " ");	// limit  0, 100 

//		logger.info("listaCuartel...sql.-> " + sql);
		List<Cuartel> matches = jdbcTemplate.query(sql, new RowMapper<Cuartel>() {
			@Override
			public Cuartel mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
				Cementerio cementerio = new Cementerio();
				cementerio.setCodcem(rs.getInt("codcem"));
				cementerio.setNomcem(rs.getString("nomcem"));
				cementerio.setLocal(rs.getBoolean("local"));

				Parmae clTipCuar = new Parmae();
				clTipCuar.setTipo("TIPCUA");
				clTipCuar.setCodigo(rs.getString("tipcuar"));
				clTipCuar.setDescri(rs.getString("destipcuar"));

				Parmae clEstado = new Parmae();
				clEstado.setTipo("ESTCUA");
				clEstado.setCodigo(rs.getString("estado"));
				clEstado.setDescri(rs.getString("desestado"));

				Cuartel cuartel = new Cuartel();
				cuartel.setCementerio(cementerio);
				cuartel.setCodcuar(rs.getInt("codcuar"));
				String xx = rs.getString("nomcuar");
				cuartel.setNomcuar(xx);
				cuartel.setTipcuar(clTipCuar);
				cuartel.setEstado(clEstado);
				cuartel.setFilas(rs.getInt("filas"));
				cuartel.setColumnas(rs.getInt("columnas"));

				return cuartel;
			}
		}, new Object[] {});
		return matches.size() > 0 ? matches : null;
	}

	@Override
	public List<Cuartel> listaCuartelDisp(int codcem) {
		String sql = "select codcem, cu.codcuar, cu.nomcuar, cu.tipcuar, p.descri destipcuar, cu.filas, cu.columnas, cu.orden, cu.estado, e.descri desestado"
				+ "   from cuartel cu left join parmae p on cu.tipcuar=p.codigo and p.tipo='TIPCUA'"
				+ "                   LEFT JOIN parmae e ON cu.estado=e.codigo AND e.tipo='ESTCUA'"
				+ " where codcem=? and codcuar in (SELECT DISTINCT l.codcuar "
				+ "                                  FROM nicho_t l, cuartel c "
				+ "                                 WHERE l.codcem=c.codcem AND (l.estado='1' or l.estado='4'))"
				+ " ORDER BY codcem, nomcuar";

		List<Cuartel> matches = jdbcTemplate.query(sql, new RowMapper<Cuartel>() {
			@Override
			public Cuartel mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
				Parmae clTipCuar = new Parmae();
				clTipCuar.setTipo("TIPCUA");
				clTipCuar.setCodigo(rs.getString("tipcuar"));
				clTipCuar.setDescri(rs.getString("destipcuar"));

				Parmae clEstado = new Parmae();
				clEstado.setTipo("ESTCUA");
				clEstado.setCodigo(rs.getString("estado"));
				clEstado.setDescri(rs.getString("desestado"));

				Cuartel cuartel = new Cuartel();
				cuartel.setCodcuar(rs.getInt("codcuar"));
				cuartel.setNomcuar(rs.getString("nomcuar"));
				cuartel.setTipcuar(clTipCuar);
				cuartel.setEstado(clEstado);
				cuartel.setFilas(rs.getInt("filas"));
				cuartel.setColumnas(rs.getInt("columnas"));

				return cuartel;
			}
		}, new Object[] { codcem });
		return matches.size() > 0 ? matches : null;
	}

	@Override
	public byte[] reportePdf(int codcem, int codcuar, boolean bNichos) throws Exception {
		byte[] data = null;

		String condicion = "";
		if (codcem!=0)  condicion += " and codcem=" + codcem;
		if (codcuar!=0) condicion += " and codcuar=" + codcuar;

		Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("datasource", datasource);
		parametros.put("condicion", condicion);
		
		logger.info("parametros:" + parametros);		
		logger.info("bNichos:" + bNichos);		
		if (bNichos==false) {
	        PdfCuartel reporte = new PdfCuartel(parametros);
	        data = reporte.creaReporte();
		} else {
	        PdfCuartelNichos reporte = new PdfCuartelNichos(parametros);
	        data = reporte.creaReporte();
		}
		return data;
	}

	private String getCampo(Object objeto, String campo) {
		Class clase;
		Method metodoGet;
		String dato = "";
		// Cargamos la clase
		clase = objeto.getClass();
		// Instanciamos un objeto de la clase
		try {
//                objeto = (Object)ejecucion;
			try {
				// Accedemos al metodo getEjeMes, sin parametros
				metodoGet = clase.getMethod(campo, null);
				dato = (String) metodoGet.invoke(objeto, null);
			} catch (NoSuchMethodException e) {
				System.out.println("Error al acceder al metodo. " + e);
			} catch (SecurityException e) {
				System.out.println("Error al acceder al metodo. " + e);
			} catch (InvocationTargetException e) {
				System.out.println("Error al ejecutar el metodo. " + e);
			}
		} catch (IllegalAccessException e) {
			System.out.println("Error al instanciar el objeto. " + e);
		}
		return dato;
	}

	private void setCampoS(Object objeto, String campo, String dato) {
		Class clase;
//        Object objeto;
		Method metodoSet;
//        Double dato;
		Class[] clasesParamSet;
		Object[] paramSet;
		try {
			// Cargamos la clase
			clase = objeto.getClass();
//            clase = Class.forName("com.spring.bean.Ejecucion");
			// Instanciamos un objeto de la clase
			try {
//                objeto = (Object)ejecucion;
				try {
					// Accedemos al metodo setEjeMes, con un parametro (Double) pra modificar
					clasesParamSet = new Class[1];
					clasesParamSet[0] = Class.forName("java.lang.String");
					metodoSet = clase.getMethod(campo, clasesParamSet);
					paramSet = new Object[1];
					paramSet[0] = dato;
					metodoSet.invoke(objeto, paramSet);
				} catch (NoSuchMethodException e) {
					System.out.println("Error al acceder al metodo. " + e);
				} catch (SecurityException e) {
					System.out.println("Error al acceder al metodo. " + e);
				} catch (InvocationTargetException e) {
					System.out.println("Error al ejecutar el metodo. " + e);
				}
//            } catch (InstantiationException e) {
//                System.out.println("Error al instanciar el objeto. " + e);
			} catch (IllegalAccessException e) {
				System.out.println("Error al instanciar el objeto. " + e);
			}
		} catch (ClassNotFoundException e) {
			System.out.println("No se ha encontrado la clase. " + e);
		}
	}

	private void setCampo(Object objeto, String campo, Integer dato) {
		Class clase;
		Method metodoSet;
		Class[] clasesParamSet;
		Object[] paramSet;
		try {
			// Cargamos la clase
			clase = objeto.getClass();
			// clase = Class.forName("com.spring.bean.Ejecucion");
			// Instanciamos un objeto de la clase
			try {
				// objeto = (Object)ejecucion;
				try {
					// Accedemos al metodo setEjeMes, con un parametro (Double) pra modificar
					clasesParamSet = new Class[1];
					clasesParamSet[0] = Class.forName("java.lang.Integer");
					metodoSet = clase.getMethod(campo, clasesParamSet);
					paramSet = new Object[1];
					////////////////////
					paramSet[0] = dato;
					///////////////////
					metodoSet.invoke(objeto, paramSet);
				} catch (NoSuchMethodException e) {
					System.out.println("Error al acceder al metodo. " + e);
				} catch (SecurityException e) {
					System.out.println("Error al acceder al metodo. " + e);
				} catch (InvocationTargetException e) {
					System.out.println("Error al ejecutar el metodo. " + e);
				}
				// } catch (InstantiationException e) {
				// System.out.println("Error al instanciar el objeto. " + e);
			} catch (IllegalAccessException e) {
				System.out.println("Error al instanciar el objeto. " + e);
			}
		} catch (ClassNotFoundException e) {
			System.out.println("No se ha encontrado la clase. " + e);
		}
	}

	private Object getCampo(Object objeto, String campo, int tipo) {
		Class clase;
		Method metodoGet;
		Object dato = null;
		// Cargamos la clase
		clase = objeto.getClass();
		// Instanciamos un objeto de la clase
		try {
//            objeto = (Object)ejecucion;
			try {
				// Accedemos al metodo getEjeMes, sin parametros
				metodoGet = clase.getMethod(campo, null);
				switch (tipo) {
				case 1:
					dato = (String) metodoGet.invoke(objeto, null);
					break;
				case 2:
					dato = (Integer) metodoGet.invoke(objeto, null);
					break;
				case 3:
					dato = (Double) metodoGet.invoke(objeto, null);
					break;
				}
			} catch (NoSuchMethodException e) {
				System.out.println("Error al acceder al metodo. " + e);
			} catch (SecurityException e) {
				System.out.println("Error al acceder al metodo. " + e);
			} catch (InvocationTargetException e) {
				System.out.println("Error al ejecutar el metodo. " + e);
			}
		} catch (IllegalAccessException e) {
			System.out.println("Error al instanciar el objeto. " + e);
		}
		return dato;
	}

}