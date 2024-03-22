package com.rosist.difunto.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.dao.NichotDao;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cuartel;
import com.rosist.difunto.modelSbp.Nicho_t;
import com.rosist.difunto.modelSbp.Parmae;

@Repository
public class NichotImpl implements NichotDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int getNichotCount() {
        String sql = "select count(*) as count from cuartel";
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getNichotCount(String condicion) {
        String sql = "select count(*) as count from cuartel where 1=1 " + condicion;
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int insertaNichot(int codcem, int codcuar, Nicho_t nichot) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int modificaNichot(int codcem, int codcuar, Nicho_t nichot) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int eliminaNichot(int codcem, int codcuar, int fila1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Nicho_t buscaNichot(int codcem, int codcuar, int fila1, int col1) {
        String sql = "select codcem, codcuar, fila1, fila2, col1, col2, estado, e.descri desestado"
                + "   from nicho_t LEFT JOIN parmae e ON estado= e.codigo and e.tipo='ESTNIC'"
                + " where codcem=? and codcuar=? and fila1=? and col1=? ";
        System.out.println("SQL ==> " + sql);
        
        List<Nicho_t> matches = jdbcTemplate.query (sql,
                new RowMapper<Nicho_t>() {
                @Override
                    public Nicho_t mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Nicho_t nichos_t = new Nicho_t();
                        
                        Parmae estado = new Parmae();
                        estado.setTipo("ESTCUA");
                        estado.setCodigo(rs.getString("estado"));
                        estado.setDescri(rs.getString("desestado"));
                        
                        nichos_t.setFila1(rs.getInt("fila1"));
                        nichos_t.setFila2(rs.getString("fila2"));
                        nichos_t.setCol1(rs.getInt("col1"));
                        nichos_t.setCol2(rs.getInt("col2"));
                        nichos_t.setEstado(estado);
                        return nichos_t;
                    }
        }, new Object[] {codcem,codcuar,fila1, col1});
        return matches.size() > 0? (Nicho_t)matches.get(0): null;
    }
    
    @Override
    public List<Nicho_t> listaNichot(String condicion) {
        String sql = "select codcem, codcuar, fila1, fila2, col1, col2, estado, e.descri desestado"
                + "   from nicho_t LEFT JOIN parmae e ON estado= e.codigo and e.tipo='ESTNIC'"
                + " where 1=1 " + condicion;
        System.out.println("SQL Nichot ==> " + sql);
        
        List<Nicho_t> matches = jdbcTemplate.query (sql,
                new RowMapper<Nicho_t>() {
                @Override
                    public Nicho_t mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Nicho_t nichos_t = new Nicho_t();
                        
                        Parmae estado = new Parmae();
                        estado.setTipo("ESTCUA");
                        estado.setCodigo(rs.getString("estado"));
                        estado.setDescri(rs.getString("desestado"));
                        
                        nichos_t.setFila1(rs.getInt("fila1"));
                        nichos_t.setFila2(rs.getString("fila2"));
                        nichos_t.setCol1(rs.getInt("col1"));
                        nichos_t.setCol2(rs.getInt("col2"));
                        nichos_t.setEstado(estado);
                        return nichos_t;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }
    
    @Override
    public List<Nicho_t> listaFilas(int codcem, int codcuar) {
        String sql = "select distinct codcem, codcuar, fila1, fila2 " 
                + "     from nicho_t"
                + "    where codcem=? and codcuar=?";
        System.out.println("SQL ==> " + sql + " codcem " + codcem + " codcuar " + codcuar);
        
        List<Nicho_t> matches = jdbcTemplate.query (sql,
                new RowMapper<Nicho_t>() {
                @Override
                    public Nicho_t mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Nicho_t nichos_t = new Nicho_t();
                        nichos_t.setFila1(rs.getInt("fila1"));
                        nichos_t.setFila2(rs.getString("fila2"));
                        return nichos_t;
                    }
        }, new Object[] {codcem,codcuar});
        return matches.size() > 0? matches: null;
    }
    
    @Override
    public List<Nicho_t> listaFilasDisp(int codcem, int codcuar) {
        String sql = "select distinct codcem, codcuar, fila1, fila2 " 
                + "     from nicho_t"
                + "    where codcem=? and codcuar=? and (estado='1' or estado='4')";
        System.out.println("SQL ==> " + sql);
        
        List<Nicho_t> matches = jdbcTemplate.query (sql,
                new RowMapper<Nicho_t>() {
                @Override
                    public Nicho_t mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Nicho_t nichos_t = new Nicho_t();
                        nichos_t.setFila1(rs.getInt("fila1"));
                        nichos_t.setFila2(rs.getString("fila2"));
                        return nichos_t;
                    }
        }, new Object[] {codcem,codcuar});
        return matches.size() > 0? matches: null;
    }
    
    @Override
    public List<Nicho_t> listaResumen(int codcem, String cAno) {
        String sql = "SELECT n.codcem, c.nomcem, n.codcuar, cu.nomcuar, cu.tipcuar, cu.grupo, n.fila2, SUM(vf" + cAno + ") vfnum"
                + "     FROM nicho_t n LEFT JOIN cementerio c ON n.codcem=c.codcem"
                + "                    LEFT JOIN cuartel cu ON n.codcem=cu.codcem AND n.codcuar=cu.codcuar"
                + "    WHERE vf" + cAno + ">0 and c.codcem=?"
                + " GROUP BY grupo, codcem, codcuar, fila2";
        
        System.out.println("SQL NichoResumen ==> " + sql);
        List<Nicho_t> matches = jdbcTemplate.query (sql,
                new RowMapper<Nicho_t>() {
                @Override
                    public Nicho_t mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Cementerio cementerio = new Cementerio();
                        cementerio.setCodcem(rs.getInt("codcem"));
                        cementerio.setNomcem(rs.getString("nomcem"));
                        
                        Cuartel cuartel = new Cuartel();
                        cuartel.setCodcuar(rs.getInt("codcuar"));
                        cuartel.setNomcuar(rs.getString("nomcuar"));
                        cuartel.setGrupo(rs.getString("grupo"));
                        
                        Nicho_t nicho_t = new Nicho_t();
                        nicho_t.setCementerio(cementerio);
                        nicho_t.setCuartel(cuartel);
                        nicho_t.setFila2(rs.getString("fila2"));
                        nicho_t.setNum(rs.getInt("vfnum"));
//                        String campo = "vf" + cAno;
//                        setCampo(nicho_t,setColE,0,2);
                        
                        return nicho_t;
                    }
        }, new Object[] {codcem});
        return matches.size() > 0? matches: null;
    }
}