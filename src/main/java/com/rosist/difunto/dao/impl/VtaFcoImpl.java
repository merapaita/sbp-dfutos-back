package com.rosist.difunto.dao.impl;

import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cuartel;
import com.rosist.difunto.modelSbp.Difunto;
import com.rosist.difunto.modelSbp.Mausoleo;
import com.rosist.difunto.modelSbp.Nicho_t;
import com.rosist.difunto.modelSbp.Parmae;
import com.rosist.difunto.modelSbp.Vtafco;
import com.rosist.difunto.reports.PdfReciboVtaFcoNuevo;
import com.rosist.difunto.dao.VtaFocosDao;
import com.rosist.difunto.dao.MausoleoDao;
import com.rosist.difunto.dao.NichotDao;
import com.rosist.difunto.dao.ParmaeDao;
import com.rosist.difunto.dao.VtaFocosDao;
import java.awt.print.Printable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Administrador
 */
@Repository
public class VtaFcoImpl implements VtaFocosDao{
//    @Autowired
//    private Printable printable;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private NichotDao daoNichot;
    
    @Autowired
    private MausoleoDao daoMausoleo;
    
    @Autowired
    private ParmaeDao daoParmae;
    
    private java.text.SimpleDateFormat fFecha=new java.text.SimpleDateFormat("dd/MM/yyyy");
//    private String cano = "2015";
    
    private final static Logger logger = LoggerFactory.getLogger(VtaFcoImpl.class);
    
    @Override
    public int getVtaFcoCount(String cAno) {
        String sql = "select count(*) as count from vf"+cAno;
//        String sql = "select count(*) as count from vtafco";
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getVtaFcoCount(String cAno, String condicion) {
        String sql = "select count(*) as count "
                + "     FROM vf" + cAno + " vf LEFT JOIN parmae tv ON vf.tipvf=tv.codigo AND tv.tipo='TIPVF '"
//                + "     FROM vtafco vtafco LEFT JOIN parmae tv ON vf.tipvf=tv.codigo AND tv.tipo='TIPVF '"
                + "                    LEFT JOIN cementerio c ON vf.codcem=c.codcem"
                + "                    LEFT JOIN parmae te ON vf.tipent=te.codigo AND te.tipo='TIPENT'"
                + "                    LEFT JOIN cuartel cu ON vf.codcem=cu.codcem AND vf.codcuar=cu.codcuar"
                + "                    LEFT JOIN parmae e ON vf.estado=e.codigo AND e.tipo='ESTVF '"
                + "    where 1=1 " + condicion;
        System.out.println("consulta sqlcount.-=> " + sql);
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getNewIdVtaFco(int codcem, String cAno){
        String sql = "select ifnull(max(codvta),0)+1 from vf" + cAno + " where codcem=?" ;
        int nCorrel = this.jdbcTemplate.queryForObject(sql, Integer.class, new Object[]{codcem});
        return nCorrel;
    }
    
    @Override
    public int insertaVtaFco(Vtafco vtafco, String cAno) {
        int resp=0;
        System.out.println("guadando...");
        String sql = "insert into vf" + cAno + " (codvta,fecvta,numrec,tipvf,codcem,coddif,nomdif,tipent,codcuar,codnic,fila1,fila2,columna1,columna2,codmau,lote,ubicacion, familia,valor,nomcli,dircli, estado,user,duser) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        
        int xCodVta = this.getNewIdVtaFco(vtafco.getCementerio().getCodcem(), cAno);
        vtafco.setCodvta(xCodVta);
        vtafco.setNumrec(String.format("%1$010d",xCodVta));
        String cUser = "usuario";
//        String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Date   dUser = new Date();
//        System.out.println("date de user -=> " + xCodVta);
        int xCodCem = vtafco.getCementerio().getCodcem();
        int xCodCuar = 0;
        Nicho_t nicho = null;
        String xCodNic = "", xFila2="", xLote="", xUbicacion="", xFamilia="";
        int xFila1 = 0, xCol1=0, xCol2=0, xCodMau=0, xNumDif=0;
        Mausoleo mausoleo = null;
        if (vtafco.getTipoentierro().getCodigo().equals("1")){
            nicho = daoNichot.buscaNichot(vtafco.getCementerio().getCodcem(), vtafco.getCuartel().getCodcuar(), vtafco.getNicho().getFila1(), vtafco.getNicho().getCol1());
            if (nicho!=null){
                xCodCuar = vtafco.getCuartel().getCodcuar();
                xCodNic = String.format("%1$01d",vtafco.getNicho().getFila1()) + String.format("%1$03d",vtafco.getNicho().getCol1());
                xFila1  = Integer.valueOf(xCodNic.substring(0, 1));
                xFila2  = nicho.getFila2();
                xCol1   = Integer.valueOf(xCodNic.substring(1, 4));
                xCol2   = nicho.getCol2();
            }
        } if (vtafco.getTipoentierro().getCodigo().equals("2")){
            if (vtafco.isValida()==true){
                xCodMau = vtafco.getMausoleo().getCodmau();
            }
            mausoleo = daoMausoleo.buscaMausoleo(xCodCem, xCodMau);
            xLote = vtafco.getMausoleo().getNomlote();
            xUbicacion = vtafco.getMausoleo().getUbicacion();
            xFamilia   = vtafco.getMausoleo().getFamilia();
        }
        String xEstado = "00";
        
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                              xCodVta, vtafco.getFecvta(),vtafco.getNumrec(),vtafco.getTipovtafco().getCodigo(),
                              vtafco.getCementerio().getCodcem(),vtafco.getDifunto().getCoddif(),
                              vtafco.getDifunto().getNomdif(),vtafco.getTipoentierro().getCodigo(),
                              xCodCuar,xCodNic,xFila1,xFila2,xCol1, xCol2,
                              xCodMau,xLote,xUbicacion,xFamilia,
                              vtafco.getValor(),vtafco.getNomcli(),vtafco.getDircli(),xEstado,cUser,dUser
                          });
        if (resp>0){
            if (vtafco.getTipoentierro().getCodigo().equals("1")){
                sql = "update nicho_t set vf" + cAno + " =(vf" + cAno + "+1) where codcem=? and codcuar=? and fila1=? and col1=?";
                resp = jdbcTemplate.update(sql,
                              new Object[]{
                                  xCodCem, xCodCuar, xFila1, xCol1
                              });
                
                sql = "update nicho_f set col" + String.format("%1$03d",xCol1) + "=(col" + String.format("%1$03d",xCol1) +"+1) where codcem=? and codcuar=? and fila1=?";
                resp = jdbcTemplate.update(sql,
                                  new Object[]{
                                      xCodCem, xCodCuar, xFila1
                                  });
            }
        }
        return resp;
    }

    @Override
    public int modificaVtaFco(Vtafco vtafco, String cAno) {
        String cUser = "usuario";
//        String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Date   dUser = new Date();
        int resp=0;
//        System.out.println("vta fco en modifica.-=> " + vtafco.toString());
        String sql = "update vf" + cAno + " set nomdif=?, nomcli=?, dircli=?, lote=?, ubicacion=?, familia=?, usercr=?, dusercr=? where codcem=? and codvta=?";
//      String sql = "update vtafco set nomdif=?, nomcli=?, dircli=?, lote=?, ubicacion=?, familia=?, usercr=?, dusercr=? where codcem=? and codvta=?";
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            vtafco.getDifunto().getNomdif(), vtafco.getNomcli(), vtafco.getDircli(), vtafco.getMausoleo().getNomlote(), vtafco.getMausoleo().getUbicacion(), vtafco.getMausoleo().getFamilia(), cUser, dUser, vtafco.getCementerio().getCodcem(), vtafco.getCodvta()
                          });
        return resp;
    }

    @Override
    public int anulaVtaFco(int codcem, int codvta, String cAno) {
        int resp=0;
        Vtafco vtafco = buscaVtaFco(codcem, codvta, cAno);
        
        String sql = "update vf" + cAno + " set estado=? where codcem=? and codvta=?";
//        String sql = "update vtafco set estado=? where codcem=? and codvta=?";
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            "99", codcem, codvta
                          });
        if (resp>0){
            if (vtafco.getTipoentierro().getCodigo().equals("1")){
                int xCodCem  = vtafco.getCementerio().getCodcem();
                int xCodCuar = vtafco.getCuartel().getCodcuar();
                int xFila1 = vtafco.getNicho().getFila1();
                int xCol1  = vtafco.getNicho().getCol1();
                sql = "update nicho_t set vf" + cAno + "=(vf" + cAno + "-1) where codcem=? and codcuar=? and fila1=? and col1=?";
                resp = jdbcTemplate.update(sql,
                              new Object[]{
                                  xCodCem, xCodCuar, xFila1, xCol1
                              });
                
                sql = "update nicho_f set col" + String.format("%1$03d",xCol1) + "=(col" + String.format("%1$03d",xCol1) +"-1) where codcem=? and codcuar=? and fila1=?";
                resp = jdbcTemplate.update(sql,
                                  new Object[]{
                                      xCodCem, xCodCuar, xFila1
                                  });
            }
        }
        return resp;
    }

    @Override
    public Vtafco buscaVtaFco(int codcem, int codvta, String cAno) {
        String sql = "SELECT vf.codvta, DATE_FORMAT(vf.fecvta,'%d/%m/%Y') fecvta, vf.numrec, vf.tipvf, tv.descri destipvta, "
                + "          vf.codcem, c.nomcem, vf.coddif, vf.nomdif, vf.tipent, te.descri destipent, vf.codcuar, cu.nomcuar, "
                + "          cu.tipcuar, vf.codnic,vf.fila1,vf.fila2,vf.columna1,vf.columna2, vf.codmau, vf.familia, vf.lote, "
                + "          vf.ubicacion, m.tipomau, vf.valor, vf.nomcli, vf.dircli, vf.estado, e.descri desestado, vf.user, "
                + "          vf.usercr, vf.duser, vf.dusercr "
                + "     FROM vf" + cAno + " vf LEFT JOIN parmae tv ON vf.tipvf=tv.codigo AND tv.tipo='TIPVF ' "
                + "                            LEFT JOIN cementerio c ON vf.codcem=c.codcem "
                + "                            LEFT JOIN parmae te ON vf.tipent=te.codigo AND te.tipo='TIPENT' "
                + "                            LEFT JOIN cuartel cu ON vf.codcem=cu.codcem AND vf.codcuar=cu.codcuar "
                + "                            LEFT JOIN mausoleo m ON vf.codcem=m.codcem AND vf.codmau=m.codmau "
                + "                            LEFT JOIN parmae e ON vf.estado=e.codigo AND e.tipo='ESTVF '"
                + "    where vf.codcem=? and codvta=?";
        
        List<Vtafco> matches = jdbcTemplate.query (sql,
                new RowMapper<Vtafco>() {
                @Override
                    public Vtafco mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Cementerio cementerio = new Cementerio();
                        cementerio.setCodcem(rs.getInt("codcem"));
                        cementerio.setNomcem(rs.getString("nomcem"));
                        
                        Parmae estado = new Parmae("ESTVF ",rs.getString("estado"),"",rs.getString("desestado"));
                        Parmae tipvf  = new Parmae("TIPVF ",rs.getString("tipvf"),"",rs.getString("destipvta"));
                        
                        Cuartel cuartel = new Cuartel();
                        Nicho_t nicho = new Nicho_t();
                        Mausoleo mausoleo = new Mausoleo();
                        if (rs.getString("tipent").equals("1")){
                            Parmae pTipCuar = daoParmae.buscaParmae("TIPCUA", rs.getString("tipcuar"), "");
                            
                            cuartel.setCementerio(cementerio);
                            cuartel.setCodcuar(rs.getInt("codcuar"));
                            cuartel.setNomcuar(rs.getString("nomcuar"));
                            cuartel.setTipcuar(pTipCuar);
                            
                            nicho.setFila1(rs.getInt("fila1"));
                            nicho.setFila2(rs.getString("fila2"));
                            nicho.setCol1(rs.getInt("columna1"));
                            nicho.setCol2(rs.getInt("columna2"));
                        } else if (rs.getString("tipent").equals("2")){
                            Parmae pTipoMau = daoParmae.buscaParmae("TIPMAU", rs.getString("tipomau"), "");
                        
                            mausoleo.setCementerio(cementerio);
                            mausoleo.setCodmau(rs.getInt("codmau"));
                            mausoleo.setNomlote(rs.getString("lote"));
                            mausoleo.setTipomau(pTipoMau);
                            mausoleo.setUbicacion(rs.getString("ubicacion"));
                            mausoleo.setFamilia(rs.getString("familia"));
                        }
                        
                        Parmae pTipoEnt = new Parmae();
                        pTipoEnt.setTipo("TIPENT");
                        pTipoEnt.setCodigo(rs.getString("tipent"));
                        pTipoEnt.setDescri(rs.getString("destipent"));
                        
                        Difunto difunto = new Difunto();
                        difunto.setCoddif(rs.getInt("coddif"));
                        difunto.setNomdif(rs.getString("nomdif"));
                        difunto.setTipo_entierro(pTipoEnt);
                        difunto.setCementerio(cementerio);
                        difunto.setCuartel(cuartel);
                        difunto.setNicho(nicho);
                        difunto.setMausoleo(mausoleo);

                        Vtafco vtafco = new Vtafco();
                        vtafco.setCodvta(rs.getInt("codvta"));
                        vtafco.setFecvta(rs.getString("fecvta"));
                        vtafco.setNumrec(rs.getString("numrec"));
                        vtafco.setTipovtafco(tipvf);
                        vtafco.setCementerio(cementerio);
                        vtafco.setDifunto(difunto);
                        vtafco.setTipoentierro(pTipoEnt);
                        vtafco.setCuartel(cuartel);
                        vtafco.setNicho(nicho);
                        vtafco.setMausoleo(mausoleo);
                        vtafco.setEstado(estado);
                        vtafco.setNomcli(rs.getString("nomcli"));
                        vtafco.setDircli(rs.getString("dircli"));
                        vtafco.setValor(rs.getDouble("valor"));
                        vtafco.setUser(rs.getString("user"));
                        vtafco.setUsercr(rs.getString("usercr"));
                        vtafco.setDuser(rs.getString("duser"));
                        vtafco.setDusercr(rs.getString("dusercr"));
                        return vtafco;
                    }
        }, new Object[] {codcem,codvta});
        return matches.size() > 0? (Vtafco)matches.get(0): null;
    }
    
    @Override
    public List<Vtafco> listaVtaFco(String condicion, String cAno) {
        String sql = "SELECT vf.codvta, DATE_FORMAT(vf.fecvta,'%d/%m/%Y') fecvta, vf.numrec, vf.tipvf, tv.descri destipvta, "
                + "          vf.codcem, c.nomcem, vf.coddif, vf.nomdif, vf.tipent, te.descri destipent, vf.codcuar, cu.nomcuar, "
                + "          cu.tipcuar, vf.codnic,vf.fila1,vf.fila2,vf.columna1,vf.columna2, vf.codmau, vf.familia, vf.lote, "
                + "          vf.ubicacion, m.tipomau, vf.valor, vf.nomcli, vf.dircli, vf.estado, e.descri desestado, vf.user, "
                + "          vf.usercr, vf.duser, vf.dusercr "
                + "     FROM vf" + cAno + " vf LEFT JOIN parmae tv ON vf.tipvf=tv.codigo AND tv.tipo='TIPVF ' "
                + "                            LEFT JOIN cementerio c ON vf.codcem=c.codcem "
                + "                            LEFT JOIN parmae te ON vf.tipent=te.codigo AND te.tipo='TIPENT' "
                + "                            LEFT JOIN cuartel cu ON vf.codcem=cu.codcem AND vf.codcuar=cu.codcuar "
                + "                            LEFT JOIN mausoleo m ON vf.codcem=m.codcem AND vf.codmau=m.codmau "
                + "                            LEFT JOIN parmae e ON vf.estado=e.codigo AND e.tipo='ESTVF '"
                + "    where 1=1 " + condicion;
         
        System.out.println("sql Vta Fco en Impl.-=> " + sql);
        List<Vtafco> matches = jdbcTemplate.query (sql,
                new RowMapper<Vtafco>() {
                @Override
                    public Vtafco mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Cementerio cementerio = new Cementerio();
                        cementerio.setCodcem(rs.getInt("codcem"));
                        cementerio.setNomcem(rs.getString("nomcem"));
                        
                        Parmae estado = new Parmae("ESTVF ",rs.getString("estado"),"",rs.getString("desestado"));
                        Parmae tipvf  = new Parmae("TIPVF ",rs.getString("tipvf"),"",rs.getString("destipvta"));
                        
                        Cuartel cuartel = new Cuartel();
                        Nicho_t nicho = new Nicho_t();
                        Mausoleo mausoleo = new Mausoleo();
                        if (rs.getString("tipent").equals("1")){
                            Parmae pTipCuar = daoParmae.buscaParmae("TIPCUA", rs.getString("tipcuar"), "");
                            cuartel.setCementerio(cementerio);
                            cuartel.setCodcuar(rs.getInt("codcuar"));
                            cuartel.setNomcuar(rs.getString("nomcuar"));
                            cuartel.setTipcuar(pTipCuar);
//                            cuartel.setFilas(rs.getInt("filas"));
//                            cuartel.setColumnas(rs.getInt("columnas"));
                            
                            nicho.setFila1(rs.getInt("fila1"));
                            nicho.setFila2(rs.getString("fila2"));
                            nicho.setCol1(rs.getInt("columna1"));
                            nicho.setCol2(rs.getInt("columna2"));
                        } else if (rs.getString("tipent").equals("2")){
                            Parmae pTipoMau = daoParmae.buscaParmae("TIPMAU", rs.getString("tipomau"), "");
                        
                            mausoleo.setCementerio(cementerio);
                            mausoleo.setCodmau(rs.getInt("codmau"));
//                            mausoleo.setLotizado(rs.getString("lotizado"));
                            mausoleo.setNomlote(rs.getString("lote"));
                            mausoleo.setTipomau(pTipoMau);
                            mausoleo.setUbicacion(rs.getString("ubicacion"));
                            mausoleo.setFamilia(rs.getString("familia"));
                        }
                        
                        Parmae pTipoEnt = new Parmae();
                        pTipoEnt.setTipo("TIPENT");
                        pTipoEnt.setCodigo(rs.getString("tipent"));
                        pTipoEnt.setDescri(rs.getString("destipent"));
                        
                        Difunto difunto = new Difunto();
                        difunto.setCoddif(rs.getInt("coddif"));
                        difunto.setNomdif(rs.getString("nomdif"));
                        difunto.setTipo_entierro(pTipoEnt);
                        difunto.setCementerio(cementerio);
                        difunto.setCuartel(cuartel);
                        difunto.setNicho(nicho);
                        difunto.setMausoleo(mausoleo);
    
                        Vtafco vtafco = new Vtafco();
                        vtafco.setCodvta(rs.getInt("codvta"));
                        vtafco.setFecvta(rs.getString("fecvta"));
                        vtafco.setNumrec(rs.getString("numrec"));
                        vtafco.setTipovtafco(tipvf);
                        vtafco.setCementerio(cementerio);
                        vtafco.setDifunto(difunto);
                        vtafco.setTipoentierro(pTipoEnt);
                        vtafco.setCuartel(cuartel);
                        vtafco.setNicho(nicho);
                        vtafco.setMausoleo(mausoleo);
                        vtafco.setEstado(estado);
                        vtafco.setNomcli(rs.getString("nomcli"));
                        vtafco.setDircli(rs.getString("dircli"));
                        vtafco.setValor(rs.getDouble("valor"));
                        vtafco.setUser(rs.getString("user"));
                        vtafco.setUsercr(rs.getString("usercr"));
                        vtafco.setDuser(rs.getString("duser"));
                        vtafco.setDusercr(rs.getString("dusercr"));
                        
                        return vtafco;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }
    @Override
    public List<Vtafco> listaVtaFcoxCuartel(String condicion, String sFiltro, String cAno) {
        String sql = "SELECT codvta,date_format(fecvta,'%d/%m/%Y') fecvta,numrec,tipvf, tv.descri destipvta,vf.codcem,"
                + "          c.nomcem,coddif,nomdif,tipent, te.descri destipent, vf.codcuar, cu.nomcuar, cu.tipcuar, cu.grupo,"
                + "          codnic,fila1,fila2,columna1,columna2, valor, nomcli, dircli,"
                + "          vf.estado, e.descri desestado, user, usercr, duser, dusercr "
                + "     FROM vf" + cAno + " vf LEFT JOIN parmae tv ON vf.tipvf=tv.codigo AND tv.tipo='TIPVF '"
//                + "     FROM vtafco vf LEFT JOIN parmae tv ON vf.tipvf=tv.codigo AND tv.tipo='TIPVF '"
                + "                    LEFT JOIN cementerio c ON vf.codcem=c.codcem"
                + "                    LEFT JOIN parmae te ON vf.tipent=te.codigo AND te.tipo='TIPENT'"
                + "                    LEFT JOIN cuartel cu ON vf.codcem=cu.codcem AND vf.codcuar=cu.codcuar"
                + "                    LEFT JOIN parmae e ON vf.estado=e.codigo AND e.tipo='ESTVF '"
                + " where tipent='1' " + condicion 
                + " order by grupo,codcuar,columna1,fila1"
                + sFiltro;

//        System.out.println("sql venta dfoco x cuartel" + sql);
        
        List<Vtafco> matches = jdbcTemplate.query (sql,
                new RowMapper<Vtafco>() {
                @Override
                    public Vtafco mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Cementerio cementerio = new Cementerio();
                        cementerio.setCodcem(rs.getInt("codcem"));
                        cementerio.setNomcem(rs.getString("nomcem"));
                        
                        Parmae estado = new Parmae("ESTVF ",rs.getString("estado"),"",rs.getString("desestado"));
                        Parmae tipvf  = new Parmae("TIPVF ",rs.getString("tipvf"),"",rs.getString("destipvta"));
                        
                        Cuartel cuartel = new Cuartel();
                        Nicho_t nicho = new Nicho_t();
                        if (rs.getString("tipent").equals("1")){
//                            Parmae pTipCuar  = new Parmae("TIPCUA",rs.getString("tipcuar"),"",rs.getString("destipcua"));
                            Parmae pTipCuar = null;
//                            System.out.println("id codvta.-=> " + rs.getInt("codvta"));
                            pTipCuar = daoParmae.buscaParmae("TIPCUA", rs.getString("tipcuar"), "");
                            cuartel.setCementerio(cementerio);
                            cuartel.setCodcuar(rs.getInt("codcuar"));
                            cuartel.setNomcuar(rs.getString("nomcuar"));
                            cuartel.setTipcuar(pTipCuar);
                            cuartel.setGrupo(rs.getString("grupo"));
                            
                            nicho.setFila1(rs.getInt("fila1"));
                            nicho.setFila2(rs.getString("fila2"));
                            nicho.setCol1(rs.getInt("columna1"));
                            nicho.setCol2(rs.getInt("columna2"));
                        }
                        
                        Parmae pTipoEnt = new Parmae();
                        pTipoEnt.setTipo("TIPENT");
                        pTipoEnt.setCodigo(rs.getString("tipent"));
                        pTipoEnt.setDescri(rs.getString("destipent"));
                        
                        Difunto difunto = new Difunto();
                        difunto.setCoddif(rs.getInt("coddif"));
                        difunto.setNomdif(rs.getString("nomdif"));
                        difunto.setTipo_entierro(pTipoEnt);
                        difunto.setCementerio(cementerio);
                        difunto.setCuartel(cuartel);
                        difunto.setNicho(nicho);
    
                        Vtafco vtafco = new Vtafco();
                        vtafco.setCodvta(rs.getInt("codvta"));
                        vtafco.setFecvta(rs.getString("fecvta"));
                        vtafco.setNumrec(rs.getString("numrec"));
                        vtafco.setTipovtafco(tipvf);
                        vtafco.setCementerio(cementerio);
                        vtafco.setDifunto(difunto);
                        vtafco.setTipoentierro(pTipoEnt);
                        vtafco.setCuartel(cuartel);
                        vtafco.setNicho(nicho);
                        vtafco.setEstado(estado);
                        vtafco.setNomcli(rs.getString("nomcli"));
                        vtafco.setDircli(rs.getString("dircli"));
                        vtafco.setValor(rs.getDouble("valor"));
                        vtafco.setUser(rs.getString("user"));
                        vtafco.setUsercr(rs.getString("usercr"));
                        vtafco.setDuser(rs.getString("duser"));
                        vtafco.setDusercr(rs.getString("dusercr"));
                        
                        return vtafco;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }
    
    @Override
    public List<Vtafco> listaResumenVtaFcoxCuartel(int codcem, String cAno) {
        String sql = "SELECT vf.codcem, c.nomcem, vf.codcuar, cu.nomcuar, cu.tipcuar, cu.grupo, vf.fila2, COUNT(*) vfnum" +
                     "  FROM vf" + cAno + " vf LEFT JOIN cementerio c ON vf.codcem=c.codcem" +
                     "                         LEFT JOIN cuartel cu   ON vf.codcem=cu.codcem AND vf.codcuar=cu.codcuar" +
                     " WHERE vf.codcem=" + codcem + " AND vf.estado<>'99' AND vf.tipent='1'" +
                     " GROUP BY grupo, codcem, codcuar, fila2";
        
        List<Vtafco> matches = jdbcTemplate.query (sql,
                new RowMapper<Vtafco>() {
                @Override
                    public Vtafco mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Cementerio cementerio = new Cementerio(rs.getInt("codcem"), rs.getString("nomcem"), true);
                        
                        Cuartel cuartel = new Cuartel();
                        cuartel.setCodcuar(rs.getInt("codcuar"));
                        cuartel.setNomcuar(rs.getString("nomcuar"));
                        cuartel.setGrupo(rs.getString("grupo"));
                        
                        Nicho_t nichot = new Nicho_t();
                        nichot.setFila2(rs.getString("fila2"));
                        nichot.setNum(rs.getInt("vfnum"));
                        
                        Vtafco vtafco = new Vtafco();
                        vtafco.setCementerio(cementerio);
                        vtafco.setCuartel(cuartel);
                        vtafco.setNicho(nichot);
                        
                        return vtafco;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }
    
    @Override
    public List<Vtafco> listaVtaFcoxMausoleo(String condicion, String sFiltro, String cAno) {
        String sql = "SELECT codvta,date_format(fecvta,'%d/%m/%Y') fecvta, vf.numrec, vf.tipvf, tv.descri destipvta, vf.codcem,"
                + "          c.nomcem, vf.coddif, vf.nomdif, vf.tipent, vf.codmau, vf.lote, vf.familia, vf.ubicacion, m.tipomau, "
                + "          te.descri destipent, vf.valor, vf.nomcli, vf.dircli, vf.estado, e.descri desestado, vf.user, vf.usercr, "
                + "          vf.duser, vf.dusercr "
                + "     FROM vf" + cAno + " vf LEFT JOIN parmae tv ON vf.tipvf=tv.codigo AND tv.tipo='TIPVF '"
//                + "     FROM vtafco vf LEFT JOIN parmae tv ON vf.tipvf=tv.codigo AND tv.tipo='TIPVF '"
                + "                    LEFT JOIN cementerio c ON vf.codcem=c.codcem"
                + "                    LEFT JOIN mausoleo m ON vf.codcem=m.codcem AND vf.codmau=m.codmau"
                + "                    LEFT JOIN parmae te ON vf.tipent=te.codigo AND te.tipo='TIPENT'"
                + "                    LEFT JOIN parmae e ON vf.estado=e.codigo AND e.tipo='ESTVF '"
                + " where tipent='2' " + condicion
                + " order by lote";
//                + " limit 1,100";
        
        List<Vtafco> matches = jdbcTemplate.query (sql,
                new RowMapper<Vtafco>() {
                @Override
                    public Vtafco mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Cementerio cementerio = new Cementerio();
                        cementerio.setCodcem(rs.getInt("codcem"));
                        cementerio.setNomcem(rs.getString("nomcem"));
                        
                        Parmae estado = new Parmae("ESTVF ",rs.getString("estado"),"",rs.getString("desestado"));
                        Parmae tipvf  = new Parmae("TIPVF ",rs.getString("tipvf"),"",rs.getString("destipvta"));
                        
                        Mausoleo mausoleo = new Mausoleo();
                        if (rs.getString("tipent").equals("2")){
                            Parmae pTipoMau = null;
                            pTipoMau = daoParmae.buscaParmae("TIPMAU", rs.getString("tipomau"), "");
//                        System.out.println(pTipoMau);
                            mausoleo.setCementerio(cementerio);
                            mausoleo.setCodmau(rs.getInt("codmau"));
//                            mausoleo.setLotizado(rs.getString("lotizado"));
                            mausoleo.setNomlote(rs.getString("lote"));
                            mausoleo.setTipomau(pTipoMau);
                            mausoleo.setUbicacion(rs.getString("ubicacion"));
                            mausoleo.setFamilia(rs.getString("familia"));
                        }
                        
                        Parmae pTipoEnt = new Parmae();
                        pTipoEnt.setTipo("TIPENT");
                        pTipoEnt.setCodigo(rs.getString("tipent"));
                        pTipoEnt.setDescri(rs.getString("destipent"));
                        
                        Difunto difunto = new Difunto();
                        difunto.setCoddif(rs.getInt("coddif"));
                        difunto.setNomdif(rs.getString("nomdif"));
                        difunto.setTipo_entierro(pTipoEnt);
                        difunto.setCementerio(cementerio);
                        difunto.setMausoleo(mausoleo);
                        
                        Vtafco vtafco = new Vtafco();
                        vtafco.setCodvta(rs.getInt("codvta"));
                        vtafco.setFecvta(rs.getString("fecvta"));
                        vtafco.setNumrec(rs.getString("numrec"));
                        vtafco.setTipovtafco(tipvf);
                        vtafco.setCementerio(cementerio);
                        vtafco.setDifunto(difunto);
                        vtafco.setTipoentierro(pTipoEnt);
                        vtafco.setMausoleo(mausoleo);
                        vtafco.setEstado(estado);
                        vtafco.setNomcli(rs.getString("nomcli"));
                        vtafco.setDircli(rs.getString("dircli"));
                        vtafco.setValor(rs.getDouble("valor"));
                        vtafco.setUser(rs.getString("user"));
                        vtafco.setUsercr(rs.getString("usercr"));
                        vtafco.setDuser(rs.getString("duser"));
                        vtafco.setDusercr(rs.getString("dusercr"));
                        
                        return vtafco;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }

    @Override
    public int valFco(String tipvf) {
        int mRet = 0;
        Calendar fecha = new GregorianCalendar();
        List<Parmae> lParmae = null;
        
        SimpleDateFormat fFecLim = new SimpleDateFormat("MMdd");
        List<Parmae> lFecInc1 = daoParmae.listaParmae(" and tipo='FECINC' and codigo='1'","","");
        List<Parmae> lFecInc2 = daoParmae.listaParmae(" and tipo='FECINC' and codigo='2'","","");
        String xFecVta = fFecLim.format(fecha.getTime());
        
        String xFecInc1 = "";        // 1021
        String xFecInc2 = "";        // 1101
        if (lFecInc1!=null) {
            xFecInc1 = lFecInc1.get(0).getDescri();
        }
        if (lFecInc2!=null) {
            xFecInc2 = lFecInc2.get(0).getDescri();
        }
//        System.out.println("xFecha-=> " + xFecVta + "** " +xFecVta.compareTo(xFecInc));
        if (tipvf.equals("1")){
            if (xFecVta.compareTo(xFecInc1)<0){
                lParmae = daoParmae.listaParmae(" and tipo='VALVF ' and codigo='1'","","");
            } else if(xFecVta.compareTo(xFecInc2)<0) {
                lParmae = daoParmae.listaParmae(" and tipo='VALVF ' and codigo='3'","","");
            } else {
                lParmae = daoParmae.listaParmae(" and tipo='VALVF ' and codigo='5'","","");
            }
        } if (tipvf.equals("2")){
            if (xFecVta.compareTo(xFecInc1)<0){
                lParmae = daoParmae.listaParmae(" and tipo='VALVF ' and codigo='2'","","");
            } else if(xFecVta.compareTo(xFecInc2)<0) {
                lParmae = daoParmae.listaParmae(" and tipo='VALVF ' and codigo='4'","","");
            } else {
                lParmae = daoParmae.listaParmae(" and tipo='VALVF ' and codigo='6'","","");
            }
        }
        
        return Integer.valueOf(lParmae.get(0).getCodigoaux());
    }
    
	@Override
	public byte[] reporteVtaFoco(int id) throws Exception {
		byte[] data = null;
        Vtafco vtafco = new Vtafco();
//        Vtafco vtafco = repo.getById(id);
//        logger.info("reporteAnalisis..." + analisis);
		Map<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("vtafco", vtafco);
		
        PdfReciboVtaFcoNuevo pdfReciboVtafco = new PdfReciboVtaFcoNuevo(parametros);
        
//		data = service.generarReporte(id);
		return pdfReciboVtafco.creaPDF();
//		return pdfAnalisis.creaPDF();
	}
    
}