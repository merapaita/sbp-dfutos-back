package com.rosist.difunto.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.dao.DeudaDao;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cuartel;
import com.rosist.difunto.modelSbp.Deuda;
import com.rosist.difunto.modelSbp.Difunto;
import com.rosist.difunto.modelSbp.Mausoleo;
import com.rosist.difunto.modelSbp.Nicho_t;
import com.rosist.difunto.modelSbp.Parmae;
import com.rosist.difunto.dao.NichotDao;

@Repository
public class DeudaImpl implements DeudaDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private NichotDao servNichot;
    
    private SimpleDateFormat DATETIME_MYSQL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public int getDeudaCount() {
        int nNumReg = 0;
        String sql = "select count(*) as count from deudas";
        nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getDeudaCount(String condicion) {
        int nNumReg = 0;
        String sql = "select count(*) as count "
                + "     from deudas d LEFT JOIN parmae te ON te.tipo='TIPENT' AND d.tipent=te.codigo "
                + "                   LEFT JOIN cementerio c ON c.codcem=d.codcem "
                + "                   LEFT JOIN cuartel cu ON cu.codcem=d.codcem AND cu.codcuar=d.codcuar "
                + "                   LEFT JOIN parmae e ON e.tipo='ESTDEU' AND d.estado=e.codigo"
                + "    where 1=1 " + condicion;
        nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getNewDeuda() {
        int nCorrel = 0;
        String sql = "select ifnull(max(iddeu),0)+1 as id from deudas";
        nCorrel = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nCorrel;
    }

    @Override
    public int insertaDeuda(Deuda deuda) {
        int resp = 0;
        System.out.println("guadando...");
        String sql = "insert into deudas(iddeu, fecdeu, coddif, nomdif, nomcli, dircli, tipent, codcem, codcuar, codnic, fila1, fila2, columna1, columna2, codmau, lote, ubicacion, familia, monto, estado, user, duser) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int idDeu = this.getNewDeuda();
        String cUser = "usuario";
//        String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String dUser = DATETIME_MYSQL.format(new Date());
        int xCodCuar = 0, xFila1 = 0, xCol1 = 0, xCol2 = 0, xCodMau = 0;
        String xFila2 = "", xCodNic = "", xNomLote = "", xUbicacion = "", xFamilia = "";

        if (deuda.getTipent().getCodigo().equals("1")) {
//            xCodCuar = deuda.getCuartel().getCodcuar();
            Nicho_t nicho = servNichot.buscaNichot(deuda.getCementerio().getCodcem(), deuda.getCuartel().getCodcuar(), deuda.getNicho().getFila1(), deuda.getNicho().getCol1());
            if (nicho!=null){
                xCodCuar = deuda.getCuartel().getCodcuar();
                xCodNic = String.format("%1$01d",deuda.getNicho().getFila1()) + String.format("%1$03d",deuda.getNicho().getCol1());
                xFila1  = Integer.valueOf(xCodNic.substring(0, 1));
                xFila2  = nicho.getFila2();
                xCol1   = Integer.valueOf(xCodNic.substring(1, 4));
                xCol2   = nicho.getCol2();
            }
        } else if (deuda.getTipent().getCodigo().equals("2")) {
            xCodMau = deuda.getMausoleo().getCodmau();
            xNomLote = deuda.getMausoleo().getNomlote();
            xUbicacion = deuda.getMausoleo().getUbicacion();
            xFamilia = deuda.getMausoleo().getFamilia();
        }

        resp = jdbcTemplate.update(sql,
                new Object[]{
                    idDeu, deuda.getFecdeu(), deuda.getDifunto().getCoddif(), deuda.getDifunto().getNomdif(),
                    deuda.getNomcli(), deuda.getDircli(), deuda.getTipent().getCodigo(), deuda.getCementerio().getCodcem(),
                    xCodCuar, xCodNic, xFila1, xFila2, xCol1, xCol2, xCodMau,
                    xNomLote, xUbicacion, xFamilia,
                    deuda.getMonto(), "01", cUser, dUser
                });
        return resp;
    }

    @Override
    public int modificaDeuda(Deuda deuda) {
        int resp=0;
        String cEstado = "01";
        String cUser = "usuario";
//        String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String dUser = DATETIME_MYSQL.format(new Date());
        String sql = "update deudas set nomcli=?, dircli=?, monto=?, estado=?, usercr=?, dusercr=? where iddeu=?";
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            deuda.getNomcli(), deuda.getDircli(), deuda.getMonto(),cEstado, cUser, dUser, deuda.getIddeu()
                          }
        );
        return resp;
    }

    @Override
    public String anulaDeuda(int iddeu) {
        int resp = 0;
        String mRet = "";
        String sql = "update deudas set estado=? where iddeu=?";
        resp = jdbcTemplate.update(sql, new Object[]{"99", iddeu});
        if (resp > 0) {
            mRet = "ok";
        }
        return mRet;
    }

    @Override
    public String cancelaDeuda(int iddeu) {
        int resp = 0;
        String mRet = "";
        String sql = "update deudas set estado=?, where iddeu=?";
        resp = jdbcTemplate.update(sql, new Object[]{"02", iddeu});
        if (resp > 0) {
            mRet = "ok";
        }
        return mRet;
    }

    @Override
    public Deuda buscaDeuda(int iddeu) {
        String sql = "select d.iddeu, DATE_FORMAT(d.fecdeu, '%d/%m/%Y') fecdeu, d.coddif, d.nomdif, d.nomcli, d.dircli, d.tipent, te.descri destipent, "
                + "          d.codcem, c.nomcem, d.codcuar, cu.nomcuar, d.codnic, d.fila1, d.fila2, d.columna1, "
                + "          d.columna2, d.codmau, d.lote, d.ubicacion, d.familia, d.monto, d.estado, e.descri desestado, "
                + "          d.user, d.duser, d.usercr, d.dusercr "
                + "     FROM deudas d LEFT JOIN parmae te ON te.tipo='TIPENT' AND d.tipent=te.codigo "
                + "                   LEFT JOIN cementerio c ON c.codcem=d.codcem "
                + "                   LEFT JOIN cuartel cu ON cu.codcem=d.codcem AND cu.codcuar=d.codcuar "
                + "                   LEFT JOIN parmae e ON e.tipo='ESTDEU' AND d.estado=e.codigo "
                + "    where iddeu=?";

        List<Deuda> lDeuda = jdbcTemplate.query(sql,
                new RowMapper<Deuda>() {
            @Override
            public Deuda mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                Parmae tipent = new Parmae("TIPENT", rs.getString("tipent"), "", rs.getString("destipent"));
                Parmae estado = new Parmae("ESTDEU", rs.getString("estado"), "", rs.getString("desestado"));

                Difunto difunto = new Difunto();
                difunto.setCoddif(rs.getInt("coddif"));
                difunto.setNomdif(rs.getString("nomdif"));

                Cementerio cementerio = new Cementerio();
                cementerio.setCodcem(rs.getInt("codcem"));
                cementerio.setNomcem(rs.getString("nomcem"));

                Cuartel cuartel = null;
                Mausoleo mausoleo = null;
                Nicho_t nicho = null;
                if (rs.getString("tipent").equals("1")) {
                    cuartel = new Cuartel();
                    cuartel.setCementerio(cementerio);
                    cuartel.setCodcuar(rs.getInt("codcuar"));
                    cuartel.setNomcuar(rs.getString("nomcuar"));
//                    cuartel.setTipcuar(pTipCuar);
                    nicho = new Nicho_t();
                    nicho.setFila1(rs.getInt("fila1"));
                    nicho.setFila2(rs.getString("fila2"));
                    nicho.setCol1(rs.getInt("columna1"));
                    nicho.setCol2(rs.getInt("columna2"));
                } else if (rs.getString("tipent").equals("2")) {
                    mausoleo = new Mausoleo();
                    mausoleo.setCementerio(cementerio);
                    mausoleo.setCodmau(rs.getInt("codmau"));
                    mausoleo.setNomlote(rs.getString("lote"));
//                    mausoleo.setTipomau(pTipoMau);
                    mausoleo.setUbicacion(rs.getString("ubicacion"));
                    mausoleo.setFamilia(rs.getString("familia"));
                }

                Deuda deuda = new Deuda();
                deuda.setIddeu(rs.getInt("iddeu"));
                deuda.setFecdeu(rs.getString("fecdeu"));
                deuda.setDifunto(difunto);
                deuda.setNomcli(rs.getString("nomcli"));
                deuda.setDircli(rs.getString("dircli"));
                deuda.setTipent(tipent);
                deuda.setCementerio(cementerio);
                deuda.setCuartel(cuartel);
                deuda.setNicho(nicho);
                deuda.setMausoleo(mausoleo);
                deuda.setMonto(rs.getDouble("monto"));
                deuda.setEstado(estado);
                deuda.setUser(rs.getString("user"));
                deuda.setUsercr(rs.getString("usercr"));
                deuda.setDuser(rs.getString("duser"));
                deuda.setDusercr(rs.getString("dusercr"));
                return deuda;
            }
        }, new Object[]{iddeu});
        return lDeuda.size() > 0 ? (Deuda) lDeuda.get(0) : null;
    }

    @Override
    public List<Deuda> listaDeuda(String condicion) {
        String sql = "select d.iddeu, DATE_FORMAT(d.fecdeu, '%d/%m/%Y') fecdeu, d.coddif, d.nomdif, d.nomcli, d.dircli, d.tipent, te.descri destipent, "
                + "          d.codcem, c.nomcem, d.codcuar, cu.nomcuar, d.codnic, d.fila1, d.fila2, d.columna1, "
                + "          d.columna2, d.codmau, d.lote, d.ubicacion, d.familia, d.monto, d.estado, e.descri desestado, "
                + "          d.user, d.duser, d.usercr, d.dusercr "
                + "     FROM deudas d LEFT JOIN parmae te ON te.tipo='TIPENT' AND d.tipent=te.codigo "
                + "                   LEFT JOIN cementerio c ON c.codcem=d.codcem "
                + "                   LEFT JOIN cuartel cu ON cu.codcem=d.codcem AND cu.codcuar=d.codcuar "
                + "                   LEFT JOIN parmae e ON e.tipo='ESTDEU' AND d.estado=e.codigo "
                + "    where 1=1 " 
                + condicion;

//        System.out.println("Sql Deuda.-> " + sql);
        List<Deuda> lDeuda = jdbcTemplate.query(sql,
                new RowMapper<Deuda>() {
            @Override
            public Deuda mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                Parmae tipent = new Parmae("TIPENT", rs.getString("tipent"), "", rs.getString("destipent"));
                Parmae estado = new Parmae("ESTDEU", rs.getString("estado"), "", rs.getString("desestado"));

                Difunto difunto = new Difunto();
                difunto.setCoddif(rs.getInt("coddif"));
                difunto.setNomdif(rs.getString("nomdif"));

                Cementerio cementerio = new Cementerio();
                cementerio.setCodcem(rs.getInt("codcem"));
                cementerio.setNomcem(rs.getString("nomcem"));

                Cuartel cuartel = null;
                Mausoleo mausoleo = null;
                Nicho_t nicho = null;
                if (rs.getString("tipent").equals("1")) {
                    cuartel = new Cuartel();
                    cuartel.setCementerio(cementerio);
                    cuartel.setCodcuar(rs.getInt("codcuar"));
                    cuartel.setNomcuar(rs.getString("nomcuar"));
//                    cuartel.setTipcuar(pTipCuar);
                    nicho = new Nicho_t();
                    nicho.setFila1(rs.getInt("fila1"));
                    nicho.setFila2(rs.getString("fila2"));
                    nicho.setCol1(rs.getInt("columna1"));
                    nicho.setCol2(rs.getInt("columna2"));
                } else if (rs.getString("tipent").equals("2")) {
                    mausoleo = new Mausoleo();
                    mausoleo.setCementerio(cementerio);
                    mausoleo.setCodmau(rs.getInt("codmau"));
                    mausoleo.setNomlote(rs.getString("lote"));
//                    mausoleo.setTipomau(pTipoMau);
                    mausoleo.setUbicacion(rs.getString("ubicacion"));
                    mausoleo.setFamilia(rs.getString("familia"));
                }

                Deuda deuda = new Deuda();
                deuda.setIddeu(rs.getInt("iddeu"));
                deuda.setFecdeu(rs.getString("fecdeu"));
                deuda.setDifunto(difunto);
                deuda.setNomcli(rs.getString("nomcli"));
                deuda.setDircli(rs.getString("dircli"));
                deuda.setTipent(tipent);
                deuda.setCementerio(cementerio);
                deuda.setCuartel(cuartel);
                deuda.setNicho(nicho);
                deuda.setMausoleo(mausoleo);
                deuda.setMonto(rs.getDouble("monto"));
                deuda.setEstado(estado);
                deuda.setUser(rs.getString("user"));
                deuda.setUsercr(rs.getString("usercr"));
                deuda.setDuser(rs.getString("duser"));
                deuda.setDusercr(rs.getString("dusercr"));
                return deuda;
            }
        }, new Object[]{});
        return lDeuda.size() > 0 ? lDeuda : null;
    }
}