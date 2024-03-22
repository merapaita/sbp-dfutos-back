package com.rosist.difunto.dao.impl;

import java.io.File;
//import com.sbpiura.difuntos.service.CpeSunatServicio;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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
import org.springframework.transaction.annotation.Transactional;

//import com.google.zxing.WriterException;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.rosist.difunto.dao.IngresoDao;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cliente;
import com.rosist.difunto.modelSbp.Cuartel;
import com.rosist.difunto.modelSbp.Difunto;
import com.rosist.difunto.modelSbp.Empresa;
import com.rosist.difunto.modelSbp.Ingreso;
import com.rosist.difunto.modelSbp.Iteing;
import com.rosist.difunto.modelSbp.Mausoleo;
import com.rosist.difunto.modelSbp.Nicho_t;
import com.rosist.difunto.modelSbp.Parmae;
import com.rosist.difunto.modelSbp.Partida;
import com.rosist.difunto.modelSbp.Servicio;
import com.rosist.difunto.modelSbp.Sucursal;
import com.rosist.difunto.modelSbp.Ubigeo;
import com.rosist.difunto.util.NumerosaLetras;
import com.rosist.difunto.util.impresora;
import com.rosist.difunto.dao.ClienteDao;
import com.rosist.difunto.dao.EmpresaDao;

@Repository
public class IngresoImpl implements IngresoDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ClienteDao daoCliente;
    @Autowired
    private EmpresaDao daoEmpresa;

    private final String ruc = "20147082861";
    static String drive = System.getenv("DRIVE_CPE");
    private static final Logger logger = LoggerFactory.getLogger(IngresoImpl.class);

    @Override
    public int getIngresoCount() {
    	int nNumReg=0;
        String sql = "select count(*) as count from ingreso ";
        nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }
    
    @Override
    public int getIngresoCount(String idIng, String tiping, String tipcli, String tipcom, String conser, String sersbp, String codsbp, String fecha, String estcomsbp) {
    	int nNumReg=0;
    	String sql = " select count(*) as count"
    			+ "      from ingreso i left join parmae tc on tc.tipo='TIPCLI' and i.tipcli=tc.codigo"
    			+ "                    left join parmae tcmp on tcmp.tipo='TIPCOM' and i.tipcom=tcmp.codigo"
    			+ "                    left join parmae cs on cs.tipo='CONSER' and i.conser=cs.codigo"
    			+ "                    left join parmae ec on ec.tipo='ESTSBP' and i.estado=ec.codigo"
    			+ "                    left join parmae t  on  t.tipo='TURNO ' and i.turno=t.codigo"
    			+ "                    left join clientes c  on  c.tipdoc=i.tipdoccli and c.doccli=i.doccli"
    			+ "     where 1=1 " 
    			+ (!idIng.isEmpty() ? " and iding='" + idIng + "'" : "")
    			+ (!tiping.isEmpty() ? " and tiping='" + tiping + "'" : "")
    			+ (!tipcli.isEmpty() ? " and tipcli='" + tipcli + "'" : "")
    			+ (!tipcom.isEmpty() ? " and tipcom='" + tipcom + "'" : "")
    			+ (!conser.isEmpty() ? " and conser='" + conser + "'" : "")
    			+ (!sersbp.isEmpty() ? " and sersbp='" + sersbp + "'" : "")
    			+ (!codsbp.isEmpty() ? " and codsbp='" + codsbp + "'" : "")
    			+ (!fecha.isEmpty() ? " and fecha='" + fecha + "'" : "")
    			+ (!estcomsbp.isEmpty() ? " and estcomsbp='" + estcomsbp + "'" : "")
    			;
    	nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
    	return nNumReg;
    }
    
    @Override
    public int getNewIdIngreso(int tiping) {
        String sql = "select ifnull(max(correl),0)+1 from ingreso where tiping=?";
        int nCorrel = this.jdbcTemplate.queryForObject(sql, Integer.class, new Object[]{tiping});
        return nCorrel;
    }

    private String getSerieCP(int tiping, String tipcom) {
        String cDigito="", cRet="", sqlSer="", sCorrel="";
        String sql = "select serie from sucursal where tiping=?";
        int nSerie = this.jdbcTemplate.queryForObject(sql, Integer.class, new Object[]{tiping});
        if (tipcom.equals("FAC")){
            cDigito = "F";
            cRet = cDigito + String.format("%1$03d", nSerie);
        } else if (tipcom.equals("B/V")) {
            cDigito = "B";
            cRet = cDigito + String.format("%1$03d", nSerie);
        } else if (tipcom.equals("REC")){
            cRet = String.format("%1$04d", nSerie);
        }
        return cRet;
    }

    @Override
    public String getNewCodSBP(String cSerie, String tipcom) {
        String sql = "select ifnull(max(codsbp),0)+1 from ingreso where sersbp=? and tipcom=?";
        int nCorrel = this.jdbcTemplate.queryForObject(sql, Integer.class, new Object[]{cSerie, tipcom});
        String sCorrel = String.format("%1$07d", nCorrel);
        return sCorrel;
    }

    @Override
    @Transactional
    public Ingreso insertaIngreso(Ingreso ingreso) {
        int resp = 0, nDetalles = 0, i = 0;
        String sql = "insert into ingreso(iding, tiping, correl, tipcli, tipcom, conser, sersbp, codsbp, "
                + "      fecha, turno, tipdoccli, doccli, nomcli, coddif, nomdif, tipent, codcem, codcuar, codnic, "
                + "      fila1, fila2, columna1, columna2, codmau, lote, ubicacion, familia, "
                + "      mtosbpgr, mtosbpex, mtosbpin, mtotot, dscsbppor, dscsbp, dscigv, "
                + "      mtocp, basimpgr, basimpex, basimpin, igv, liquida, estado, user, duser) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String sqlDet = "insert into iteing(iding, item, tipser, idser, descri, codpart, tipope, mtosbp, total, "
                + "      dscsbppor, dscsbp, dscigv, mtocp, basimp, igv, user, duser) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        int xTipIng = ingreso.getTiping().getTiping();
        String cTipCom = ingreso.getTipocomprobante().getCodigo();
        int xCorrel = getNewIdIngreso(xTipIng);

        String sSerCP = getSerieCP(xTipIng, cTipCom);
        String sCodCP = getNewCodSBP(sSerCP, cTipCom);
        
        ingreso.setSersbp(sSerCP);
        ingreso.setCodsbp(sCodCP);
        
        String xIdIng = String.format("%1$02d", Integer.valueOf(ingreso.getTiping().getTiping())) + String.format("%1$07d", xCorrel);
        ingreso.setIding(xIdIng);
        Parmae xEst = new Parmae("", "00","","");
        ingreso.setEstado(xEst);
        String xCodNic = "";
        int xCodCuar=0;
        if (ingreso.getDifunto().getTipo_entierro().getCodigo().equals("1")) {
            xCodCuar = ingreso.getDifunto().getCuartel().getCodcuar();
            xCodNic = ingreso.getDifunto().getNicho().getFila1() + String.format("%1$03d", ingreso.getDifunto().getNicho().getCol1());
        } else if (ingreso.getDifunto().getTipo_entierro().getCodigo().equals("2")) {
        }

        String cUser = "usuario";
//        String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Date dUser = new Date();

        resp = jdbcTemplate.update(sql,
                new Object[]{
                    xIdIng, ingreso.getTiping().getTiping(), xCorrel, ingreso.getTipcli().getCodigo(),
                    ingreso.getTipocomprobante().getCodigo(), ingreso.getConser().getCodigo(),
                    ingreso.getSersbp(), ingreso.getCodsbp(), ingreso.getFecha(), ingreso.getTurno().getCodigo(),
                    ingreso.getCliente().getTipdoccli().getCodigo(), ingreso.getCliente().getDoccli(), 
                    ingreso.getCliente().getNomcli(), ingreso.getDifunto().getCoddif(), ingreso.getDifunto().getNomdif(), 
                    ingreso.getDifunto().getTipo_entierro().getCodigo(),
                    ingreso.getDifunto().getCementerio().getCodcem(), xCodCuar,
                    xCodNic, ingreso.getDifunto().getNicho().getFila1(), ingreso.getDifunto().getNicho().getFila2(),
                    ingreso.getDifunto().getNicho().getCol1(), ingreso.getDifunto().getNicho().getCol2(),
                    ingreso.getDifunto().getMausoleo().getCodmau(), ingreso.getDifunto().getMausoleo().getNomlote(),
                    ingreso.getDifunto().getMausoleo().getUbicacion(), ingreso.getDifunto().getMausoleo().getFamilia(),
                    ingreso.getMtosbpgr(), ingreso.getMtosbpex(), ingreso.getMtosbpin(), ingreso.getMtotot(), ingreso.getDscsbppor(), ingreso.getDscsbp(), 
                    ingreso.getDscigv(), ingreso.getMtocp(), ingreso.getBasimpgr(), ingreso.getBasimpex(), ingreso.getBasimpin(), ingreso.getIgv(),
                    ingreso.isLiquida(), ingreso.getEstado().getCodigo(), cUser, dUser 
                });
        if (ingreso.getDetiteing() != null) {
            nDetalles = ingreso.getDetiteing().size();
            for (i = 0; i < nDetalles; i++) {
                if (ingreso.getDetiteing().get(i).getRemove() == 0) {
                    resp = jdbcTemplate.update(sqlDet,
                            new Object[]{
                                ingreso.getIding(), i + 1, ingreso.getDetiteing().get(i).getTipser().getCodigo(),
                                ingreso.getDetiteing().get(i).getServicio().getIdser(), ingreso.getDetiteing().get(i).getDescri(), 
                                ingreso.getDetiteing().get(i).getPartida().getCodpart(), ingreso.getDetiteing().get(i).getServicio().getTipoOperacion().getCodigo(),
                                ingreso.getDetiteing().get(i).getMtosbp(), ingreso.getDetiteing().get(i).getTotal(),
                                ingreso.getDetiteing().get(i).getDscsbppor(), ingreso.getDetiteing().get(i).getDscsbp(), 
                                ingreso.getDetiteing().get(i).getDscigv(), ingreso.getDetiteing().get(i).getMtocp(),
                                ingreso.getDetiteing().get(i).getBasimp(), ingreso.getDetiteing().get(i).getIgv(),
                                cUser, dUser
                            });
                }
            }
        }

        if (resp != 0) {
            int correlSBP = 0;
            String tipCom = "";

            tipCom = ingreso.getTipocomprobante().getCodigo();
            correlSBP = Integer.valueOf(ingreso.getCodsbp());

            if (correlSBP != 0) {
                sqlDet = "update parmae set codigoaux=? where tipo='CORREL' AND codigo=? ";
                resp = jdbcTemplate.update(sqlDet,
                        new Object[]{
                            correlSBP, tipCom
                        });
            }

            Cliente xCliente = null;
            if (!ingreso.getCliente().getTipdoccli().getCodigo().equals("0")) {
                xCliente = daoCliente.buscaCliente(ingreso.getCliente().getTipdoccli().getCodigo(), ingreso.getCliente().getDoccli());
            }

            if (xCliente == null && !ingreso.getCliente().getTipdoccli().getCodigo().equals("0")) {
                sql = "insert into clientesunat(tipdoc, doccli, nomcli, dircli) values(?,?,?,?)";
                resp = jdbcTemplate.update(sql,
                        new Object[]{
                            ingreso.getCliente().getTipdoccli().getCodigo(),
                            ingreso.getCliente().getDoccli(),
                            ingreso.getCliente().getNomcli(),
                            ingreso.getCliente().getDircli()
                        });
            }
        }
		return (resp>0?buscaIngreso(xIdIng):null);
    }

    @Override
    public Ingreso modificaIngreso(Ingreso ingreso) {
        int resp = 0, nDetalles = 0;
        String sql, sqlDet;
        String cUser = "usuario";
//        String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Date dUser = new Date();
        sql = "update ingreso set turno=?, nomcli=?, usercr=?, dusercr=? where iding=?";
        resp = jdbcTemplate.update(sql,
                new Object[]{
                    ingreso.getTurno().getCodigo(), ingreso.getCliente().getNomcli(), cUser, dUser, ingreso.getIding()
                });
		return (resp>0?buscaIngreso(ingreso.getIding()):null);
    }

    @Override
    public String eliminaIngreso(String iding) {
        int resp = 0;
        Ingreso ingreso = buscaIngreso(iding);
		String mRet = "";

		if (mRet.isEmpty()) {
	        if (ingreso.getDetiteing() == null || ingreso.getDetiteing() == null) {
	            String sql = "delete from ingreso where iding=?";
	            resp = jdbcTemplate.update(sql, new Object[]{iding});
	        }
			if (resp != 0) {
				mRet = "ok";
			}
		}
        return mRet;
    }

    @Override
    public String anulaIngreso(String iding) {
        int resp = 0;
		String mRet = "";
		
		Ingreso ingreso = buscaIngreso(iding);
		
		if (mRet.isEmpty()) {
	        String sql = "update ingreso set estado=? where iding=?";
	        resp = jdbcTemplate.update(sql,
	                new Object[]{
	                    "99", iding
	                });
		}
		if (resp != 0) {
			mRet = "ok";
		}

		return mRet;
    }

    @Override
    public Ingreso buscaIngreso(String iding) {
        String sql = "select i.iding, i.tiping, i.correl, i.tipcli, tc.descri destipcli, i.tipcom, tcmp.descri destipcom,"
                + "          i.conser, cs.descri desconser, i.sersbp, i.codsbp, date_format(i.fecha,'%d/%m/%Y') fecha, "
                + "          i.coddif, i.turno, t.descri destur, i.tipdoccli, i.doccli, i.nomcli, "
                + "          c.dircli, i.mtosbpgr, i.mtosbpex, i.mtosbpin,"
                + "          i.mtotot, i.dscsbppor, i.dscsbp, i.dscigv, i.mtocp, i.basimpgr,"
                + "          i.basimpex, i.basimpin, i.igv, i.liquida, i.idliq, i.estado, ec.descri desestsbp, "
                + "          i.user, i.usercr, i.duser, i.dusercr"
                + "     from ingreso i left join parmae tc on tc.tipo='TIPCLI' and i.tipcli=tc.codigo"
                + "                    left join parmae tcmp on tcmp.tipo='TIPCOM' and i.tipcom=tcmp.codigo"
                + "                    left join parmae cs on cs.tipo='CONSER' and i.conser=cs.codigo"
                + "                    left join parmae ec on ec.tipo='ESTSBP' and i.estado=ec.codigo"
                + "                    left join parmae t  on  t.tipo='TURNO ' and i.turno=t.codigo"
                + "                    left join clientesunat c  on  c.tipdoc=i.tipdoccli and c.doccli=i.doccli"
                + "    where iding=?";
        
        //System.out.println("sql busca ingreso " + sql);
        List<Ingreso> matches = jdbcTemplate.query(sql,
                new RowMapper<Ingreso>() {
            @Override
            public Ingreso mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                Parmae tipcli = new Parmae("TIPCLI", rs.getString("tipcli"), "", rs.getString("destipcli"));
                Parmae tipcom = new Parmae("TIPCOM", rs.getString("tipcom"), "", rs.getString("destipcom"));
                /*cambiarla por sucursal*/

                Sucursal sucursal = null;
                Parmae conser = new Parmae("CONSER", rs.getString("conser"), "", rs.getString("desconser"));
                /*dni, direccion y estado*/
                Parmae estado = new Parmae("ESTSBP", rs.getString("estado"), "", rs.getString("desestsbp"));
                Parmae turno = new Parmae("TURNO ", rs.getString("turno"), "", rs.getString("destur"));
                Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoccli"), "", "");

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

                Ingreso ingreso = new Ingreso();
                ingreso.setIding(rs.getString("iding"));

                String sqlSucursal = "select s.tiping, s.descri, s.coddep, u.nomdep, s.codprv, u.nomprv, s.coddis, u.nomdis,"
                        + "          s.direcc, s.telefono, s.fax, s.email "
                        + "     from sucursal s left join ubigeo u on s.coddep=u.coddep and s.codprv=u.codprv and "
                        + "                                           s.coddis=u.coddis"
                        + " where s.tiping=?";

                List<Sucursal> lSucursal = jdbcTemplate.query(sqlSucursal,
                        new RowMapper<Sucursal>() {
                    @Override
                    public Sucursal mapRow(ResultSet rsSuc, int rowNum) throws SQLException, DataAccessException {
                        Ubigeo ubigeo = new Ubigeo();
                        ubigeo.setUbigeo(rsSuc.getString("coddep") + rsSuc.getString("codprv") + rsSuc.getString("coddis"));
                        ubigeo.setCoddep(rsSuc.getString("coddep"));
                        ubigeo.setCodprv(rsSuc.getString("codprv"));
                        ubigeo.setCoddis(rsSuc.getString("coddis"));
                        ubigeo.setNomdep(rsSuc.getString("nomdep"));
                        ubigeo.setNomprv(rsSuc.getString("nomprv"));
                        ubigeo.setNomdis(rsSuc.getString("nomdis"));

                        Sucursal sucursal = new Sucursal();
                        sucursal.setTiping(rsSuc.getInt("tiping"));
                        sucursal.setDescri(rsSuc.getString("descri"));
                        sucursal.setUbigeo(ubigeo);
                        sucursal.setDireccion(rsSuc.getString("direcc"));
                        sucursal.setTelefono(rsSuc.getString("telefono"));
                        sucursal.setFax(rsSuc.getString("fax"));
                        sucursal.setEmail(rsSuc.getString("email"));

                        return sucursal;
                    }
                }, new Object[]{rs.getInt("tiping")});

                sucursal = lSucursal.size() > 0 ? (Sucursal) lSucursal.get(0) : null;
                ingreso.setTiping(sucursal);
                ingreso.setCorrel(rs.getInt("correl"));
                ingreso.setTipcli(tipcli);
                ingreso.setTipocomprobante(tipcom);
                ingreso.setConser(conser);
                ingreso.setSersbp(rs.getString("sersbp"));
                ingreso.setCodsbp(rs.getString("codsbp"));
                ingreso.setFecha(rs.getString("fecha"));
                ingreso.setTurno(turno);
                ingreso.setCliente(cliente);
                ingreso.setNomcli(nomcli);
                
                Difunto difunto = new Difunto();
                
                String sqlDifunto = "select d.codcem, cem.nomcem, cem.local, d.codcuar, cu.nomcuar, cu.tipcuar, p.descri destipcuar, cu.filas, cu.columnas,"
                        + "                 d.codmau, m.tipomau, p3.descri destipmau, m.lotizado, m.nomlote, m.familia, m.ubicacion,"
                        + "                 d.tipent, p2.descri destipent, d.coddif, date_format(d.fecfall,'%d/%m/%Y') fecfall, date_format(d.fecsep,'%d/%m/%Y') fecsep, d.apepat, d.apemat, d.nombres, "
                        + "                 CONCAT(TRIM(d.apepat),' ',TRIM(d.apemat),', ',TRIM(d.nombres)) nomdif, d.edad_a, d.edad_m, d.edad_d, d.sexo, d.reservado, d.codocu, d.codnic, d.fila1, d.fila2, d.columna1, d.columna2, d.estado, d.estvta, d.observ"
                        + "                 "
                        + "            from difunto d left join cementerio cem on d.codcem =cem.codcem"
                        + "                           left join cuartel    cu  on d.codcem=cu.codcem and d.codcuar=cu.codcuar"
                        + "                           left join mausoleo   m   on d.codmau=m.codmau"
                        + "                           left join parmae     p   on p.tipo='TIPCUA'  and cu.tipcuar=p.codigo"
                        + "                           left join parmae     p2  on p2.tipo='TIPENT' and d.tipent=p2.codigo"
                        + "                           left join parmae     p3  on p3.tipo='TIPMAU' and m.tipomau=p3.codigo"
                        + " where coddif=?";

                List<Difunto> lDifunto = jdbcTemplate.query(sqlDifunto,
                        new RowMapper<Difunto>() {
                    @Override
                    public Difunto mapRow(ResultSet rsDif, int rowNum) throws SQLException, DataAccessException {
                        
                        Cementerio cementerio = new Cementerio();
                        cementerio.setCodcem(rsDif.getInt("codcem"));
                        cementerio.setNomcem(rsDif.getString("nomcem"));
                        cementerio.setLocal(rsDif.getBoolean("local"));

                        Parmae pTipCuar = new Parmae();
                        pTipCuar.setTipo("TIPCUA");
                        pTipCuar.setCodigo(rsDif.getString("tipcuar"));
                        pTipCuar.setDescri(rsDif.getString("destipcuar"));
                        
                        Parmae estDif = new Parmae("ESTDIF", rsDif.getString("estado"), "", "");
                        
                        Cuartel cuartel = new Cuartel();
                        Nicho_t nicho = new Nicho_t();
                        Mausoleo mausoleo = new Mausoleo();
                        
                        if (rsDif.getString("tipent").equals("1")){
                            cuartel.setCementerio(cementerio);
                            cuartel.setCodcuar(rsDif.getInt("codcuar"));
                            cuartel.setNomcuar(rsDif.getString("nomcuar"));
                            cuartel.setTipcuar(pTipCuar);
                            cuartel.setFilas(rsDif.getInt("filas"));
                            cuartel.setColumnas(rsDif.getInt("columnas"));
                            
                            nicho.setFila1(rsDif.getInt("fila1"));
                            nicho.setFila2(rsDif.getString("fila2"));
                            nicho.setCol1(rsDif.getInt("columna1"));
                            nicho.setCol2(rsDif.getInt("columna2"));
                        } else if (rsDif.getString("tipent").equals("2")){
                            Parmae pTipoMau = new Parmae();
                            pTipoMau.setTipo("TIPMAU");
                            pTipoMau.setCodigo(rsDif.getString("tipomau"));
                            pTipoMau.setDescri(rsDif.getString("destipmau"));
                        
                            mausoleo.setCementerio(cementerio);
                            mausoleo.setCodmau(rsDif.getInt("codmau"));
                            mausoleo.setLotizado(rsDif.getString("lotizado"));
                            mausoleo.setNomlote(rsDif.getString("nomlote"));
                            mausoleo.setTipomau(pTipoMau);
                            mausoleo.setUbicacion(rsDif.getString("ubicacion"));
                            mausoleo.setFamilia(rsDif.getString("familia"));
                        }
                        
                        Parmae pTipoEnt = new Parmae();
                        pTipoEnt.setTipo("TIPENT");
                        pTipoEnt.setCodigo(rsDif.getString("tipent"));
                        pTipoEnt.setDescri(rsDif.getString("destipent"));
                        
                        Difunto difunto = new Difunto();
                        difunto.setCoddif(rsDif.getInt("coddif"));
                        difunto.setApepat(rsDif.getString("apepat"));
                        difunto.setApemat(rsDif.getString("apemat"));
                        difunto.setNomdif(rsDif.getString("nomdif"));
                        difunto.setNombres(rsDif.getString("nombres"));
                        difunto.setReservado(rsDif.getString("reservado"));
                        difunto.setEstado(estDif);
                        difunto.setFecfall(rsDif.getString("fecfall"));
                        difunto.setFecsep(rsDif.getString("fecsep"));
                        difunto.setEdad_a(rsDif.getInt("edad_a"));
                        difunto.setEdad_m(rsDif.getInt("edad_m"));
                        difunto.setEdad_d(rsDif.getInt("edad_d"));
                        difunto.setSexodif(rsDif.getString("sexo"));
                        difunto.setTipo_entierro(pTipoEnt);
                        difunto.setCementerio(cementerio);
                        difunto.setCuartel(cuartel);
                        difunto.setNicho(nicho);
                        difunto.setMausoleo(mausoleo);
                        difunto.setObserv(rsDif.getString("observ"));
                        return difunto;
                    }
                }, new Object[]{rs.getInt("coddif")});
                difunto = lDifunto.size() > 0 ? (Difunto) lDifunto.get(0) : null;

                ingreso.setDifunto(difunto);
                ingreso.setMtosbpgr(rs.getDouble("mtosbpgr"));
                ingreso.setMtosbpex(rs.getDouble("mtosbpex"));
                ingreso.setMtotot(rs.getDouble("mtotot"));
                ingreso.setDscsbppor(rs.getDouble("dscsbppor"));
                ingreso.setDscsbp(rs.getDouble("dscsbp"));
                ingreso.setDscigv(rs.getDouble("dscigv"));
                ingreso.setMtocp(rs.getDouble("mtocp"));
                ingreso.setBasimpgr(rs.getDouble("basimpgr"));
                ingreso.setBasimpex(rs.getDouble("basimpex"));
                ingreso.setIgv(rs.getDouble("igv"));
                ingreso.setLiquida(rs.getBoolean("liquida"));
                ingreso.setIdliq(rs.getString("idliq"));
                ingreso.setEstado(estado);
                ingreso.setUser(rs.getString("user"));
                ingreso.setDuser(rs.getString("duser"));
                ingreso.setUsercr(rs.getString("usercr"));
                ingreso.setDusercr(rs.getString("dusercr"));

                List<Iteing> detalles = jdbcTemplate.query(""
                        + "select ii.iding, ii.item, ii.tipser, ts.descri destipser, ii.idser, s.desser desser, ii.descri desadi, "
                        + "       ii.codpart, p.descri descodpart, ii.tipope, top.descri destipope, ii.mtosbp, ii.total, ii.dscsbppor, "
                        + "       ii.dscsbp, ii.dscigv, ii.mtocp, ii.basimp, ii.igv, liquida, idliq "
                        + "  from iteing ii  left join parmae ts on ts.tipo='TIPSER' and ii.tipser=ts.codigo"
                        + "                  left join parmae top on top.tipo='TIPOPE' and ii.tipope=top.codigo"
                        + "                  left join servicios s  on ii.idser=s.idser"
                        + "                  left join partida p  on ii.codpart=p.codpart"
                        + " where iding=?",
                        new RowMapper<Iteing>() {
                    public Iteing mapRow(ResultSet rsDet, int rowNumDet) throws SQLException, DataAccessException {
                        Parmae tipser = new Parmae("TIPSER", rsDet.getString("tipser"), "", rsDet.getString("destipser"));
                        Parmae tipope = new Parmae("TIPOPE", rsDet.getString("tipope"), "", rsDet.getString("destipope"));
                        Partida partida = new Partida(rsDet.getString("codpart"), rsDet.getString("descodpart"));
                        int xCorrel = Integer.valueOf(rsDet.getString("idser").substring(2, 5));
                        String sDesSer = (rsDet.getString("desser") != null ? rsDet.getString("desser") : "Error en Descripcion");
                        Servicio servicio = new Servicio(rsDet.getString("idser"), tipser, xCorrel, sDesSer, 0.00, 0.00, 0.00, 0.00, 0.00, partida, tipope);

                        Iteing iteing = new Iteing();
                        iteing.setItem(rsDet.getInt("item"));
                        iteing.setTipser(tipser);
                        iteing.setServicio(servicio);
                        iteing.setDescri(rsDet.getString("desadi"));
                        iteing.setPartida(partida);
                        iteing.setMtosbp(rsDet.getDouble("mtosbp"));
                        iteing.setTotal(rsDet.getDouble("total"));
                        iteing.setDscsbppor(rsDet.getDouble("dscsbppor"));
                        iteing.setDscsbp(rsDet.getDouble("dscsbp"));
                        iteing.setDscigv(rsDet.getDouble("dscigv"));
                        iteing.setMtocp(rsDet.getDouble("mtocp"));
                        iteing.setBasimp(rsDet.getDouble("basimp"));
                        iteing.setIgv(rsDet.getDouble("igv"));
                        iteing.setLiquida(rsDet.getInt("liquida"));
                        iteing.setIdliq(rsDet.getString("idliq"));
                        return iteing;
                    }
                }, new Object[]{ingreso.getIding()});
                List<Iteing> listaDetalles = detalles.size() > 0 ? detalles : null;
                ingreso.setDetiteing(listaDetalles);
                return ingreso;
            }
        }, new Object[]{iding});
        return matches.size() > 0 ? (Ingreso) matches.get(0) : null;
    }

    @Override
	public List<Ingreso> listaIngresos(String idIng, String tiping, String tipcli, String tipcom, String conser, String sersbp, String codsbp, String fecha, String estcomsbp, String orden, Integer page, Integer size) {
        String sql = "select i.iding, i.tiping, i.correl, i.tipcli, tc.descri destipcli, i.tipcom, tcmp.descri destipcom,"
                + "          i.conser, cs.descri desconser, i.sersbp, i.codsbp, date_format(i.fecha,'%d/%m/%Y') fecha, "
                + "          i.coddif, i.turno, t.descri destur, i.tipdoccli, i.doccli, i.nomcli, "
                + "          c.dircli, i.mtosbpgr, i.mtosbpex, i.mtosbpin, i.mtotot, i.dscsbppor, i.dscsbp, i.dscigv, i.mtocp, i.basimpgr,"
                + "          i.basimpex, i.basimpin, i.igv, i.liquida, i.idliq, i.estado, ec.descri desestsbp, "
                + "          i.user, i.usercr, i.duser, i.dusercr"
                + "     from ingreso i left join parmae tc on tc.tipo='TIPCLI' and i.tipcli=tc.codigo"
                + "                    left join parmae tcmp on tcmp.tipo='TIPCOM' and i.tipcom=tcmp.codigo"
                + "                    left join parmae cs on cs.tipo='CONSER' and i.conser=cs.codigo"
                + "                    left join parmae ec on ec.tipo='ESTSBP' and i.estado=ec.codigo"
                + "                    left join parmae t  on t.tipo='TURNO ' and i.turno=t.codigo"
                + "                    left join clientesunat c on c.tipdoc=i.tipdoccli and c.doccli=i.doccli"
                + "     where 1=1 "
				+ (!idIng.isEmpty() ? " and idIng='" + idIng + "'" : "")
				+ (!tiping.isEmpty() ? " and tiping='" + tiping + "'" : "")
				+ (!tipcli.isEmpty() ? " and tipcli='" + tipcli + "'" : "")
				+ (!tipcom.isEmpty() ? " and tipcom='" + tipcom + "'" : "")
				+ (!conser.isEmpty() ? " and conser='" + conser + "'" : "")
				+ (!sersbp.isEmpty() ? " and sersbp='" + sersbp + "'" : "")
				+ (!codsbp.isEmpty() ? " and codsbp='" + codsbp + "'" : "")
				+ (!fecha.isEmpty() ? " and fecha='" + fecha + "'" : "")
				+ (!estcomsbp.isEmpty() ? " and estcomsbp='" + estcomsbp + "'" : "")
				+ (!orden.isEmpty() ? " order by iding " + orden : "")
                + (page>-1?" limit " + page*size + ", " + size:"");

        List<Ingreso> matches = jdbcTemplate.query(sql,
                new RowMapper<Ingreso>() {
            @Override
            public Ingreso mapRow(ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                Parmae tipcli = new Parmae("TIPCLI", rs.getString("tipcli"), "", rs.getString("destipcli"));
                Parmae tipcom = new Parmae("TIPCOM", rs.getString("tipcom"), "", rs.getString("destipcom"));

                Sucursal sucursal = null;

                Parmae conser = new Parmae("CONSER", rs.getString("conser"), "", rs.getString("desconser"));
                Parmae estado = new Parmae("ESTSBP", rs.getString("estado"), "", rs.getString("desestsbp"));
                Parmae turno = new Parmae("TURNO ", rs.getString("turno"), "", rs.getString("destur"));

                Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoccli"), "", "");
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
                
                Ingreso ingreso = new Ingreso();
                ingreso.setIding(rs.getString("iding"));
                ingreso.setTipcli(tipcli);

                String sqlSucursal = "select s.tiping, s.descri, s.coddep, u.nomdep, s.codprv, u.nomprv, s.coddis, u.nomdis,"
                        + "          s.direcc, s.telefono, s.fax, s.email "
                        + "     from sucursal s left join ubigeo u on s.coddep=u.coddep and s.codprv=u.codprv and "
                        + "                                           s.coddis=u.coddis"
                        + " where s.tiping=?";

                List<Sucursal> lSucursal = jdbcTemplate.query(sqlSucursal,
                        new RowMapper<Sucursal>() {
                    @Override
                    public Sucursal mapRow(ResultSet rsSuc, int rowNum) throws SQLException, DataAccessException {
                        Ubigeo ubigeo = new Ubigeo();
                        ubigeo.setUbigeo(rsSuc.getString("coddep") + rsSuc.getString("codprv") + rsSuc.getString("coddis"));
                        ubigeo.setCoddep(rsSuc.getString("coddep"));
                        ubigeo.setCodprv(rsSuc.getString("codprv"));
                        ubigeo.setCoddis(rsSuc.getString("coddis"));
                        ubigeo.setNomdep(rsSuc.getString("nomdep"));
                        ubigeo.setNomprv(rsSuc.getString("nomprv"));
                        ubigeo.setNomdis(rsSuc.getString("nomdis"));

                        Sucursal sucursal = new Sucursal();
                        sucursal.setTiping(rsSuc.getInt("tiping"));
                        sucursal.setDescri(rsSuc.getString("descri"));
                        sucursal.setUbigeo(ubigeo);
                        sucursal.setDireccion(rsSuc.getString("direcc"));
                        sucursal.setTelefono(rsSuc.getString("telefono"));
                        sucursal.setFax(rsSuc.getString("fax"));
                        sucursal.setEmail(rsSuc.getString("email"));

                        return sucursal;
                    }
                }, new Object[]{rs.getInt("tiping")});
                sucursal = lSucursal.size() > 0 ? (Sucursal) lSucursal.get(0) : null;
                ingreso.setTiping(sucursal);
                
                ingreso.setCorrel(rs.getInt("correl"));
                ingreso.setTipocomprobante(tipcom);
                ingreso.setConser(conser);
                ingreso.setSersbp(rs.getString("sersbp"));
                ingreso.setCodsbp(rs.getString("codsbp"));
                ingreso.setFecha(rs.getString("fecha"));
                ingreso.setTurno(turno);
                ingreso.setCliente(cliente);
                ingreso.setNomcli(nomcli);
                
                Difunto difunto = new Difunto();
                
                String sqlDifunto = "select d.codcem, cem.nomcem, cem.local, d.codcuar, cu.nomcuar, cu.tipcuar, p.descri destipcuar, cu.filas, cu.columnas,"
                        + "                 d.codmau, m.tipomau, p3.descri destipmau, m.lotizado, m.nomlote, m.familia, m.ubicacion,"
                        + "                 d.tipent, p2.descri destipent, d.coddif, date_format(d.fecfall,'%d/%m/%Y') fecfall, date_format(d.fecsep,'%d/%m/%Y') fecsep, d.apepat, d.apemat, d.nombres, "
                        + "                 CONCAT(TRIM(d.apepat),' ',TRIM(d.apemat),', ',TRIM(d.nombres)) nomdif, d.edad_a, d.edad_m, d.edad_d, d.sexo, d.reservado, d.codocu, d.codnic, d.fila1, d.fila2, d.columna1, d.columna2, d.estado, d.estvta, d.observ"
                        + "                 "
                        + "            from difunto d left join cementerio cem on d.codcem =cem.codcem"
                        + "                  left join cuartel    cu  on d.codcem=cu.codcem and d.codcuar=cu.codcuar"
                        + "                  left join mausoleo   m   on d.codmau=m.codmau"
                        + "                  left join parmae p  on p.tipo='TIPCUA'  and cu.tipcuar=p.codigo"
                        + "                  left join parmae p2 on p2.tipo='TIPENT' and d.tipent=p2.codigo"
                        + "                  left join parmae p3 on p3.tipo='TIPMAU' and m.tipomau=p3.codigo"
                        + " where coddif=?";

                List<Difunto> lDifunto = jdbcTemplate.query(sqlDifunto,
                        new RowMapper<Difunto>() {
                    @Override
                    public Difunto mapRow(ResultSet rsDif, int rowNum) throws SQLException, DataAccessException {
                        
                        Cementerio cementerio = new Cementerio();
                        cementerio.setCodcem(rsDif.getInt("codcem"));
                        cementerio.setNomcem(rsDif.getString("nomcem"));
                        cementerio.setLocal(rsDif.getBoolean("local"));

                        Parmae pTipCuar = new Parmae();
                        pTipCuar.setTipo("TIPCUA");
                        pTipCuar.setCodigo(rsDif.getString("tipcuar"));
                        pTipCuar.setDescri(rsDif.getString("destipcuar"));
                        
                        Parmae estDif = new Parmae("ESTDIF", rsDif.getString("estado"), "", "");
                        
                        Cuartel cuartel = new Cuartel();
                        Nicho_t nicho = new Nicho_t();
                        Mausoleo mausoleo = new Mausoleo();
                        
                        if (rsDif.getString("tipent").equals("1")){
                            cuartel.setCementerio(cementerio);
                            cuartel.setCodcuar(rsDif.getInt("codcuar"));
                            cuartel.setNomcuar(rsDif.getString("nomcuar"));
                            cuartel.setTipcuar(pTipCuar);
                            cuartel.setFilas(rsDif.getInt("filas"));
                            cuartel.setColumnas(rsDif.getInt("columnas"));
                            
                            nicho.setFila1(rsDif.getInt("fila1"));
                            nicho.setFila2(rsDif.getString("fila2"));
                            nicho.setCol1(rsDif.getInt("columna1"));
                            nicho.setCol2(rsDif.getInt("columna2"));
                        } else if (rsDif.getString("tipent").equals("2")){
                            Parmae pTipoMau = new Parmae();
                            pTipoMau.setTipo("TIPMAU");
                            pTipoMau.setCodigo(rsDif.getString("tipomau"));
                            pTipoMau.setDescri(rsDif.getString("destipmau"));
                        
                            mausoleo.setCementerio(cementerio);
                            mausoleo.setCodmau(rsDif.getInt("codmau"));
                            mausoleo.setLotizado(rsDif.getString("lotizado"));
                            mausoleo.setNomlote(rsDif.getString("nomlote"));
                            mausoleo.setTipomau(pTipoMau);
                            mausoleo.setUbicacion(rsDif.getString("ubicacion"));
                            mausoleo.setFamilia(rsDif.getString("familia"));
                        }
                        
                        Parmae pTipoEnt = new Parmae();
                        pTipoEnt.setTipo("TIPENT");
                        pTipoEnt.setCodigo(rsDif.getString("tipent"));
                        pTipoEnt.setDescri(rsDif.getString("destipent"));
                        
                        Difunto difunto = new Difunto();
                        difunto.setCoddif(rsDif.getInt("coddif"));
                        difunto.setApepat(rsDif.getString("apepat"));
                        difunto.setApemat(rsDif.getString("apemat"));
                        difunto.setNomdif(rsDif.getString("nomdif"));
                        difunto.setNombres(rsDif.getString("nombres"));
                        difunto.setReservado(rsDif.getString("reservado"));
                        difunto.setEstado(estDif);
                        difunto.setFecfall(rsDif.getString("fecfall"));
                        difunto.setFecsep(rsDif.getString("fecsep"));
                        difunto.setEdad_a(rsDif.getInt("edad_a"));
                        difunto.setEdad_m(rsDif.getInt("edad_m"));
                        difunto.setEdad_d(rsDif.getInt("edad_d"));
                        difunto.setSexodif(rsDif.getString("sexo"));
                        difunto.setTipo_entierro(pTipoEnt);
                        difunto.setCementerio(cementerio);
                        difunto.setCuartel(cuartel);
                        difunto.setNicho(nicho);
                        difunto.setMausoleo(mausoleo);
                        difunto.setObserv(rsDif.getString("observ"));
                        return difunto;
                    }
                }, new Object[]{rs.getInt("coddif")});
                difunto = lDifunto.size() > 0 ? (Difunto) lDifunto.get(0) : null;

                ingreso.setDifunto(difunto);
                ingreso.setMtosbpgr(rs.getDouble("mtosbpgr"));
                ingreso.setMtosbpex(rs.getDouble("mtosbpex"));
                ingreso.setMtosbpin(rs.getDouble("mtosbpin"));
                ingreso.setMtotot(rs.getDouble("mtotot"));
                ingreso.setDscsbppor(rs.getDouble("dscsbppor"));
                ingreso.setDscsbp(rs.getDouble("dscsbp"));
                ingreso.setDscigv(rs.getDouble("dscigv"));
                ingreso.setMtocp(rs.getDouble("mtocp"));
                ingreso.setBasimpgr(rs.getDouble("basimpgr"));
                ingreso.setBasimpex(rs.getDouble("basimpex"));
                ingreso.setBasimpin(rs.getDouble("basimpin"));
                ingreso.setIgv(rs.getDouble("igv"));
                ingreso.setLiquida(rs.getBoolean("liquida"));
                ingreso.setIdliq(rs.getString("idliq"));
                ingreso.setEstado(estado);
                ingreso.setUser(rs.getString("user"));
                ingreso.setDuser(rs.getString("duser"));
                ingreso.setUsercr(rs.getString("usercr"));
                ingreso.setDusercr(rs.getString("dusercr"));

                return ingreso;
            }
        }, new Object[]{});
        return matches.size() > 0 ? (List<Ingreso>) matches : null;
    }

//    @Override
//    public String generaPDF(String iding) {
//        String mRet = "";
//        String cFile = " ";
//        File file;
//        Ingreso ingreso = buscaIngreso(iding);
//        PdfWriter pdfWriter;
//        PdfDocument pdfDoc;
//        com.itextpdf.layout.Document documento;
//
//        if (ingreso.getTipocomprobante().getCodigo().equals("FAC") || ingreso.getTipocomprobante().getCodigo().equals("B/V")) {
//            String cTipCP = (ingreso.getTipocomprobante().getCodigo().equals("FAC") ? "01" : "03");
//            cFile = drive + "\\CPE\\PDF\\" + ruc + "-" + cTipCP + "-" + ingreso.getSersbp() + "-" + ingreso.getCodsbp() + ".pdf";
//            file = new File(cFile);
//            try {
//                pdfWriter = new PdfWriter(file);
//                pdfDoc = new PdfDocument(pdfWriter);
//                documento = new com.itextpdf.layout.Document(pdfDoc, PageSize.LETTER);
//
//                logger.info("Agregando los elementos al documento");
//
//                String tipCP = "";
//                if (ingreso.getTipocomprobante().getCodigo().equals("FAC")) {
//                    tipCP = "01";
//                } else if (ingreso.getTipocomprobante().getCodigo().equals("B/V")) {
//                    tipCP = "03";
//                }
//
//                documento.add(new Paragraph("SOCIEDAD DE BENEFICENCIA PUBLICA DE PIURA"));
//                documento.add(new Paragraph("RUC:20147082861"));
//                documento.add(new Paragraph(ingreso.getTiping().getDescri()));
//
//                String xDir = ingreso.getTiping().getDireccion() + " " + ingreso.getTiping().getUbigeo().getNomdep() + "-" + ingreso.getTiping().getUbigeo().getNomprv() + "-" + ingreso.getTiping().getUbigeo().getNomdis();
//                documento.add(new Paragraph(xDir));
//
//                documento.add(new Paragraph(ingreso.getEstado().getDescri()));
//
//                String xComp = "";
//                if (ingreso.getTipocomprobante().getCodigo().equals("B/V")) {
//                    xComp = "BOLETA DE VENTA ELECTRONICA Nro:";
//                } else if (ingreso.getTipocomprobante().getCodigo().equals("FAC")) {
//                    xComp = "FACTURA ELECTRONICA Nro:";
//                } else if (ingreso.getTipocomprobante().getCodigo().equals("REC")) {
//                    xComp = "RECIBO DE INGRESO N°";
//                }
//                xComp += ingreso.getSersbp() + "-" + ingreso.getCodsbp();
//                documento.add(new Paragraph(xComp));
//
//                documento.add(new Paragraph("FECHA :" + ingreso.getFecha()));
//                documento.add(new Paragraph("Nombre :" + ingreso.getNomcli()));
//                /*  tabla */
//                Table tItems = new Table(new float[]{380, 10, 10});
////                tItems.setWidthPercent(100);
////            tItems.setWidth(200F);
//                tItems.addCell("Descripcion");
//                tItems.addCell("P.U.");
//                tItems.addCell("Total");
//
//                if (ingreso.getDetiteing().size() > 0) {
//                    int i = 0;
//                    for (Iteing iteing : ingreso.getDetiteing()) {
//                        i++;
//                        String xDescri = iteing.getServicio().getDesser();
//                        /**/
//                        if (!iteing.getDescri().isEmpty()) {
//                            xDescri += " - " + iteing.getDescri();
//                        }
//                        tItems.addCell(xDescri);
//                        tItems.addCell("");
//
//                        double xMto = 0.0;
//                        if (ingreso.getTiping().getTiping() != 4) {
//                            xMto = iteing.getMtosbp();   //p.salto();
//                        } else {
//                            xMto = iteing.getMtosbp();   //p.salto();
//                        }
//                        tItems.addCell(String.valueOf(xMto));
//                    }
//                }
//                documento.add(tItems);
//
//                Table tMontos = new Table(new float[]{125, 125});
////                tMontos.setWidthPercent(100);
//
//                if (!tipCP.isEmpty()) {
//                    tMontos.addCell("Op Grabada S/ ");
//                    tMontos.addCell(String.valueOf(ingreso.getBasimpgr()));
//                    tMontos.addCell("Op Exonerada S/ ");
//                    tMontos.addCell(String.valueOf(ingreso.getBasimpex()));
//                    tMontos.addCell("I.G.V. S/ ");
//                    tMontos.addCell(String.valueOf(ingreso.getIgv()));
//                    tMontos.addCell("DSCTO S/ ");
//                    tMontos.addCell(String.valueOf(ingreso.getDscsbp()));
//                    tMontos.addCell("Total S/ ");
//                    tMontos.addCell(String.valueOf(ingreso.getMtocp()));
//                } else {
//                    tMontos.addCell("Mto SBP S/ ");
//                    tMontos.addCell(String.valueOf(ingreso.getMtosbpgr()));
//                }
//
//                Table tRes = new Table(new float[]{30, 70});
////                tRes.setWidthPercent(100);
//
//                String xFile = drive + "\\CPE\\CODIGOBARRA\\" + ruc + "-" + tipCP + "-" + ingreso.getSersbp() + "-" + ingreso.getCodsbp() + ".jpg";
//                Image image = new Image(ImageDataFactory.create(xFile));
//                tRes.addCell(image);
//
//                tRes.addCell(tMontos);
//                documento.add(tRes);
//
//                String letras = new NumerosaLetras().convierteNumeroaLetras(ingreso.getMtocp());       //+ingreso.getMtosbpex()
//                documento.add(new Paragraph("Son :" + letras));
//
//                documento.add(new Paragraph("Cajero :" + ingreso.getUser()));
//                documento.add(new Paragraph("Fecha Emision :" + ingreso.getDuser()));
//                documento.close();
//
//                mRet = "ok";
//
//            } catch (FileNotFoundException ex) {
//                logger.error(ex.getMessage());
//            } catch (MalformedURLException ex) {
//                logger.error(ex.getMessage());
//            }
//
//        } else {
//            mRet = "No es un Comprobante Sunat";
//        }
//        return "";
//    }

    @Override
    public int imprimeComprobante(Ingreso ingreso) throws IOException {
        int ret = 0;
        int pagina = 0;
        int linea = 0;
        String barra = "";
        String maqServ = "";
        String cDisp = "";

        int nItems = getIngresoCount(ingreso.getIding(),"","","","","","","","");
    	//           getIngresoCount(String idIng, String tiping, String tipcli, String tipcom, String conser, String sersbp, String codsbp, String fecha, String estcomsbp) {
       if (nItems > 0) {
            impresora p = new impresora();
//                p.setDispositivo("/tmp/reporte.txt");
            String OS = System.getProperty("os.name").toLowerCase();
            if (OS.indexOf("win") >= 0) {
                barra = "\\";
            } else {
                barra = "/";
            }
            maqServ = "";
            String maqImpre = "";
            maqServ = InetAddress.getLocalHost().getHostName().toUpperCase();
//                System.out.println("mi maquina serviciod.-=> " + maqServ);
            if (maqServ.equals("SCM-ADMIN")) {
                maqImpre = "scm-caja";            // servicio consultorios medicos
            } else if (maqServ.equals("PEBS-ADMIN")) {
                maqImpre = "pebs-caja";             // policlinico el Buen Samaritano
            } else if (maqServ.equals("INFO_NB")) {
                maqImpre = "pebs-caja";      //prueba
//                maqImpre = "informatica";      //prueba
            } else if (maqServ.equals("INFORMATICA")) {
                if (ingreso.getTiping().getTiping() == 4) {
                    maqImpre = "informatica";      // comedor momentaneamente
                } else {
                    maqImpre = "IngresosCaja";      //caja central
                }
//                String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
//                if (cUser.equals("floro")){     // ingreso.getUser().equals("floro"
//                    maqImpre = "IngresosCaja";      //prueba
//                } else {
//                    maqImpre = "informatica";      //prueba
//                }
            }
            cDisp = barra + barra + maqImpre + barra + "fx890";

//            p.setDispositivo("");
            p.setDispositivo(cDisp);
            //System.out.println(cDisp);
//            p.setDispositivo("c:\\Windows\\Temp\\reporte.txt");
            p.escribir((char) 27 + "@");  // reinicia
            p.setRoman();
            p.setNegro();
            p.setFormato(5);    // 20 cpi == comprimir
//          p.setTipoCaracterLatino();
//          p.escribir((char)27+"C");
//          p.escribir((char)15+"");
//          p.setNegro();
            encabezado(ingreso, p);
            pagina++;
            cuerpo(ingreso, p);
            p.cerrarDispositivo();
        }
        return ret;
    }

    private void encabezado(Ingreso ingreso, impresora p) {
//        p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");
//        p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");p.padl("0123456789", 10, "");
        p.padc("SOCIEDAD DE BENEFICENCIA DE PIURA", 75, " ");
        p.espacios(10);
        p.padc("SOCIEDAD DE BENEFICENCIA DE PIURA", 75, " ");
        p.salto();

        p.padc(ingreso.getTiping().getDescri(), 75, " ");
        p.espacios(10);
        p.padc(ingreso.getTiping().getDescri(), 75, " ");
        p.salto();

        String xDir = ingreso.getTiping().getDireccion() + ingreso.getTiping().getUbigeo().getNomdep() + "-" + ingreso.getTiping().getUbigeo().getNomprv() + "-" + ingreso.getTiping().getUbigeo().getNomdis();
        p.padc(xDir, 75, " ");
        p.espacios(10);
        p.padc(xDir, 75, " ");
        p.salto();

        p.padr(ingreso.getEstado().getDescri(), 75, " ");
        p.espacios(10);
        p.padr(ingreso.getEstado().getDescri(), 75, " ");
        p.salto();

        p.padl("RUC. 20147082861", 16, " ");
        p.espacios(10);
        if (ingreso.getTipocomprobante().getCodigo().equals("B/V")) {
            p.padl("BOLETA DE VENTA ELCTRONICA", 29, " ");         //p.salto();
        } else if (ingreso.getTipocomprobante().getCodigo().equals("FAC")) {
            p.padl("FACTURA ELECTRONICA", 29, " ");                 //p.salto();
        } else if (ingreso.getTipocomprobante().getCodigo().equals("REC")) {
            p.padl("RECIBO DE INGRESO", 29, " ");    //p.salto();
        }
        /**/
        p.espacios(20);
        p.espacios(10);
        p.padl("RUC. 20147082861", 16, " ");
        p.espacios(10);
        if (ingreso.getTipocomprobante().getCodigo().equals("B/V")) {
            p.padl("BOLETA DE VENTA ELECTRONICA", 29, " ");p.salto();
        } else if (ingreso.getTipocomprobante().getCodigo().equals("FAC")) {
            p.padl("FACTURA ELECTRONICA", 29, " ");p.salto();
        } else if (ingreso.getTipocomprobante().getCodigo().equals("REC")) {
            p.padl("RECIBO DE INGRESO", 29, " ");
            p.salto();
        }
        p.salto();
    }

    private void cuerpo(Ingreso ingreso, impresora p) {
//        linea++;
//        p.espacios(5);p.padl(ingreso.getFecha(),10, " ");p.espacios(10);
        p.padl("FECHA :", 10, " ");
        p.padl(ingreso.getFecha(), 10, " ");
        p.espacios(10);
        p.padl(ingreso.getSersbp(), 4, "0");
        p.padl("N° ", 5, " ");
        p.padl(ingreso.getCodsbp(), 10, " ");
        p.espacios(26);
        p.espacios(10);
        p.padl("FECHA :", 10, " ");
        p.padl(ingreso.getFecha(), 10, " ");
        p.espacios(10);
        p.padl(ingreso.getSersbp(), 4, "0");
        p.padl("N° ", 5, " ");
        p.padl(ingreso.getCodsbp(), 10, " ");
        p.espacios(26);
        p.espacios(10);
        /**/
//        p.espacios(10);p.padr(ingreso.getNomcli(),30, " ");p.salto();
        p.padr("Nombre: ", 8, " ");
        p.padr(ingreso.getNomcli(), 30, " ");
        p.espacios(36);
        p.padr("Nombre: ", 8, " ");
        p.padr(ingreso.getNomcli(), 30, " ");
        p.espacios(36);
        p.salto();
        /**/
        p.padc("DESCRIPCION", 30, " ");
        p.padl("P.U.", 8, " ");
        p.padl("TOTAL", 10, " ");
        p.espacios(27);
        p.espacios(10);
        p.padc("DESCRIPCION", 30, " ");
        p.padl("P.U.", 8, " ");
        p.padl("TOTAL", 10, " ");
        p.salto();
        /**/
        String tipCP = "";
        if (ingreso.getTipocomprobante().getCodigo().equals("FAC")) {
            tipCP = "01";
        } else if (ingreso.getTipocomprobante().getCodigo().equals("B/V")) {
            tipCP = "03";
        }

//        p.salto();
        if (ingreso.getDetiteing().size() > 0) {
            int i = 0;
            for (Iteing iteing : ingreso.getDetiteing()) {
                i++;
                p.padr(iteing.getServicio().getDesser(), 30, " ");
                p.padl(String.valueOf(iteing.getMtosbp()), 10, " ");   //p.salto();
                p.espacios(27);
                p.espacios(10);
                /**/
                p.padr(iteing.getServicio().getDesser(), 30, " ");
                p.padl(String.valueOf(iteing.getMtosbp()), 10, " ");
                p.salto();
                
                String cDesAdi1 = "", cDesAdi2 = "";
                if (!iteing.getDescri().isEmpty()) {
                    int lDesAdi = iteing.getDescri().length();
                    if (lDesAdi > 75) {
                        cDesAdi1 = iteing.getDescri().substring(0, 75);
                        cDesAdi2 = iteing.getDescri().substring(75);
                    } else {
                        cDesAdi1 = iteing.getDescri();
                    }
                    p.padr(cDesAdi1, 75, " ");
                    p.espacios(10);
                    /**/
                    p.padr(cDesAdi1, 75, " ");
                    p.salto();
                    /**/
                    p.padr(cDesAdi2, 75, " ");
                    p.espacios(10);
                    /**/
                    p.padr(cDesAdi2, 75, " ");
                    p.salto();
                }
            }
            p.salto();

            if (!tipCP.isEmpty()) {
                p.salto();
                p.padl("Op Grabada", 45, " ");
                p.padl("S/.", 5, " ");
                p.padl(String.valueOf(ingreso.getMtosbpgr()), 10, " ");
                p.espacios(15);
                p.espacios(10);     //p.salto();
                p.padl("Op Grabada", 45, " ");
                p.padl("S/.", 5, " ");
                p.padl(String.valueOf(ingreso.getMtosbpgr()), 10, " ");
                p.salto();
                /**/
                p.padl("Op Exonerada", 45, " ");
                p.padl("S/.", 5, " ");
                p.padl(String.valueOf(ingreso.getMtosbpex()), 10, " ");
                p.espacios(15);
                p.espacios(10);     //p.salto();
                p.padl("Op Exonerada", 45, " ");
                p.padl("S/.", 5, " ");
                p.padl(String.valueOf(ingreso.getMtosbpex()), 10, " ");
                p.salto();
                /**/
                p.padl("I.G.V.", 45, " ");
                p.padl("S/.", 5, " ");
                p.padl(String.valueOf(ingreso.getIgv()), 10, " ");
                p.espacios(15);
                p.espacios(10);     //p.salto();
                p.padl("I.G.V.", 45, " ");
                p.padl("S/.", 5, " ");
                p.padl(String.valueOf(ingreso.getIgv()), 10, " ");
                p.salto();
            } else {
                p.salto();
                p.padl("Mto SBP", 45, " ");
                p.padl("S/.", 5, " ");
                p.padl(String.valueOf(ingreso.getMtosbpgr()), 10, " ");
                p.espacios(15);
                p.espacios(10);     //p.salto();
                p.padl("Mto SBP", 45, " ");
                p.padl("S/.", 5, " ");
                p.padl(String.valueOf(ingreso.getMtosbpgr()), 10, " ");
                p.salto();
                /**/
            }
        }

        p.padl("Cajero :", 8, " ");
        p.padl(ingreso.getUser(), 15, " ");
        p.espacios(52);
        p.espacios(10);
        p.padl("Cajero :", 8, " ");
        p.padl(ingreso.getUser(), 15, " ");
        p.salto();
        p.padl("Fecha Emision :", 15, " ");
        p.padl(ingreso.getDuser(), 21, " ");
        p.espacios(39);
        p.espacios(10);
        p.padl("Fecha Emision :", 15, " ");
        p.padl(ingreso.getDuser(), 21, " ");
        p.salto();

        p.avanza_pagina();

//        if (!(linea<=60)){
//            p.avanza_pagina();
//            encabezado();
//            cabezaGrupo(ingreso);
//        }
    }
}