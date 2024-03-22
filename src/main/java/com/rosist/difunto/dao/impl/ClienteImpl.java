package com.rosist.difunto.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.dao.ClienteDao;
import com.rosist.difunto.modelSbp.Cliente;
import com.rosist.difunto.modelSbp.Ingreso;
import com.rosist.difunto.modelSbp.Parmae;

@Repository
public class ClienteImpl implements ClienteDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final Logger logger = LoggerFactory.getLogger(ClienteImpl.class);

	@Override
	public int getClienteCount() {
		String sql = "select count(*) as count from clientesunat";
		int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nNumReg;
	}

	@Override
	public int getClienteCount(String condicion) {
		String sql = "select count(*) as count "
				+ "     from clientesunat c left join parmae td on td.tipo='TDCLI ' and td.codigo=c.tipdoc"
				+ "    where 1=1 "
                + (!condicion.isEmpty()? condicion: "");

		int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nNumReg;
	}

	@Override
	public int getNewIdCliente() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cliente insertaCliente(Cliente cliente) {
		int resp = 0;
		System.out.println("guadando...");
		String sql = "insert into clientesunat(tipdoc, doccli, nomcli, dircli) values(?,?,?,?)";

		resp = jdbcTemplate.update(sql, new Object[] { cliente.getTipdoccli().getCodigo(), cliente.getDoccli(),
				cliente.getNomcli(), cliente.getDircli() });
		return (resp>0?buscaCliente(cliente.getTipdoccli().getCodigo(), cliente.getDoccli()):null);
	}

	@Override
	public Cliente modificaCliente(Cliente cliente) {
		int resp = 0;
		String sql = "update clientesunat set nomcli=?, dircli=? where tipdoc=? and doccli=?";
		resp = jdbcTemplate.update(sql, new Object[] { cliente.getNomcli(), cliente.getDircli(),
				cliente.getTipdoccli().getCodigo(), cliente.getDoccli() });
		return (resp>0?buscaCliente(cliente.getTipdoccli().getCodigo(), cliente.getDoccli()):null);
	}

	@Override
	public String eliminaCliente(String tipdoccli, String doccli) {
		int resp = 0;
		String mRet = "";
		List<Ingreso> lIngreso = new ArrayList<>();
// poner esto cuando se cree el servicio de ingreso
//        List<Ingreso> lIngreso = servIngreso.listaIngresos(" and i.tipdoccli='" + tipdoccli + "' and i.doccli='" + doccli+"'","", "asc");
		if (lIngreso != null) {
			mRet = "Cliente ya existe en ingreso";
		}
//logger.info("eliminaCliente...antes de eliminar - tipdoccli:" + tipdoccli + " doccli:" + doccli + " mRet:" + mRet);
		if (mRet.isEmpty()) {
			String sql = "delete from clientesunat where tipdoc=? and doccli=?";
			resp = jdbcTemplate.update(sql, new Object[] { tipdoccli, doccli });
		}

		if (resp > 0) {
			mRet = "ok";
		}
		return mRet;
	}

	@Override
	public Cliente buscaCliente(String tipdoccli, String doccli) {
		String sql = "select c.tipdoc, td.descri destipdoc, c.doccli, c.nomcli, c.dircli"
				+ "  from clientesunat c left join parmae td on td.tipo='TDCLI ' and td.codigo=c.tipdoc "
				+ "where tipdoc=? and doccli=?";

//logger.info("sql Cliente:" + sql + " " + tipdoccli + " " + doccli);		
		List<Cliente> matches = jdbcTemplate.query(sql, new RowMapper<Cliente>() {
			@Override
			public Cliente mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
				Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoc"), "", rs.getString("destipdoc"));

				Cliente cliente = new Cliente();
				cliente.setTipdoccli(tipdoccli);
				cliente.setDoccli(rs.getString("doccli"));
				cliente.setNomcli(rs.getString("nomcli"));
				cliente.setDircli(rs.getString("dircli"));

				return cliente;
			}
		}, new Object[] { tipdoccli, doccli });
		return matches.size() > 0 ? (Cliente) matches.get(0) : null;
	}

	@Override
	public List<Cliente> listaClientes(String condicion, String limit, String orden) {
		String sql = "select c.tipdoc, td.descri destipdoc, c.doccli, c.nomcli, c.dircli"
				+ "  from clientesunat c left join parmae td on td.tipo='TDCLI ' and td.codigo=c.tipdoc " + " where 1=1 "
                + (!condicion.isEmpty()? condicion: "")
                + (!orden.isEmpty() ? " order by " + orden : "")
                + (!limit.isEmpty() ? limit : "");		// "  limit  0, 100 "

//logger.info("sql:" + sql);		
		
		List<Cliente> matches = jdbcTemplate.query(sql, new RowMapper<Cliente>() {
			@Override
			public Cliente mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
				Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoc"), "", rs.getString("destipdoc"));

				Cliente cliente = new Cliente();
				cliente.setTipdoccli(tipdoccli);
				cliente.setDoccli(rs.getString("doccli"));
				cliente.setNomcli(rs.getString("nomcli"));
				cliente.setDircli(rs.getString("dircli"));

				return cliente;
			}
		}, new Object[] {});
		return matches.size() > 0 ? matches : null;
	}

}
