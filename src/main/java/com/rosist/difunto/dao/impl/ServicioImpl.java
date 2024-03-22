package com.rosist.difunto.dao.impl;

import com.rosist.difunto.modelSbp.Iteing;
import com.rosist.difunto.modelSbp.Parmae;
import com.rosist.difunto.modelSbp.Partida;
import com.rosist.difunto.modelSbp.Servicio;
import com.rosist.difunto.reports.PdfServicio;
import com.rosist.difunto.dao.ServicioDao;
import com.rosist.difunto.dao.IteIngDao;
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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Marco
 */
@Repository
public class ServicioImpl implements ServicioDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DriverManagerDataSource datasource;
    
    @Autowired
    private IteIngDao daoIteIng;
    
    private static final Logger logger = LoggerFactory.getLogger(ServicioImpl.class);
    
    @Override
    public int getServicioCount() {
        String sql = "select count(*) as count from servicios";
        return this.jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
	public int getServicioCount(String idser, String tipser, String desser) {
		String sql = "select count(*) as count"
				+ "     from servicios s left join parmae ts on ts.tipo='TIPSER' AND ts.codigo=s.tipser"
				+ "                      left join partida p on s.codpart=p.codpart" + " where 1=1 "
                + (!idser.isEmpty()? " and idser='" + idser + "'": "")
                + (!tipser.isEmpty()? " and tipser='" + tipser + "'": "")
                + (!desser.isEmpty()? " and desser like '%" + desser + "%'": "");
        return this.jdbcTemplate.queryForObject(sql, Integer.class);
    }

    @Override
    public int getNewIdServicio(String tipser) {
        int nCorrel = 0;
        String sql = "select ifnull(max(correl),0)+1 from servicios where tipser="+tipser;
        nCorrel = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nCorrel;
    }

    @Override
    public Servicio insertaServicio(Servicio servicio) {
        int resp=0;
        System.out.println("guadando...");
        String sql = "insert into servicios(idser, tipser, correl, desser, mtosbp, mtotot, codpart, tipope) values(?,?,?,?,?,?,?,?)";
        int xCorrel = getNewIdServicio(servicio.getTipser().getCodigo());
        servicio.setCorrel(xCorrel);
        servicio.setIdser(servicio.getTipser().getCodigo()+String.format("%1$03d",xCorrel));
        
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            servicio.getIdser(), servicio.getTipser().getCodigo(), servicio.getCorrel(), servicio.getDesser(), servicio.getMtosbp(), servicio.getMtotot(), servicio.getPartida().getCodpart(), servicio.getTipoOperacion().getCodigo()
                          });
		return (resp>0?buscaServicio(servicio.getIdser()):null);
    }
    
    @Override
    public Servicio modificaServicio(Servicio servicio) {
        int resp=0;
        String sql = "update servicios set desser=?, mtosbp=?, mtotot=?, codpart=?, tipope=? where idser=?";
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            servicio.getDesser(), servicio.getMtosbp(), servicio.getMtotot(), servicio.getPartida().getCodpart(), servicio.getTipoOperacion().getCodigo(), servicio.getIdser()
                          }
        );
		return (resp>0?buscaServicio(servicio.getIdser()):null);
    }

    @Override
    public String eliminaServicio(String idser) {
        int resp=0;
        String mRet = "", sql ="";
        String cCondicion = " and ii.idser=" + idser;
        List<Iteing> lIteIng = daoIteIng.listaIteIng(cCondicion);
        if (lIteIng!=null) {
            mRet = "El Servicio ya esta utilizado";
        }

        if (mRet.isEmpty()){
            sql = "delete from servicios where idser=?";
            resp = jdbcTemplate.update(sql, new Object[]{idser});
        }
        if (resp>0){
            mRet = "ok";
        }
        return mRet;
    }

    @Override
    public Servicio buscaServicio(String idser) {
        String sql = "select s.idser, s.tipser, s.correl, ts.descri destipser, s.desser, s.mtosbp,"
                + "          s.mtotot, s.codpart, p.descri descodpart, tipope, top.descri destipope "
                + "     from servicios s left join parmae ts on ts.tipo='TIPSER' AND ts.codigo=s.tipser"
                + "                      left join parmae top on top.tipo='TIPOPE' AND top.codigo=s.tipope"
                + "                      left join partida p on s.codpart=p.codpart"
                + "    where s.idser=?";
        //                   idser, tipser, desser, mtomed, mtosbp, mtotot, tartra, codpart
        List<Servicio> matches = jdbcTemplate.query (sql,
                new RowMapper<Servicio>() {
                @Override
                    public Servicio mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Parmae tipser  = new Parmae("TIPSER", rs.getString("tipser"), "", rs.getString("destipser"));
                        Parmae tipope  = new Parmae("TIPOPE", rs.getString("tipope"), "", rs.getString("destipope"));
                        Partida partida = new Partida(rs.getString("codpart"), rs.getString("descodpart"));
                        
                        Servicio servicio = new Servicio();
                        servicio.setIdser(rs.getString("idser"));
                        servicio.setTipser(tipser);
                        servicio.setTipoOperacion(tipope);
                        servicio.setCorrel(rs.getInt("correl"));
                        servicio.setDesser(rs.getString("desser"));
                        servicio.setMtosbp(rs.getDouble("mtosbp"));
                        servicio.setMtotot(rs.getDouble("mtotot"));
                        servicio.setPartida(partida);
                        
                        return servicio;
                    }
        }, new Object[] {idser});
        return matches.size() > 0? (Servicio)matches.get(0): null;
    }

    @Override
	public List<Servicio> listaServicios(String idser, String tipser, String desser, Integer page, Integer size) {
        String sql = "select s.idser, s.tipser, s.correl, ts.descri destipser, s.desser, s.mtosbp, s.mtotot, "
                + "          s.codpart, p.descri descodpart, tipope, top.descri destipope "
                + "     from servicios s left join parmae ts on ts.tipo='TIPSER' AND ts.codigo=s.tipser"
                + "                      left join parmae top on top.tipo='TIPOPE' AND top.codigo=s.tipope"
                + "                      left join partida p on s.codpart=p.codpart"
				+ "    where 1=1 "
                + (!idser.isEmpty()? " and idser='" + idser + "'": "")
                + (!tipser.isEmpty()? " and tipser='" + tipser + "'": "")
                + (!desser.isEmpty()? " and desser like '%" + desser + "%'": "")
                + " order by desser asc "
                + (page>-1 ? " limit " + page + ", " + size: "");
        
        List<Servicio> matches = jdbcTemplate.query (sql,
                new RowMapper<Servicio>() {
                @Override
                    public Servicio mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Parmae tipser = new Parmae("TIPSER", rs.getString("tipser"), "", rs.getString("destipser"));
                        Parmae tipope  = new Parmae("TIPOPE", rs.getString("tipope"), "", rs.getString("destipope"));
                        Partida partida = new Partida(rs.getString("codpart"), rs.getString("descodpart"));
                        
                        Servicio servicio = new Servicio();
                        servicio.setIdser(rs.getString("idser"));
                        servicio.setTipser(tipser);
                        servicio.setTipoOperacion(tipope);
                        servicio.setCorrel(rs.getInt("correl"));
                        servicio.setDesser(rs.getString("desser"));
                        servicio.setMtosbp(rs.getDouble("mtosbp"));
                        servicio.setMtotot(rs.getDouble("mtotot"));
                        servicio.setPartida(partida);
                        
                        return servicio;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }
    
	@Override
	public byte[] reporteServicio() throws Exception {
		Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("datasource", datasource);
        PdfServicio pdfServicio = new PdfServicio(parametros);
		return pdfServicio.creaReporte();
	}
    
}