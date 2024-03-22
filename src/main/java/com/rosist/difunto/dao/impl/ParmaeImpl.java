package com.rosist.difunto.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.dao.ParmaeDao;
import com.rosist.difunto.modelSbp.Parmae;

@Repository
public class ParmaeImpl implements ParmaeDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static Logger logger = LoggerFactory.getLogger(ParmaeImpl.class);
	@Override
	public int getParmaeCount() {
		String sql = "select count(*) as count from parmae";
		int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nNumReg;
	}

	@Override
	public int getParmaeCount(String condicion) {
		String sql = "select count(*) as count from parmae where 1=1 " + condicion;
		int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nNumReg;
	}

	@Override
	public Parmae insertaParmae(Parmae parmae) {
		int resp = 0;
		String sql = "insert into parmae(tipo, codigo, codigoaux, descri) values(?,?,?,?)";
		resp = jdbcTemplate.update(sql,
				new Object[] { parmae.getTipo(), parmae.getCodigo(), parmae.getCodigoaux(), parmae.getDescri() });
		return (resp>0?buscaParmae(parmae.getTipo(), parmae.getCodigo(), parmae.getCodigoaux()):null);
	}

	@Override
	public Parmae modificaParmae(Parmae parmae) {
		int resp = 0;
		String sql = "update parmae set descri=? where tipo=? and codigo=? and codigoaux =?";
		resp = jdbcTemplate.update(sql,
				new Object[] { parmae.getDescri(), parmae.getTipo(), parmae.getCodigo(), parmae.getCodigoaux() });
		return (resp>0?buscaParmae(parmae.getTipo(), parmae.getCodigo(), parmae.getCodigoaux()):null);
	}

	@Override
	public String eliminaParmae(String tipo, String codigo, String codaux) {
		int resp = 0;
		String mRet = "";

		if (mRet.isEmpty()) {
			String sql = "delete from parmae where 1=1 "
				      + (!tipo.equals("")? " and tipo='" + tipo + "'": "")
				      + (!codigo.equals("")? " and codigo='" + codigo + "'": "")
				      + (!codaux.equals("")? " and codaux='" + codaux + "'": "");
			
			resp = jdbcTemplate.update(sql, new Object[] { });
		}
		if (resp > 0) {
			mRet = "ok";
		}
		return mRet;
	}

	@Override
	public List<Parmae> listaParmae(String condicion, String limit, String order) {
		String sql = "select tipo, codigo, codigoaux, descri  from parmae where 1=1 "
                + (!condicion.isEmpty()? condicion: "")
                + (!order.isEmpty() ? " order by " + order : "")
                + (!limit.isEmpty() ? limit : "  limit  0, 100 ");
		
//logger.info("sql:" + sql);

		List<Parmae> matches = jdbcTemplate.query(sql, new RowMapper<Parmae>() {
			@Override
			public Parmae mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
				Parmae parmae = new Parmae();
				parmae.setTipo(rs.getString("tipo"));
				parmae.setCodigo(rs.getString("codigo"));
				parmae.setCodigoaux(rs.getString("codigoaux"));
				parmae.setDescri(rs.getString("descri"));
				return parmae;
			}
		}, new Object[] {});
		return matches.size() > 0 ? matches : null;
	}

	@Override
	public Parmae buscaParmae(String tipo, String codigo, String codaux) {
//		logger.info("tipo:" + tipo + " codigo:" + codigo + " codigoaux:" + codaux);
		String sql = "select tipo, codigo, codigoaux, descri from parmae"
				+ "    where 1=1 "
				+ (!tipo.isEmpty()?" and tipo='" + tipo + "'":"")
				+ (!codigo.isEmpty()?" and codigo='" + codigo + "'":"")
				+ (!codaux.isEmpty()?" and codigoaux='" + codaux + "'":"");
			//	"tipo=? and codigo=?"; // and codigoaux=?
//logger.info("sql:" + sql);		
		List<Parmae> matches = jdbcTemplate.query(sql, // ,codaux
				new RowMapper<Parmae>() {
					@Override
					public Parmae mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
						Parmae parmae = new Parmae();
						parmae.setTipo(rs.getString("tipo"));
						parmae.setCodigo(rs.getString("codigo"));
						parmae.setCodigoaux(rs.getString("codigoaux"));
						parmae.setDescri(rs.getString("descri"));
						return parmae;
					}
				}, new Object[] {});
		return matches.size() > 0 ? (Parmae) matches.get(0) : null;
	}

}