package com.rosist.difunto.dao.impl;

import com.rosist.difunto.modelSbp.Partida;
import com.rosist.difunto.dao.PartidaDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PartidaImpl implements PartidaDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
//    @Override
//    public int getNewIdPartida() {
//        int nCorrel = this.jdbcTemplate.queryForInt("select ifnull(max(idmed),0)+1 from partidas");
//        return nCorrel;
//    }
    
    @Override
    public int getPartidaCount() {
        String sql = "select count(*) as count from partida";
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
	public int getPartidaCount(String codpart, String descri) {
		String sql = "select count(*) as count from partida where 1=1 "
                + (!codpart.isEmpty()? " and codpart like '" + codpart + "%'": "")
                + (!descri.isEmpty()?  " and descri  like '%" + descri + "%'": "");

		int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
		return nNumReg;
	}

    @Override
    public Partida insertaPartida(Partida partida) {
        int resp=0;
        System.out.println("guadando...");
        String sql = "insert into partida(codpart, descri) values(?,?)";
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            partida.getCodpart(), partida.getDescri()
                          }
        );
        return (resp>0?buscaPartida(partida.getCodpart()):null);
    }
    
    @Override
    public Partida modificaPartida(Partida partida) {
        int resp=0;
        String sql = "update partida set descri=? where codpart=?";
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            partida.getDescri(),partida.getCodpart()
                          }
        );
		return (resp>0?buscaPartida(partida.getCodpart()):null);
    }

    @Override
    public int eliminaPartida(String codpart) {
        ////////////////////////////////////////////////////
        //      O J O      L E E R       E S T O          //
        // buscamos clientes con en mausoleos, difuntos, otros //
        // si NO se encuentran que proceda la emilinacion //
        ////////////////////////////////////////////////////
        
        int resp=0;
        String sql = "delete from partida where codpart=?";
        resp = jdbcTemplate.update(sql, new Object[]{codpart});
        return resp;
    }

    @Override
    public Partida buscaPartida(String codpart) {
        String sql = "select codpart, descri from partida where codpart=?";
        List<Partida> matches = jdbcTemplate.query (sql,
                new RowMapper<Partida>() {
                @Override
                    public Partida mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Partida partida = new Partida();
                        partida.setCodpart(rs.getString("codpart"));
                        partida.setDescri(rs.getString("descri"));
                        
                        return partida;
                    }
        }, new Object[] {codpart});
        return matches.size() > 0? (Partida)matches.get(0): null;
    }
    
//    @Override
//    public List<Partida> listaPartidas(String condicion) {
//        String sql = "select codpart, descri "
//                     + "from partida "
//                     + " where 1=1" + condicion;
//        
//        List<Partida> matches = jdbcTemplate.query (sql,
//                new RowMapper<Partida>() {
//                @Override
//                    public Partida mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
//                        Partida partida = new Partida();
//                        partida.setCodpart(rs.getString("codpart"));
//                        partida.setDescri(rs.getString("descri"));
//                        
//                        return partida;
//                    }
//        }, new Object[] {});
//        return matches.size() > 0? matches: null;
//    }
    
    @Override
	public List<Partida> listaPartidas(String codpart, String descri, Integer page, Integer size) {
		String sql = "select codpart, descri " + "from partida " + " where 1=1 "
                + (!codpart.isEmpty()? " and codpart like '" + codpart + "%'": "")
                + (!descri.isEmpty()?  " and descri  like '%" + descri + "%'": "")
                + (page>-1?"limit " + page*size + ", " + size:"");
        
        List<Partida> matches = jdbcTemplate.query (sql,
                new RowMapper<Partida>() {
                @Override
                    public Partida mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Partida partida = new Partida();
                        partida.setCodpart(rs.getString("codpart"));
                        partida.setDescri(rs.getString("codpart") + " " + rs.getString("descri"));
                        
                        return partida;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }
}