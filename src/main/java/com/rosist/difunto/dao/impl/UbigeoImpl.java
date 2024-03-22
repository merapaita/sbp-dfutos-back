package com.rosist.difunto.dao.impl;

import com.rosist.difunto.modelSbp.Ubigeo;
import com.rosist.difunto.dao.UbigeoDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UbigeoImpl implements UbigeoDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int getUbigeoCount() {
        String sql = "select count(*) as count from ubigeo";
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getUbigeoCount(String condicion) {
        String sql = "select count(*) as count from ubigeo where 1=1 " + condicion;
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

//    @Override
//    public int getNewIdUbigeo() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public int insertaUbigeo(Ubigeo ubigeo) {
        int resp=0;
        System.out.println("guadando...");
        String sql = "insert into ubigeo(ubigeo, codcep, nomdep, codprv, nomprv, coddis, nomdis) values(?,?,?,?,?,?,?)";
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            ubigeo.getUbigeo(), ubigeo.getCoddep(), ubigeo.getNomdep(), ubigeo.getCodprv(), ubigeo.getNomprv(), ubigeo.getCoddis(), ubigeo.getNomdis()
                          }
        );
        return resp;
    }

    @Override
    public int modificaUbigeo(Ubigeo ubigeo) {
        int resp=0;
        String sql = "update ubigeo set nomdep=?, nomprv=?, nomdis=? where ubigeo=?";
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            ubigeo.getNomdep(), ubigeo.getNomprv(), ubigeo.getNomdis(), ubigeo.getUbigeo()
                          }
        );
        return resp;
    }

    @Override
    public int eliminaUbigeo(String ubigeo) {
        ////////////////////////////////////////////////////
        //      O J O      L E E R       E S T O          //
        // buscamos clientes con en mausoleos, difuntos, otros //
        // si NO se encuentran que proceda la emilinacion //
        ////////////////////////////////////////////////////
        
        int resp=0;
        String sql = "delete from ubigeo where ubigeo=?";
        resp = jdbcTemplate.update(sql, new Object[]{ubigeo});
        return resp;
    }

    @Override
    public Ubigeo buscaUbigeo(String ubigeo) {
        String sql = "select ubigeo, coddep, nomdep, codprv, nomprv, coddis, nomdis"
                +    " from ubigeo "
                +    "where ubigeo=?";

        List<Ubigeo> matches = jdbcTemplate.query (sql,
                new RowMapper<Ubigeo>() {
                @Override
                    public Ubigeo mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        
                        Ubigeo ubigeo = new Ubigeo();
                        ubigeo.setUbigeo(rs.getString("ubigeo"));
                        ubigeo.setCoddep(rs.getString("coddep"));
                        ubigeo.setNomdep(rs.getString("nomdep"));
                        ubigeo.setCodprv(rs.getString("codprv"));
                        ubigeo.setNomprv(rs.getString("nomprv"));
                        ubigeo.setCoddis(rs.getString("coddis"));
                        ubigeo.setNomdis(rs.getString("nomdis"));
                        
                        return ubigeo;
                    }
        }, new Object[] {ubigeo});
        return matches.size() > 0? (Ubigeo)matches.get(0): null;
    }

    @Override
    public List<Ubigeo> listaUbigeo(String condicion) {
        String sql = "select ubigeo, coddep, nomdep, codprv, nomprv, coddis, nomdis"
                + "from ubigeo "
                + " where 1=1" + condicion;
        
        List<Ubigeo> matches = jdbcTemplate.query (sql,
                new RowMapper<Ubigeo>() {
                @Override
                    public Ubigeo mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        
                        Ubigeo ubigeo = new Ubigeo();
                        ubigeo.setUbigeo(rs.getString("ubigeo"));
                        ubigeo.setCoddep(rs.getString("coddep"));
                        ubigeo.setNomdep(rs.getString("nomdep"));
                        ubigeo.setCodprv(rs.getString("codprv"));
                        ubigeo.setNomprv(rs.getString("nomprv"));
                        ubigeo.setCoddis(rs.getString("coddis"));
                        ubigeo.setNomdis(rs.getString("nomdis"));
                        
                        return ubigeo;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }
}