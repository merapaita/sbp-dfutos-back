package com.rosist.difunto.dao.impl;

import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cliente;
import com.rosist.difunto.modelSbp.Difunto;
import com.rosist.difunto.modelSbp.Mausoleo;
import com.rosist.difunto.modelSbp.Parmae;
import com.rosist.difunto.dao.MausoleoDao;
import com.rosist.difunto.dao.ClienteDao;
import com.rosist.difunto.dao.DifuntoDao;
import com.rosist.difunto.dao.ParmaeDao;
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
public class MausoleoImpl implements MausoleoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

	@Autowired
	private DifuntoDao daoDifunto;
	
    @Autowired
    private ParmaeDao daoParmae;

    @Autowired
    private ClienteDao daoCliente;

    private final static Logger logger = LoggerFactory.getLogger(MausoleoImpl.class);
    
    @Override
    public int getMausoleoCount() {
        String sql = "select count(*) as count from mausoleo";
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getMausoleoCount(String condicion) {
        String sql = "select count(*) as count from mausoleo where 1=1 " + condicion;
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getNewIdMausoleo(int codcem) {
    	String sql = "select ifnull(max(codmau),0)+1 from mausoleo where codcem=" + codcem;
        int nCorrel = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nCorrel;
    }

    @Override
    public Mausoleo insertaMausoleo(Mausoleo mausoleo) {
        int resp = 0, xFilas = 0, i;
        System.out.println("guadando...");
        System.out.println("guadando..." + mausoleo.toString());
        String sql = "insert into mausoleo(codcem, codmau, lotizado, nomlote, tipomau, ubicacion, familia, area_adq, area_cons, area_cerc, tipdoccli, doccli, nomcli, "
                + "      estado, totdif, numdif, estvta, observ) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        int xCodMau = getNewIdMausoleo(mausoleo.getCementerio().getCodcem());
        Parmae estado = new Parmae("ESTMAU", "00", "", "");
        Parmae estvta = new Parmae("ESTVTA", "00", "", "");
        mausoleo.setCodmau(xCodMau);
        mausoleo.setEstado(estado);
        mausoleo.setEstvta(estvta);

//        int xCliente = (mausoleo.getCliente()!=null?mausoleo.getCliente().getCodcli():0);
        resp = jdbcTemplate.update(sql,
                new Object[]{
                    mausoleo.getCementerio().getCodcem(), mausoleo.getCodmau(),
                    mausoleo.getLotizado(), mausoleo.getNomlote(),
                    mausoleo.getTipomau().getCodigo(), mausoleo.getUbicacion(),
                    mausoleo.getFamilia(), 
                    mausoleo.getArea_adq(), mausoleo.getArea_cons(), mausoleo.getArea_cerc(),
                    mausoleo.getCliente().getTipdoccli().getCodigo(), mausoleo.getCliente().getDoccli(),
                    mausoleo.getCliente().getNomcli(), mausoleo.getEstado().getCodigo(), mausoleo.getTotdif(), mausoleo.getNumdif(),
                    mausoleo.getEstvta().getCodigo(), mausoleo.getObserv()
                }
        );

        if (resp > 0) {
            Cliente xCliente = null;
            if (!mausoleo.getCliente().getTipdoccli().getCodigo().equals("0")) {
                xCliente = daoCliente.buscaCliente(mausoleo.getCliente().getTipdoccli().getCodigo(), mausoleo.getCliente().getDoccli());
            }

            if (xCliente == null && !mausoleo.getCliente().getTipdoccli().getCodigo().equals("0")) {
                sql = "insert into clientesunat(tipdoc, doccli, nomcli, dircli) values(?,?,?,?)";
                resp = jdbcTemplate.update(sql,
                        new Object[]{
                            mausoleo.getCliente().getTipdoccli().getCodigo(),
                            mausoleo.getCliente().getDoccli(),
                            mausoleo.getCliente().getNomcli(),
                            mausoleo.getCliente().getDircli()
                        });
            }

        }
		return (resp>0?buscaMausoleo(mausoleo.getCementerio().getCodcem(), mausoleo.getCodmau()):null);
    }

    @Override
    public Mausoleo modificaMausoleo(Mausoleo mausoleo) {
        int resp = 0;
        String sql = "update mausoleo set lotizado=?, nomlote=?, tipomau=?, ubicacion=?, familia=?, area_adq=?, area_cons=?, area_cerc=?, tipdoccli=?, doccli=?, nomcli=?, estado=?, totdif=?, numdif=?, observ=? where codcem=? and codmau=?";
        logger.info("MausoleoImpl.modificaMausoleo...mausoleo.-> " + mausoleo.toString());
        resp = jdbcTemplate.update(sql,
                new Object[]{
                    mausoleo.getLotizado(), mausoleo.getNomlote(),
                    mausoleo.getTipomau().getCodigo(), mausoleo.getUbicacion(),
                    mausoleo.getFamilia(), 
                    mausoleo.getArea_adq(), mausoleo.getArea_cons(), mausoleo.getArea_cerc(),
                    mausoleo.getCliente().getTipdoccli().getCodigo(), mausoleo.getCliente().getDoccli(),
                    mausoleo.getCliente().getNomcli(), mausoleo.getEstado().getCodigo(),
                    mausoleo.getTotdif(), mausoleo.getNumdif(),mausoleo.getObserv(),
                    mausoleo.getCementerio().getCodcem(), mausoleo.getCodmau()
                }
        );
        
        if (resp > 0) {
            Cliente xCliente = null;
            if (!mausoleo.getCliente().getTipdoccli().getCodigo().equals("0")) {
                xCliente = daoCliente.buscaCliente(mausoleo.getCliente().getTipdoccli().getCodigo(), mausoleo.getCliente().getDoccli());
            }

            if (xCliente == null && !mausoleo.getCliente().getTipdoccli().getCodigo().equals("0")) {
                sql = "insert into clientesunat(tipdoc, doccli, nomcli, dircli) values(?,?,?,?)";
                resp = jdbcTemplate.update(sql,
                        new Object[]{
                            mausoleo.getCliente().getTipdoccli().getCodigo(),
                            mausoleo.getCliente().getDoccli(),
                            mausoleo.getCliente().getNomcli(),
                            mausoleo.getCliente().getDircli()
                        });
            }
        }
		return (resp>0?buscaMausoleo(mausoleo.getCementerio().getCodcem(), mausoleo.getCodmau()):null);
    }

    @Override
    public String eliminaMausoleo(int codcem, int codmau) throws Exception {
        int resp = 0;
        String mRet = "", sql="";
        String cCondicion = " and d.codcem=" + codcem + " and d.codmau=" + codmau;
        List<Difunto> lDifuntos = daoDifunto.listaDifunto(cCondicion, "", "");
        if (lDifuntos!=null) {
        	mRet = "El Mausoleo ya tiene Difuntos";
        }
        if (!mRet.isEmpty()){
        	throw new Exception(mRet);
        }
        sql = "delete from mausoleo where codcem=? and codmau=?";
        resp = jdbcTemplate.update(sql, new Object[]{codcem, codmau});
        if (resp>0){
            mRet = "ok";
        }
        return mRet;
    }

    @Override
    public Mausoleo buscaMausoleo(int codcem, int codmau) {
        String sql = "SELECT ma.codcem, cem.nomcem, cem.local, ma.codmau, ma.lotizado, ma.nomlote, "
                + "          ma.tipomau, p.descri destipmau, ma.ubicacion, ma.familia, ma.area_adq, "
                + "          ma.area_cons, ma.area_cerc, ma.tipdoccli, ma.doccli, "
                + "          ma.nomcli, c.dircli, ma.estado, ma.totdif, ma.numdif, ma.estvta, ma.observ"
                + "     FROM mausoleo ma LEFT JOIN cementerio cem ON ma.codcem=cem.codcem"
                + "                      LEFT JOIN parmae p ON ma.tipomau=p.codigo AND p.tipo='TIPMAU'"
                + "                      LEFT JOIN parmae e ON ma.estado=e.codigo AND e.tipo='ESTMAU'"
                + "                      left join clientesunat c  on  c.tipdoc=ma.tipdoccli and c.doccli=ma.doccli"
                + "    where ma.codcem=? and ma.codmau=?";

        List<Mausoleo> matches = jdbcTemplate.query(sql,
                new RowMapper<Mausoleo>() {
            @Override
            public Mausoleo mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                Cementerio cementerio = new Cementerio();
                cementerio.setCodcem(rs.getInt("codcem"));
                cementerio.setNomcem(rs.getString("nomcem"));
                cementerio.setLocal(rs.getBoolean("local"));

                Parmae clTipMau = daoParmae.buscaParmae("TIPMAU", rs.getString("tipomau"), "");
                Parmae estMau = daoParmae.buscaParmae("ESTMAU", rs.getString("estado"), "");
                Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoccli"), "", "");
//                        Cliente cliente = servCliente.buscaCliente(rs.getInt("codcli"));

                Cliente cliente = null;
                String nomcli = "";

                if (!tipdoccli.getCodigo().equals("0")) {
                    cliente = daoCliente.buscaCliente(rs.getString("tipdoccli"), rs.getString("doccli"));
                } else {
                    cliente = new Cliente();
                    cliente.setTipdoccli(tipdoccli);
                    cliente.setNomcli(rs.getString("nomcli"));
                }
                nomcli = cliente.getNomcli();

                Mausoleo mausoleo = new Mausoleo();
                mausoleo.setCementerio(cementerio);
                mausoleo.setCodmau(rs.getInt("codmau"));
                mausoleo.setLotizado(rs.getString("lotizado"));
                mausoleo.setNomlote(rs.getString("nomlote"));
                mausoleo.setTipomau(clTipMau);
                mausoleo.setUbicacion(rs.getString("ubicacion"));
                mausoleo.setFamilia(rs.getString("familia"));
                mausoleo.setArea_adq(rs.getString("area_adq"));
                mausoleo.setArea_cons(rs.getString("area_cons"));
                mausoleo.setArea_cerc(rs.getString("area_cerc"));
                mausoleo.setEstado(estMau);
                mausoleo.setTotdif(rs.getInt("totdif"));
                mausoleo.setNumdif(rs.getInt("numdif"));
                mausoleo.setCliente(cliente);
                mausoleo.setNomcli(nomcli);
                mausoleo.setObserv(rs.getString("observ"));

                return mausoleo;
            }
        }, new Object[]{codcem, codmau});

        return matches.size() > 0 ? (Mausoleo) matches.get(0) : null;
    }

    @Override
    public List<Mausoleo> listaMausoleo(String condicion, String limit, String orden) {
        String sql = "SELECT ma.codcem, cem.nomcem, cem.local, ma.codmau, ma.lotizado, ma.nomlote, "
                + "          ma.tipomau, p.descri destipmau, ma.ubicacion, ma.familia, ma.area_adq, "
                + "          ma.area_cons, ma.area_cerc, ma.tipdoccli, "
                + "          ma.doccli, ma.nomcli, c.dircli, ma.estado, ma.totdif, ma.numdif, ma.estvta, ma.observ"
                + "     FROM mausoleo ma LEFT JOIN cementerio cem ON ma.codcem=cem.codcem"
                + "                      LEFT JOIN parmae p ON ma.tipomau=p.codigo AND p.tipo='TIPMAU'"
                + "                      LEFT JOIN parmae e ON ma.estado=e.codigo AND e.tipo='ESTMAU'"
                + "                      left join clientesunat c  on  c.tipdoc=ma.tipdoccli and c.doccli=ma.doccli"
                + " where 1=1" + condicion;
        
        if (!orden.isEmpty()){
            sql += " order by " + orden;
        }
        sql += (!limit.isEmpty() ? limit : " ");	// limit  0, 100 
        
//        System.out.println("sql mausoleo em impl.-=> " + sql);
        List<Mausoleo> matches = jdbcTemplate.query(sql,
                new RowMapper<Mausoleo>() {
            @Override
            public Mausoleo mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                Cementerio cementerio = new Cementerio();
                cementerio.setCodcem(rs.getInt("codcem"));
                cementerio.setNomcem(rs.getString("nomcem"));
                cementerio.setLocal(rs.getBoolean("local"));

                Parmae clTipMau = daoParmae.buscaParmae("TIPMAU", rs.getString("tipomau"), "");
                Parmae estMau = daoParmae.buscaParmae("ESTMAU", rs.getString("estado"), "");
//                        Cliente cliente = servCliente.buscaCliente(rs.getInt("codcli"));
                Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoccli"), "", "");
//                        Cliente cliente = servCliente.buscaCliente(rs.getInt("codcli"));

                Cliente cliente = null;
                String nomcli = "";

                if (!tipdoccli.getCodigo().equals("0")) {
//                    System.out.println("doc cli " + rs.getInt("tipdoccli") + "-" + rs.getString("doccli"));
                    cliente = daoCliente.buscaCliente(rs.getString("tipdoccli"), rs.getString("doccli"));
                } else {
                    cliente = new Cliente();
                    cliente.setTipdoccli(tipdoccli);
                    cliente.setNomcli(rs.getString("nomcli"));
                }
                nomcli = cliente.getNomcli();

                Mausoleo mausoleo = new Mausoleo();
                mausoleo.setCementerio(cementerio);
                mausoleo.setCodmau(rs.getInt("codmau"));
                mausoleo.setLotizado(rs.getString("lotizado"));
                mausoleo.setNomlote(rs.getString("nomlote"));
                mausoleo.setTipomau(clTipMau);
                mausoleo.setUbicacion(rs.getString("ubicacion"));
                mausoleo.setFamilia(rs.getString("familia"));
                mausoleo.setArea_adq(rs.getString("area_adq"));
                mausoleo.setArea_cons(rs.getString("area_cons"));
                mausoleo.setArea_cerc(rs.getString("area_cerc"));
                mausoleo.setEstado(estMau);
                mausoleo.setTotdif(rs.getInt("totdif"));
                mausoleo.setNumdif(rs.getInt("numdif"));
                mausoleo.setCliente(cliente);
                mausoleo.setNomcli(nomcli);
                mausoleo.setObserv(rs.getString("observ"));
//                        mausoleo.setEstvta(rs.getString("estvta"));

                return mausoleo;
            }
        }, new Object[]{});
        return matches.size() > 0 ? matches : null;
    }
}