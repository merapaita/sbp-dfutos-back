package com.rosist.difunto.dao.impl;

import com.rosist.difunto.dao.CementerioDao;
import com.rosist.difunto.dao.CuartelDao;
import com.rosist.difunto.dao.MausoleoDao;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cuartel;
import com.rosist.difunto.modelSbp.Mausoleo;

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

/**
 *
 * @author Administrador
 */
@Repository
public class CementerioImpl implements CementerioDao {
	@Autowired
	private CuartelDao daoCuartel;

	@Autowired
	private MausoleoDao daoMausoleo;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final Logger logger = LoggerFactory.getLogger(CementerioImpl.class);
	
	@Override
	public int getCementerioCount() {
		String sql = "select count(*) as count from cementerio";
		int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nNumReg;
	}

	@Override
	public int getCementerioCount(String condicion) {
		String sql = "select count(*) as count from cementerio where 1=1 " + condicion;
		int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nNumReg;
	}

	@Override
	public int getNewCementerio() {
		String sql = "select ifnull(max(codcem),0)+1 from cementerio";
		int nCorrel = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nCorrel;
	}

	@Override
	public Cementerio insertaCementerio(Cementerio cementerio) {
//        int idCliente = 0, flag = 0;
		int resp = 0;
		System.out.println("guadando...");
		String sql = "insert into cementerio(codcem, nomcem) values(?,?)";
		int xCodCem = this.getNewCementerio();
		cementerio.setCodcem(xCodCem);
		resp = jdbcTemplate.update(sql, new Object[] { cementerio.getCodcem(), cementerio.getNomcem() });
		return (resp>0?buscaCementerio(cementerio.getCodcem()):null);
	}

	@Override
	public Cementerio modificaCementerio(Cementerio cementerio) {
		int resp = 0;
		String sql = "update cementerio set nomcem=? where codcem=?";
		resp = jdbcTemplate.update(sql, new Object[] { cementerio.getNomcem(), cementerio.getCodcem() });
		return (resp>0?buscaCementerio(cementerio.getCodcem()):null);
	}

	@Override
	public String eliminaCementerio(int codcem) throws Exception {
		int resp = 0;
		String mRet = "", sql = "";
		String cCondicion = " and cu.codcem=" + codcem;
		List<Cuartel> lCuartel = daoCuartel.listaCuartel(cCondicion, "", "");
		if (lCuartel != null) {
			mRet += " El Cementerio tiene cuarteles.";
		}
		cCondicion = " and ma.codcem=" + codcem;
		List<Mausoleo> lMausoleo = daoMausoleo.listaMausoleo(cCondicion, "", "");
//        System.out.println("mausoleos " + lMausoleo);
		if (lMausoleo != null) {
			mRet += " El Cementerio tiene Mausoleos";
		}
		if (!mRet.isEmpty()) {
			throw new Exception(mRet);
		}
		sql = "delete from cementerio where codcem=?";
		resp = jdbcTemplate.update(sql, new Object[] { codcem });
		if (resp > 0) {
			mRet = "ok";
		}
		return mRet;
	}

	@Override
	public Cementerio buscaCementerio(int codcem) {
		String sql = "select codcem, nomcem, local from cementerio where codcem=?";
		List<Cementerio> matches = jdbcTemplate.query(sql, new RowMapper<Cementerio>() {
			@Override
			public Cementerio mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
				Cementerio cementerio = new Cementerio();
				cementerio.setCodcem(rs.getInt("codcem"));
				cementerio.setNomcem(rs.getString("nomcem"));
				cementerio.setLocal(rs.getBoolean("local"));
				return cementerio;
			}
		}, new Object[] { codcem });
		return matches.size() > 0 ? (Cementerio) matches.get(0) : null;
	}

	@Override
	public List<Cementerio> listaCementerio(String condicion, String limit, String order) {
		String sql = "select codcem, nomcem, local from cementerio where 1=1 " + condicion
				+ (!order.isEmpty() ? " order by " + order : "")
				+ (!limit.isEmpty() ? limit : "");
		List<Cementerio> matches = jdbcTemplate.query(sql, new RowMapper<Cementerio>() {
			@Override
			public Cementerio mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
				Cementerio cementerio = new Cementerio();
				cementerio.setCodcem(rs.getInt("codcem"));
				cementerio.setNomcem(rs.getString("nomcem"));
				cementerio.setLocal(rs.getBoolean("local"));
				return cementerio;
			}
		}, new Object[] {});
		return matches.size() > 0 ? matches : null;
	}
}