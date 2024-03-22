package com.rosist.difunto.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.dao.IteIngDao;
import com.rosist.difunto.modelSbp.Iteing;
import com.rosist.difunto.modelSbp.Parmae;
import com.rosist.difunto.modelSbp.Partida;
import com.rosist.difunto.modelSbp.Servicio;

@Repository
public class IteIngImpl implements IteIngDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
    
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int getIteIngCount() {
        String sql = "select count(*) as count from iteing";
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getIteIngCount(String condicion) {
        String sql = "select count(*) as count from iteing where 1=1 " + condicion;
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int insertaIteIng(int iding, Iteing iteing) {
        int resp = 0;
        return resp;
    }
    
    @Override
    public int modificaIteIng(int iding, Iteing iteing) {
        int resp = 0;
        return resp;
    }

    @Override
    public int eliminaIteIng(int iding, int item) {
        int resp = 0;
        String sqlDet = "delete from iteing where iding=? and item=?";
        resp = jdbcTemplate.update(sqlDet,new Object[]{iding,item});
        return resp;
    }

    @Override
    public Iteing buscaIteIng(int iding, int item) {
        String sqlDet = "select ii.iding, ii.item, ii.tipser, ts.descri destipser, ii.idser, s.desser, "
                + "             ii.codpart, p.descri descodpart, ii.tipope, to.descri destipope, ii.mtosbpgr, "
                + "             ii.mtosbpex, ii.mtomed, ii.mtodonsbp, ii.mtodonmed, ii.total, ii.basimpgr, ii.basimpex, ii.igv, "
                + "             ii.liquida, ii.idliq, ii.user, ii.usercr, ii.duser, ii.dusercr "
                      + "  from iteing ii  left join parmae ts on p='TIPSER' and ts.codigo=ii.tipser"
                      + "                  left join parmae to on p='TIPOPE' and to.codigo=ii.tipope"
                      + "                  left join servicio s on s.idser=ii.idser"
                      + "                  left join partida p on p.codpart=ii.codpart"
                      + " where idoc=? and item=?";
        
        List<Iteing> detalles = jdbcTemplate.query(sqlDet,
                new RowMapper<Iteing>(){
                    public Iteing mapRow(ResultSet rsDet,int rowNumDet) throws SQLException,DataAccessException{
                        Parmae tipser  = new Parmae("TIPSER", rsDet.getString("tipser"), "", rsDet.getString("destipser"));
                        Parmae tipope  = new Parmae("TIPOPE", rsDet.getString("tipope"), "", rsDet.getString("destipope"));
                        Partida partida = new Partida(rsDet.getString("codpart"), rsDet.getString("descodpart"));
                        
                        int xCorrel = Integer.valueOf(rsDet.getString("idser").substring(2, 5));
                        Servicio servicio  = new Servicio(rsDet.getString("idser"), tipser, xCorrel, rsDet.getString("desser"), 0.00, 0.00, 0.00, 0.00, 0.00, partida, tipope);
                        
                        Iteing iteing = new Iteing();
                        iteing.setItem(rsDet.getInt("item"));
                        iteing.setTipser(tipser);
                        iteing.setServicio(servicio);
                        iteing.setPartida(partida);
                        iteing.setMtosbp(rsDet.getDouble("mtosbp"));
                        iteing.setTotal(rsDet.getDouble("total"));
                        iteing.setBasimp(rsDet.getDouble("basimp"));
                        iteing.setIgv(rsDet.getDouble("igv"));
                        iteing.setLiquida(rsDet.getInt("liquida"));
                        iteing.setIdliq(rsDet.getString("idliq"));
                        
                        return iteing;
                    }
                }, new Object[]{iding,item});
        return detalles.size() > 0? (Iteing)detalles.get(0): null;
    }

    @Override
    public List<Iteing> listaIteIng(int iding, String condicion) {
        String sqlDet = "select ii.iding, ii.item, ii.tipser, ts.descri destipser, ii.idser, s.desser, ii.codpart, "
                + "             p.descri descodpart, ii.tipope, to.descri destipope, ii.mtosbp, ii.total, ii.basimpgr, ii.basimpex, ii.igv, ii.liquida, ii.idliq, ii.user, ii.usercr, "
                + "             ii.duser, ii.dusercr "
                      + "  from iteing ii  left join parmae ts on p='TIPSER' and ts.codigo=ii.tipser"
                      + "                  left join parmae to on p='TIPOPE' and to.codigo=ii.tipope"
                      + "                  left join servicio s on s.idser=ii.idser"
                      + "                  left join partida p on p.codpart=ii.codpart"
                      + " where iding=? " + condicion ;
        
        System.out.println("sqlDet " + sqlDet);
        List<Iteing> detalles = jdbcTemplate.query(sqlDet,
                new RowMapper<Iteing>(){
                    public Iteing mapRow(ResultSet rsDet,int rowNumDet) throws SQLException,DataAccessException{
                        Parmae tipser  = new Parmae("TIPSER", rsDet.getString("tipser"), "", rsDet.getString("destipser"));
                        Parmae tipope  = new Parmae("TIPOPE", rsDet.getString("tipope"), "", rsDet.getString("destipope"));
                        Partida partida = new Partida(rsDet.getString("codpart"), rsDet.getString("descodpart"));
                        int xCorrel = Integer.valueOf(rsDet.getString("idser").substring(2, 5));
                        Servicio servicio  = new Servicio(rsDet.getString("idser"), tipser, xCorrel, rsDet.getString("desser"), 0.00, 0.00, 0.00, 0.00, 0.00, partida, tipope);

                        Iteing iteing = new Iteing();
                        iteing.setItem(rsDet.getInt("item"));
                        iteing.setTipser(tipser);
                        iteing.setServicio(servicio);
                        iteing.setPartida(partida);
                        iteing.setMtosbp(rsDet.getDouble("mtosbp"));
                        iteing.setTotal(rsDet.getDouble("total"));
                        iteing.setBasimp(rsDet.getDouble("basimp"));
                        iteing.setIgv(rsDet.getDouble("igv"));
                        iteing.setLiquida(rsDet.getInt("liquida"));
                        iteing.setIdliq(rsDet.getString("idliq"));
                        
                        return iteing;
                    }
                }, new Object[]{iding});
        return detalles.size()>0?(List<Iteing>)detalles:null;
    }
    
    @Override
    public List<Iteing> listaIteIng(String condicion) {
        String sqlDet = "select ii.iding, ii.item, ii.tipser, ts.descri destipser, ii.idser, s.desser, s.enable, ii.codpart, "
                + "             p.descri descodpart, ii.tipope, tope.descri destipope, ii.mtosbp, ii.total, ii.basimp, "
                + "             ii.igv, ii.liquida, ii.idliq, ii.user, ii.usercr, ii.duser, ii.dusercr "
                      + "  from iteing ii  left join parmae ts on ts.tipo='TIPSER' and ts.codigo=ii.tipser"
                      + "                  left join parmae tope on tope.tipo='TIPOPE' and tope.codigo=ii.tipope"
                      + "                  left join servicios s on s.idser=ii.idser"
                      + "                  left join partida p on p.codpart=ii.codpart"
                      + " where 1=1 " + condicion ;
        
        System.out.println("sqlDet " + sqlDet);
        List<Iteing> detalles = jdbcTemplate.query(sqlDet,
                new RowMapper<Iteing>(){
                    public Iteing mapRow(ResultSet rsDet,int rowNumDet) throws SQLException,DataAccessException{
                        Parmae tipser  = new Parmae("TIPSER", rsDet.getString("tipser"), "", rsDet.getString("destipser"));
                        Parmae tipope  = new Parmae("TIPOPE", rsDet.getString("tipope"), "", rsDet.getString("destipope"));
                        Partida partida = new Partida(rsDet.getString("codpart"), rsDet.getString("descodpart"));
                        int xCorrel = Integer.valueOf(rsDet.getString("idser").substring(2, 5));
                        Servicio servicio  = new Servicio(rsDet.getString("idser"), tipser, xCorrel, rsDet.getString("desser"), 0.00, 0.00, 0.00, 0.00, 0.00, partida, tipope);

                        Iteing iteing = new Iteing();
                        iteing.setItem(rsDet.getInt("item"));
                        iteing.setTipser(tipser);
                        iteing.setServicio(servicio);
                        iteing.setPartida(partida);
                        iteing.setMtosbp(rsDet.getDouble("mtosbp"));
                        iteing.setTotal(rsDet.getDouble("total"));
                        iteing.setBasimp(rsDet.getDouble("basimp"));
                        iteing.setIgv(rsDet.getDouble("igv"));
                        iteing.setLiquida(rsDet.getInt("liquida"));
                        iteing.setIdliq(rsDet.getString("idliq"));
                        
                        return iteing;
                    }
                }, new Object[]{});
        return detalles.size()>0?(List<Iteing>)detalles:null;
    }
}