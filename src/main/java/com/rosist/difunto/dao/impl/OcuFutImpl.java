package com.rosist.difunto.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.dao.ClienteDao;
import com.rosist.difunto.dao.CuartelDao;
import com.rosist.difunto.dao.DifuntoDao;
import com.rosist.difunto.dao.OcuFutDao;
import com.rosist.difunto.dao.ParmaeDao;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cliente;
import com.rosist.difunto.modelSbp.Cuartel;
import com.rosist.difunto.modelSbp.Difunto;
import com.rosist.difunto.modelSbp.Nicho_t;
import com.rosist.difunto.modelSbp.Ocufut;
import com.rosist.difunto.modelSbp.Parmae;

@Repository
public class OcuFutImpl implements OcuFutDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private ClienteDao daoCliente;

	@Autowired
	private ParmaeDao daoParMae;

	private static final Logger logger = LoggerFactory.getLogger(OcuFutImpl.class);

	@Override
	public int getOcuFutCount() {
		String sql = "select count(*) as count from ocufut";
		int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nNumReg;
	}

	@Override
	public int getOcuFutCount(String condicion) {
		String sql = "select count(*) as count from ocufut where 1=1" + condicion;
		int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nNumReg;
	}

	@Override
	public int getNewIdOcuFut(int codcem) {
		String sql = "select ifnull(max(codocu),0)+1 from ocufut where codcem=" + codcem;
		int nCorrel = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nCorrel;
	}

	@Override
	public Ocufut insertaOcuFut(Ocufut ocufut) {
		int resp = 0, idof;
		System.out.println("guadando...");
//        System.out.println("antes de guardar -=> " + ocufut.toString());

		idof = getNewIdOcuFut(ocufut.getCementerio().getCodcem());
		ocufut.setCodocu(idof);
		String cUser = "usuario";
//        String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
		Date dUser = new Date();
		int xDifunto = 0;
		int xTransf = 0;
		String xCodNic = String.format("%1$01d", ocufut.getNicho().getFila1())
				+ String.format("%1$03d", ocufut.getNicho().getCol1());
//		System.out.println(" ocufut en inserta " + ocufut.toString());
		String xEstado = "10";
		String xEstvta = "00";
		String sql = "insert into ocufut(codocu, apepat, apemat, nombres, edad_a, edad_m, edad_d, sexo, tipdoccli, doccli, nomcli, codcem, codcuar, codnic, fila1, fila2, columna1, columna2, estado, observ, codtrn, coddif, recing, fecri, mtori, estvta, user, duser) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		resp = jdbcTemplate.update(sql,
				new Object[] { idof, ocufut.getApepat(), ocufut.getApemat(), ocufut.getNombres(), ocufut.getEdad_a(),
						ocufut.getEdad_m(), ocufut.getEdad_d(), ocufut.getSexo(),
						ocufut.getCliente().getTipdoccli().getCodigo(), ocufut.getCliente().getDoccli(),
						ocufut.getCliente().getNomcli(), ocufut.getCementerio().getCodcem(),
						ocufut.getCuartel().getCodcuar(), xCodNic, ocufut.getNicho().getFila1(),
						ocufut.getNicho().getFila2(), ocufut.getNicho().getCol1(), ocufut.getNicho().getCol2(),
						xEstado, ocufut.getObserv(), xTransf, xDifunto, ocufut.getRecing(),
						ocufut.getFecri(), ocufut.getMtori(), xEstvta, cUser, dUser });
		if (resp > 0) {

			Cliente xCliente = null;
			if (!ocufut.getCliente().getTipdoccli().getCodigo().equals("0")) {
				xCliente = daoCliente.buscaCliente(ocufut.getCliente().getTipdoccli().getCodigo(),
						ocufut.getCliente().getDoccli());
			}

			if (xCliente == null && !ocufut.getCliente().getTipdoccli().getCodigo().equals("0")) {
				sql = "insert into clientesunat(tipdoc, doccli, nomcli, dircli) values(?,?,?,?)";
				resp = jdbcTemplate.update(sql,
						new Object[] { ocufut.getCliente().getTipdoccli().getCodigo(), ocufut.getCliente().getDoccli(),
								ocufut.getCliente().getNomcli(), ocufut.getCliente().getDircli() });
			}

			int xFila1 = ocufut.getNicho().getFila1();
			String xFila2 = ocufut.getNicho().getFila2();
			int xCol1 = ocufut.getNicho().getCol1();
			int xCol2 = ocufut.getNicho().getCol2();
			int xCodCem = ocufut.getCementerio().getCodcem();
			int xCodCuar = ocufut.getCuartel().getCodcuar();
			String sqlNic = "update nicho_t set estado=? where codcem=? and codcuar=? and fila1=? and col1=?";
			resp = jdbcTemplate.update(sqlNic, new Object[] { "2", xCodCem, xCodCuar, xFila1, xCol1 });
			if (resp > 0) {
				String sqlNicE = "update nicho_e set col" + String.format("%1$03d", xCol1)
						+ "=? where codcem=? and codcuar=? and fila1=?";
				resp = jdbcTemplate.update(sqlNicE, new Object[] { "2", xCodCem, xCodCuar, xFila1 });
			}
		}
		return (resp>0?buscaOcuFut(ocufut.getCementerio().getCodcem(), ocufut.getCodocu()):null);
	}

	@Override
	public Ocufut modificaOcuFut(Ocufut ocufut) {
		int resp = 0;
		String sql = "update ocufut set apepat=?, apemat=?, nombres=?, edad_a=?, edad_m=?, edad_d=?, sexo=?, tipdoccli=?, doccli=?, nomcli=?, estado=?, observ=?, codtrn=?, coddif=?, recing=?, fecri=?, mtori=?, estvta=? where codcem=? and codocu=?";
		int xDifunto = 0;
		int xTransf = 0;
		ocufut.setNomocu(ocufut.getApepat() + " " + ocufut.getApemat() + ", " + ocufut.getNombres());
		System.out.println("ocufut en implement-=> " + ocufut.toString());
		resp = jdbcTemplate.update(sql,
				new Object[] { ocufut.getApepat(), ocufut.getApemat(), ocufut.getNombres(), ocufut.getEdad_a(),
						ocufut.getEdad_m(), ocufut.getEdad_d(), ocufut.getSexo(),
						ocufut.getCliente().getTipdoccli().getCodigo(), ocufut.getCliente().getDoccli(),
						ocufut.getCliente().getNomcli(), ocufut.getEstado().getCodigo(), ocufut.getObserv(), xTransf,
						xDifunto, ocufut.getRecing(), ocufut.getFecri(), ocufut.getMtori(),
						ocufut.getEstvta().getCodigo(), ocufut.getCementerio().getCodcem(), ocufut.getCodocu() });
		if (resp > 0) {
			Cliente xCliente = null;
			if (!ocufut.getCliente().getTipdoccli().getCodigo().equals("0")) {
				xCliente = daoCliente.buscaCliente(ocufut.getCliente().getTipdoccli().getCodigo(),
						ocufut.getCliente().getDoccli());
			}

			if (xCliente == null && !ocufut.getCliente().getTipdoccli().getCodigo().equals("0")) {
				sql = "insert into clientesunat(tipdoc, doccli, nomcli, dircli) values(?,?,?,?)";
				resp = jdbcTemplate.update(sql,
						new Object[] { ocufut.getCliente().getTipdoccli().getCodigo(), ocufut.getCliente().getDoccli(),
								ocufut.getCliente().getNomcli(), ocufut.getCliente().getDircli() });
			}
		}
		return (resp>0?buscaOcuFut(ocufut.getCementerio().getCodcem(), ocufut.getCodocu()):null);
	}

	@Override
	public int eliminaOcuFut(int codcem, int codocu) {
		////////////////////////////////////////////////////
		// O J O L E E R E S T O //
		// buscamos nichos con ese cuartel //
		// si NO se encuentran que proceda la emilinacion //
		////////////////////////////////////////////////////

		int resp = 0;
		Ocufut ocufut = buscaOcuFut(codcem, codocu);
		String sql = "delete from ocufut where codcem=? and codocu=?";
		resp = jdbcTemplate.update(sql, new Object[] { codcem, codocu });
		if (resp > 0) {
			int xFila1 = ocufut.getNicho().getFila1();
			String xFila2 = ocufut.getNicho().getFila2();
			int xCol1 = ocufut.getNicho().getCol1();
			int xCol2 = ocufut.getNicho().getCol2();
			int xCodCem = ocufut.getCementerio().getCodcem();
			int xCodCuar = ocufut.getCuartel().getCodcuar();
			String sqlNic = "update nicho_t set estado=? where codcem=? and codcuar=? and fila1=? and col1=?";
			resp = jdbcTemplate.update(sqlNic, new Object[] { "1", xCodCem, xCodCuar, xFila1, xCol1 });
			if (resp > 0) {
				String sqlNicE = "update nicho_e set col" + String.format("%1$03d", xCol1)
						+ "=? where codcem=? and codcuar=? and fila1=?";
				resp = jdbcTemplate.update(sqlNicE, new Object[] { "1", xCodCem, xCodCuar, xFila1 });
			}
		}
		return resp;
		// falta cambiar el estado del nicho a 1
	}

	@Override
	public Ocufut buscaOcuFut(int codcem, int codocu) {
		String sql = "SELECT of.codocu, CONCAT(TRIM(of.apepat),' ',TRIM(of.apemat),', ',TRIM(of.nombres)) nomocu, "
				+ "          of.apepat, of.apemat, of.nombres, of.edad_a, of.edad_m, of.edad_d, "
				+ "          of.sexo, of.codcem, cem.nomcem, cem.local, of.codcuar, cu.nomcuar, cu.tipcuar, "
				+ "          cu.filas, cu.columnas, cu.estado estcua, of.codnic, of.fila1, of.fila2, of.columna1, "
				+ "          of.columna2, of.estado, of.observ, e.descri desestado, of.coddif, d.fecfall, d.fecsep, d.estado estdif, of.codtrn, "
				+ "          of.coddif, of.recing, of.fecri, of.mtori, of.estvta, ev.descri desestvta, of.tipdoccli, of.doccli, of.nomcli, cl.dircli "
				+ "     FROM ocufut of LEFT JOIN cementerio cem ON of.codcem=cem.codcem "
				+ "                    LEFT JOIN parmae e  ON of.estado=e.codigo AND e.tipo='ESTOCF' "
				+ "                    LEFT JOIN parmae ev ON of.estado=ev.codigo AND ev.tipo='ESTVTA' "
				+ "                    LEFT JOIN cuartel cu ON of.codcem=cu.codcem AND of.codcuar=cu.codcuar "
				+ "                    LEFT JOIN difunto d ON of.codcem=d.codcem AND of.coddif=d.coddif "
				+ "                    left join clientesunat cl  on  cl.tipdoc=of.tipdoccli and cl.doccli=of.doccli "
				+ " where of.codcem=? and of.codocu=? ";

		logger.info("sql ocufut impl.-=> " + sql);
		List<Ocufut> matches = jdbcTemplate.query(sql, new RowMapper<Ocufut>() {
			@Override
			public Ocufut mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
				Cementerio cementerio = new Cementerio();
				cementerio.setCodcem(rs.getInt("codcem"));
				cementerio.setNomcem(rs.getString("nomcem"));
				cementerio.setLocal(rs.getBoolean("local"));

				Parmae clEstado = daoParMae.buscaParmae("ESTOCF", rs.getString("estado"), "");
				Parmae estvta = daoParMae.buscaParmae("ESTVTA", rs.getString("estvta"), "");

				Parmae tipcuar = daoParMae.buscaParmae("TIPCUA", rs.getString("tipcuar"), "");
				Parmae estCuartel = daoParMae.buscaParmae("ESTCUA", rs.getString("estcua"), "");
				Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoccli"), "", "");

				Cuartel cuartel = new Cuartel();
				cuartel.setCementerio(cementerio);
				cuartel.setCodcuar(rs.getInt("codcuar"));
				cuartel.setColumnas(rs.getInt("columnas"));
				cuartel.setFilas(rs.getInt("filas"));
				cuartel.setNomcuar(rs.getString("nomcuar"));
				cuartel.setTipcuar(tipcuar);
				cuartel.setEstado(estCuartel);

//                        Cuartel cuartel = servCuartel.buscaCuartel(rs.getInt("codcem"), rs.getInt("codcuar"));
				Difunto difunto = new Difunto();
				Parmae estadoDif = new Parmae();
				if (rs.getString("estdif")!=null) {
					estadoDif = daoParMae.buscaParmae("ESTDIF", rs.getString("estdif"), "");
				}

				Cliente cliente = null;
				String nomcli = "";

				if (!tipdoccli.getCodigo().equals("0")) {
					// System.out.println("doc cli " + rs.getInt("tipdoccli") + "-" +
					// rs.getString("doccli"));
					cliente = daoCliente.buscaCliente(rs.getString("tipdoccli"), rs.getString("doccli"));
				} else {
					cliente = new Cliente();
					cliente.setTipdoccli(tipdoccli);
					cliente.setNomcli(rs.getString("nomcli"));
				}
				nomcli = cliente.getNomcli();

				difunto.setCementerio(cementerio);
				difunto.setCoddif(rs.getInt("coddif"));
				difunto.setFecfall(rs.getString("fecfall"));
				difunto.setFecsep(rs.getString("fecsep"));
				
				difunto.setEstado(estadoDif);
				Nicho_t nicho = new Nicho_t();
				nicho.setFila1(rs.getInt("fila1"));
				nicho.setFila2(rs.getString("fila2"));
				nicho.setCol1(rs.getInt("columna1"));
				nicho.setCol2(rs.getInt("columna2"));

				Ocufut ocufut = new Ocufut();
				ocufut.setCementerio(cementerio);
				ocufut.setCodocu(rs.getInt("codocu"));
				ocufut.setNomocu(rs.getString("nomocu"));
				ocufut.setApepat(rs.getString("apepat"));
				ocufut.setApemat(rs.getString("apemat"));
				ocufut.setNombres(rs.getString("nombres"));
				ocufut.setEdad_a(rs.getInt("edad_a"));
				ocufut.setEdad_m(rs.getInt("edad_m"));
				ocufut.setEdad_d(rs.getInt("edad_d"));
				ocufut.setSexo(rs.getString("sexo"));
				ocufut.setCliente(cliente);
				ocufut.setNomcli(nomcli);
				ocufut.setCuartel(cuartel);
				ocufut.setNicho(nicho);
				ocufut.setDifunto(difunto);
				ocufut.setRecing(rs.getString("recing"));
				ocufut.setFecri(rs.getString("fecri"));
				ocufut.setMtori(rs.getDouble("mtori"));
				ocufut.setEstado(clEstado);
				ocufut.setObserv(rs.getString("observ"));
				ocufut.setEstvta(estvta);

				return ocufut;
			}
		}, new Object[] { codcem, codocu });

		return matches.size() > 0 ? (Ocufut) matches.get(0) : null;
	}

	@Override
	public List<Ocufut> listaOcuFut(String condicion, String limit, String orden) {
		String sql = "SELECT of.codocu, CONCAT(TRIM(of.apepat),' ',TRIM(of.apemat),', ',TRIM(of.nombres)) nomocu, of.apepat, of.apemat, of.nombres, of.edad_a, of.edad_m, of.edad_d, "
				+ "          of.sexo, of.codcem, cem.nomcem, cem.local, of.codcuar, cu.nomcuar, cu.tipcuar, "
				+ "          cu.filas, cu.columnas, cu.estado estcua, of.codnic, of.fila1, of.fila2, of.columna1, "
				+ "          of.columna2, of.estado, e.descri desestado, of.coddif, d.fecfall, d.fecsep, d.estado estdif, "
				+ "          of.codtrn, of.coddif, of.recing, of.fecri, of.mtori, of.estvta, of.observ, ev.descri desestvta, of.tipdoccli, of.doccli, of.nomcli, cl.dircli"
				+ "     FROM ocufut of LEFT JOIN cementerio cem ON of.codcem=cem.codcem"
				+ "                    LEFT JOIN parmae       e  ON of.estado=e.codigo AND e.tipo='ESTOCF'"
				+ "                    LEFT JOIN parmae       ev ON of.estado=ev.codigo AND ev.tipo='ESTVTA'"
				+ "                    LEFT JOIN cuartel      cu ON of.codcem=cu.codcem AND of.codcuar=cu.codcuar"
				+ "                    LEFT JOIN difunto      d  ON of.codcem=d.codcem AND of.coddif=d.coddif"
				+ "                    left join clientesunat cl on  cl.tipdoc=of.tipdoccli and cl.doccli=of.doccli"
				+ "    where 1=1 " + condicion;

		sql += (!orden.isEmpty() ? " order by " + orden: " ");
		sql += (!limit.isEmpty() ? limit : "  limit  0, 100 ");

		System.out.println("condicion++> " + sql);
		List<Ocufut> matches = jdbcTemplate.query(sql, new RowMapper<Ocufut>() {
			@Override
			public Ocufut mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
				Cementerio cementerio = new Cementerio();
				cementerio.setCodcem(rs.getInt("codcem"));
				cementerio.setNomcem(rs.getString("nomcem"));
				cementerio.setLocal(rs.getBoolean("local"));
				
				Parmae clEstado = daoParMae.buscaParmae("ESTOCF", rs.getString("estado"), "");
				Parmae estvta = daoParMae.buscaParmae("ESTVTA", rs.getString("estvta"), "");

				Parmae tipcuar = daoParMae.buscaParmae("TIPCUA", rs.getString("tipcuar"), "");
				Parmae estCuartel = daoParMae.buscaParmae("ESTCUA", rs.getString("estcua"), "");
				Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoccli"), "", "");
				Cuartel cuartel = new Cuartel();
				cuartel.setCementerio(cementerio);
				cuartel.setCodcuar(rs.getInt("codcuar"));
				cuartel.setColumnas(rs.getInt("columnas"));
				cuartel.setFilas(rs.getInt("filas"));
				cuartel.setNomcuar(rs.getString("nomcuar"));
				cuartel.setTipcuar(tipcuar);
				cuartel.setEstado(estCuartel);
				Difunto difunto = new Difunto();
				Parmae estadoDif = new Parmae();
				if (rs.getString("estdif")!=null) {
					estadoDif = daoParMae.buscaParmae("ESTDIF", rs.getString("estdif"), "");
				}
				difunto.setCementerio(cementerio);
				difunto.setCoddif(rs.getInt("coddif"));
				difunto.setFecfall(rs.getString("fecfall"));
				difunto.setFecsep(rs.getString("fecsep"));
				difunto.setEstado(estadoDif);
				Cliente cliente = null;
				String nomcli = "";

				if (!tipdoccli.getCodigo().equals("0")) {
					cliente = daoCliente.buscaCliente(rs.getString("tipdoccli"), rs.getString("doccli"));
				} else {
					cliente = new Cliente();
					cliente.setTipdoccli(tipdoccli);
					cliente.setNomcli(rs.getString("nomcli"));
				}
				nomcli = cliente.getNomcli();

				Nicho_t nicho = new Nicho_t();
				nicho.setFila1(rs.getInt("fila1"));
				nicho.setFila2(rs.getString("fila2"));
				nicho.setCol1(rs.getInt("columna1"));
				nicho.setCol2(rs.getInt("columna2"));

				Ocufut ocufut = new Ocufut();
				ocufut.setCementerio(cementerio);
				ocufut.setCodocu(rs.getInt("codocu"));
				ocufut.setNomocu(rs.getString("nomocu"));
				ocufut.setApepat(rs.getString("apepat"));
				ocufut.setApemat(rs.getString("apemat"));
				ocufut.setNombres(rs.getString("nombres"));
				ocufut.setEdad_a(rs.getInt("edad_a"));
				ocufut.setEdad_m(rs.getInt("edad_m"));
				ocufut.setEdad_d(rs.getInt("edad_d"));
				ocufut.setSexo(rs.getString("sexo"));
				ocufut.setCliente(cliente);
				ocufut.setNomcli(nomcli);
				ocufut.setCuartel(cuartel);
				ocufut.setNicho(nicho);
				ocufut.setDifunto(difunto);
				ocufut.setRecing(rs.getString("recing"));
				ocufut.setFecri(rs.getString("fecri"));
				ocufut.setMtori(rs.getDouble("mtori"));
				ocufut.setEstado(clEstado);
				ocufut.setObserv(rs.getString("observ"));
				ocufut.setEstvta(estvta);
				return ocufut;
			}
		}, new Object[] {});
		return matches.size() > 0 ? matches : null;
	}

	private Object getCampo(Object objeto, String campo, int tipo)
			throws IllegalArgumentException, InvocationTargetException {
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

	private void setCampo(Object objeto, String campo, Object dato, int tipo) {
		Class clase;
		Method metodoSet;
		Class[] clasesParamSet;
		Object[] paramSet;
		try {
			// Cargamos la clase
			clase = objeto.getClass();
			// Instanciamos un objeto de la clase
			try {
//              objeto = (Object)ejecucion;
				try {
					// Accedemos al metodo setEjeMes, con un parametro (Double) pra modificar
					clasesParamSet = new Class[1];
					switch (tipo) {
					case 1:
						clasesParamSet[0] = Class.forName("java.lang.String");
						break;
					case 2:
						clasesParamSet[0] = Class.forName("java.lang.Integer");
						break;
					case 3:
						clasesParamSet[0] = Class.forName("java.lang.Double");
						break;
					}
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
			} catch (IllegalAccessException e) {
				System.out.println("Error al instanciar el objeto. " + e);
			}
		} catch (ClassNotFoundException e) {
			System.out.println("No se ha encontrado la clase. " + e);
		}
	}
}