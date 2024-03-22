package com.rosist.difunto.dao.impl;

import com.rosist.difunto.dao.ClienteDao;
import com.rosist.difunto.dao.DifuntoDao;
import com.rosist.difunto.dao.MausoleoDao;
import com.rosist.difunto.dao.NichotDao;
import com.rosist.difunto.dao.OcuFutDao;
import com.rosist.difunto.dao.ParmaeDao;
import com.rosist.difunto.exception.ModelNotFoundException;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cliente;
import com.rosist.difunto.modelSbp.Cuartel;
import com.rosist.difunto.modelSbp.Difunto;
import com.rosist.difunto.modelSbp.Mausoleo;
import com.rosist.difunto.modelSbp.Nicho_t;
import com.rosist.difunto.modelSbp.Ocufut;
import com.rosist.difunto.modelSbp.Parmae;

import jakarta.transaction.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

@Repository
public class DifuntoImpl implements DifuntoDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private ParmaeDao daoParmae;
    
    @Autowired
    private NichotDao daoNichot;
    
    @Autowired
    private OcuFutDao daoOcuFut;
    
//    @Autowired
//    private MausoleoDao daoMausoleo;

    @Autowired
    private ClienteDao daoCliente;
    
    private static final Logger logger = LoggerFactory.getLogger(DifuntoImpl.class);
    
    @Override
    public int getDifuntoCount() {
        String sql = "select count(*) as count from difunto";
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getDifuntoCount(String condicion) {
        String sql = "select count(*) as count from difunto d where 1=1" + condicion;
        System.out.println("sqlcount.-=> " + sql);
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getNewIdDifunto(int codcem){
    	String sql = "select ifnull(max(coddif),0)+1 from difunto where codcem=?";
        int nCorrel = this.jdbcTemplate.queryForObject(sql,Integer.class, new Object[]{codcem});
        return nCorrel;
    }

	@Transactional
    @Override
    public Difunto insertaDifunto(Difunto difunto) {
        int resp=0;
        System.out.println("guadando...");
        String sql = "insert into difunto(coddif, fecfall, fecsep, apepat, apemat, nombres, sexo, edad_a, edad_m, edad_d, "
                + "                       tipent, codcem, codcuar, codnic, fila1, columna1, fila2, columna2, codmau, tipdoccli, "
                + "                       doccli, nomcli, codtras, codocu, estado, reservado, estvta, diamed, "
                + "                       recing, fecri, mtori, observ, user, duser) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//                                        coddif, fecfall, fecsep, apepat, apemat, nombres, nomdif, sexo, edad_a, edad_m, edad_d, tipent, codcem, codcuar, codnic, fila, columna, codmau, codcli, codtras, codocu, estado, reservado, estvta, observ 
        int xCodDif = this.getNewIdDifunto(difunto.getCementerio().getCodcem());
        difunto.setCoddif(xCodDif);
        String cUser = "usuario";
//        String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Date   dUser = new Date();
        difunto.setNomdif(difunto.getApepat() + " " + difunto.getApemat() + ", " + difunto.getNombres());
        int xCodCem = difunto.getCementerio().getCodcem();
        int xCodCuar = 0;
        Nicho_t nicho = null;
        String xCodNic = "", xFila2="";
        int xFila1 = 0, xCol1=0, xCol2=0, xCodMau=0, xNumDif=0;
        Mausoleo mausoleo = null;
        
        if (difunto.getTipo_entierro().getCodigo().equals("1")){
            
            System.out.println("difunto nuevo " + difunto.toString());
            
            nicho = daoNichot.buscaNichot(difunto.getCementerio().getCodcem(), difunto.getCuartel().getCodcuar(), difunto.getNicho().getFila1(), difunto.getNicho().getCol1());
            System.out.println("valor de nicho en Imple.-=> " + nicho );
            System.out.println("valor de nicho en Imple.-=> " + nicho.toString() );
            if (nicho!=null){
                xCodCuar = difunto.getCuartel().getCodcuar();
                xCodNic = String.format("%1$01d",difunto.getNicho().getFila1()) + String.format("%1$03d",difunto.getNicho().getCol1());
                xFila1  = Integer.valueOf(xCodNic.substring(0, 1));
                xFila2  = nicho.getFila2();
                xCol1   = Integer.valueOf(xCodNic.substring(1, 4));
                xCol2   = nicho.getCol2();
            }
        } if (difunto.getTipo_entierro().getCodigo().equals("2")){
            xCodMau = difunto.getMausoleo().getCodmau();
            String _sql = "select numdif from mausoleo where codcem=? and codmau=?";
            xNumDif = this.jdbcTemplate.queryForObject(_sql,Integer.class, new Object[]{xCodCem, xCodMau});
            xNumDif++;
        } if (difunto.getTipo_entierro().getCodigo().equals("3")){
            
        }
//        int xCliente  = (difunto.getCliente()!=null?difunto.getCliente().getCodcli():0);
        int xTraslado = (difunto.getTraslado()!=null?difunto.getTraslado().getCodtras():0);
        int xCodOcu = (difunto.getOcufut()!=null?difunto.getOcufut().getCodocu():0);
        String xEstado = "00";
        String xEstVta = "00";
        
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                              difunto.getCoddif(),difunto.getFecfall(),difunto.getFecsep(),
                              difunto.getApepat(),
                              difunto.getApemat(),difunto.getNombres(),difunto.getSexodif(),
                              difunto.getEdad_a(),difunto.getEdad_m(),difunto.getEdad_d(),
                              difunto.getTipo_entierro().getCodigo(),difunto.getCementerio().getCodcem(),
                              xCodCuar,xCodNic,xFila1,xCol1,xFila2,xCol2,
                              xCodMau, difunto.getCliente().getTipdoccli().getCodigo(), difunto.getCliente().getDoccli(),
                              difunto.getCliente().getNomcli(),xTraslado,xCodOcu,
                              xEstado, difunto.getReservado(), xEstVta, 
                              difunto.getDiamed(), difunto.getRecing(),
                              difunto.getFecri(), difunto.getMtori(), difunto.getObserv(), cUser,dUser
                              
                          }
        );
        if (resp>0){
            
            Cliente xCliente = null;
            if (!difunto.getCliente().getTipdoccli().getCodigo().equals("0")) {
                xCliente = daoCliente.buscaCliente(difunto.getCliente().getTipdoccli().getCodigo(), difunto.getCliente().getDoccli());
            }

            if (xCliente == null && !difunto.getCliente().getTipdoccli().getCodigo().equals("0")) {
                sql = "insert into clientesunat(tipdoc, doccli, nomcli, dircli) values(?,?,?,?)";
                resp = jdbcTemplate.update(sql,
                        new Object[]{
                            difunto.getCliente().getTipdoccli().getCodigo(),
                            difunto.getCliente().getDoccli(),
                            difunto.getCliente().getNomcli(),
                            difunto.getCliente().getDircli()
                        });
            }
            
            if (difunto.getTipo_entierro().getCodigo().equals("1")){
                String sqlNic  = "update nicho_t set estado=? where codcem=? and codcuar=? and fila1=? and col1=?";
                resp = jdbcTemplate.update(sqlNic,
                                  new Object[]{
                                      "3", xCodCem, xCodCuar, xFila1, xCol1
                                  });
                String sqlNicE = "update nicho_e set col" + String.format("%1$03d",xCol1) + "=? where codcem=? and codcuar=? and fila1=?";
                resp = jdbcTemplate.update(sqlNicE,
                                  new Object[]{
                                      "3",xCodCem, xCodCuar, xFila1
                                  });
                if (xCodOcu>0){
                    String sqlNicOF = "update ocufut set estado=?, coddif=? where codcem=? and codocu=?";
                    resp = jdbcTemplate.update(sqlNicOF,
                                      new Object[]{
                                          "20",difunto.getCoddif(), xCodCem, xCodOcu
                                      });
                }
                
            } if (difunto.getTipo_entierro().getCodigo().equals("2")){
                String sqlMau = "update mausoleo set estado=?, numdif=? where codcem=? and codmau=?";
                    resp = jdbcTemplate.update(sqlMau,
                                      new Object[]{
                                          "20",xNumDif, xCodCem, xCodMau
                                      });
            } else {} // si tipo de enterro es 3 
        }
		return (resp>0?buscaDifunto(difunto.getCementerio().getCodcem(), difunto.getCoddif()):null);
    }
    
	@Transactional
    @Override
    public Difunto modificaDifunto(Difunto difunto) throws Exception {
        String cUser = "usuario";
//        String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Date   dUser = new Date();
        int resp=0;
//        logger.info("difunto.-=> " + difunto.toString());
        if (difunto.getEstado().getCodigo().equals("30")) {
        	throw new Exception("El difunto tiene traslado no se puede modificar");
        }
        difunto.setNomdif(difunto.getApepat() + " " + difunto.getApemat() + ", " + difunto.getNombres());
        String xFecFall  = (!difunto.getFecfall().equals("")?difunto.getFecfall():"0000-00-00");
        String xFecSep   = (!difunto.getFecsep().equals("")?difunto.getFecsep():"0000-00-00");
//        String xHoraSep  = (!difunto.getHorasep().equals("")?difunto.getHorasep():"00:00");
        String sql = "update difunto set fecfall=?, fecsep=?, apepat=?, apemat=?, nombres=?, sexo=?, edad_a=?, edad_m=?, edad_d=?, tipdoccli=?, doccli=?, nomcli=?, diamed=?, recing=?, fecri=?, mtori=?, observ=?, usercr=?, dusercr=? where codcem=? and coddif=?";
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            xFecFall, xFecSep, difunto.getApepat(), difunto.getApemat(), difunto.getNombres(), difunto.getSexodif(), difunto.getEdad_a(), difunto.getEdad_m(), difunto.getEdad_d(), difunto.getCliente().getTipdoccli().getCodigo(), difunto.getCliente().getDoccli(), difunto.getCliente().getNomcli(), difunto.getDiamed(), difunto.getRecing(), difunto.getFecri(), difunto.getMtori(), difunto.getObserv(), cUser,dUser, difunto.getCementerio().getCodcem(), difunto.getCoddif(),
                          });
        if (resp > 0) {
            Cliente xCliente = null;
            if (!difunto.getCliente().getTipdoccli().getCodigo().equals("0")) {
                xCliente = daoCliente.buscaCliente(difunto.getCliente().getTipdoccli().getCodigo(), difunto.getCliente().getDoccli());
            }

            if (xCliente == null && !difunto.getCliente().getTipdoccli().getCodigo().equals("0")) {
                sql = "insert into clientesunat(tipdoc, doccli, nomcli, dircli) values(?,?,?,?)";
                resp = jdbcTemplate.update(sql,
                        new Object[]{
                            difunto.getCliente().getTipdoccli().getCodigo(),
                            difunto.getCliente().getDoccli(),
                            difunto.getCliente().getNomcli(),
                            difunto.getCliente().getDircli()
                        });
            }
        }
		return (resp>0?buscaDifunto(difunto.getCementerio().getCodcem(), difunto.getCoddif()):null);
    }
    
	@Transactional
    @Override
    public int eliminaDifunto(int codcem, int coddif) throws Exception {
        int resp=0;
        Difunto difunto = buscaDifunto(codcem, coddif);
        
        if (difunto.getEstado().getCodigo().equals("30")) {
        	throw new Exception("El difunto tiene traslado no se puede eliminar");
        }
        
        int xCodOcu = 0, xCodCuar = 0, xFila1=0, xCol1=0, xCodMau=0;
        String sql = "delete from difunto where codcem=? and coddif=?";
        resp = jdbcTemplate.update(sql, new Object[]{codcem, coddif});
        if (resp>0){
            if (difunto.getReservado().equals("S")){
                xCodOcu = difunto.getOcufut().getCodocu();
                sql = "update ocufut set estado=?, coddif=? where codcem=? and codocu=? and estado=?";
                resp = jdbcTemplate.update(sql, new Object[]{"10", 0, codcem, xCodOcu,"20"});
            }
            if (difunto.getTipo_entierro().getCodigo().equals("1")){
                xCodCuar = difunto.getCuartel().getCodcuar();
                xFila1 = difunto.getNicho().getFila1();
                xCol1  = difunto.getNicho().getCol1();
                Nicho_t nichot = daoNichot.buscaNichot(codcem, xCodCuar, xFila1, xCol1);
                String xEst = (xCodOcu>0?"2":"1");
                sql = "update nicho_t set estado=? where codcem=? and codcuar=? and fila1=? and col1=?";
                resp = jdbcTemplate.update(sql, new Object[]{xEst, codcem, xCodCuar, xFila1,xCol1});
                sql = "update nicho_e set col" + String.format("%1$03d",xCol1) + "=? where codcem=? and codcuar=? and fila1=?";
                resp = jdbcTemplate.update(sql, new Object[]{xEst, codcem, xCodCuar, xFila1});
            }if (difunto.getTipo_entierro().getCodigo().equals("2")){
                xCodMau = difunto.getMausoleo().getCodmau();
                sql = "select numdif from mausoleo where codcem=? and codmau=?";
                int xNumDif = this.jdbcTemplate.queryForObject(sql,Integer.class, new Object[]{codcem, xCodMau});
                xNumDif--;
                String xEstado = "20";
                if (xNumDif == 0){
                    xEstado = "00";
                }
                sql = "update mausoleo set numdif=?, estado=? where codcem=? and codmau=?";
                resp = jdbcTemplate.update(sql, new Object[]{xNumDif, xEstado, codcem, xCodMau});
            } else {
                
            }
        }
        return resp;
    }

    @Override
    public Difunto buscaDifunto(int codcem, int coddif) {
        String sql = "select d.codcem, cem.nomcem, cem.local, d.codcuar, cu.nomcuar, cu.tipcuar, p.descri destipcuar, cu.filas, cu.columnas,"
                + "          d.codmau, m.tipomau, p3.descri destipmau, m.lotizado, m.nomlote, m.familia, m.ubicacion, m.totdif, m.numdif, "
                + "          d.tipent, p2.descri destipent, d.coddif, date_format(d.fecfall,'%d/%m/%Y') fecfall, "
                + "          date_format(d.fecsep,'%d/%m/%Y') fecsep, d.apepat, d.apemat, d.nombres, d.diamed, d.recing, "
                + "          date_format(d.fecri,'%d/%m/%Y') fecri, d.mtori, "
                + "          CONCAT(TRIM(d.apepat),' ',TRIM(d.apemat),', ',TRIM(d.nombres)) nomdif, d.edad_a, d.edad_m, d.edad_d, d.sexo, "
                + "          d.reservado, d.codocu, d.codnic, d.fila1, d.fila2, d.columna1, d.columna2, d.estado, d.estvta, d.observ,"
                + "          d.tipdoccli, d.doccli, d.nomcli, cl.dircli, d.user, d.duser "
                + "   from difunto d left join cementerio cem on d.codcem =cem.codcem"
                + "                  left join cuartel    cu  on d.codcem=cu.codcem and d.codcuar=cu.codcuar"
                + "                  left join mausoleo   m   on d.codmau=m.codmau"
                + "                  left join parmae p  on p.tipo='TIPCUA'  and cu.tipcuar=p.codigo"
                + "                  left join parmae p2 on p2.tipo='TIPENT' and d.tipent=p2.codigo"
                + "                  left join parmae p3 on p3.tipo='TIPMAU' and m.tipomau=p3.codigo"
            + "                      left join clientesunat cl  on  cl.tipdoc=d.tipdoccli and cl.doccli=d.doccli"
                + " where coddif=?";
        
        List<Difunto> matches = jdbcTemplate.query (sql,
                new RowMapper<Difunto>() {
                @Override
                    public Difunto mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Ocufut ocufut = null;
                        Cementerio cementerio = new Cementerio();
                        cementerio.setCodcem(rs.getInt("codcem"));
                        cementerio.setNomcem(rs.getString("nomcem"));
                        cementerio.setLocal(rs.getBoolean("local"));
                        
                        if (rs.getString("reservado").equals("S")){
                            ocufut = daoOcuFut.buscaOcuFut(rs.getInt("codcem"), rs.getInt("codocu"));
                        }
                        
                        Parmae pTipCuar = new Parmae("TIPCUA",rs.getString("tipcuar"),rs.getString("destipcuar"),"");
//                        pTipCuar.setTipo("TIPCUA");
//                        pTipCuar.setCodigo(rs.getString("tipcuar"));
//                        pTipCuar.setDescri(rs.getString("destipcuar"));
                        Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoccli"), "", "");
                        
                        Parmae estado = daoParmae.buscaParmae("ESTDIF", rs.getString("estado"), "");
                        Parmae estvta = daoParmae.buscaParmae("ESTVTA", rs.getString("estvta"), "");
                        
                        Cuartel cuartel = new Cuartel();
                        Nicho_t nicho = new Nicho_t();
                        Mausoleo mausoleo = new Mausoleo();
                        
                        Cliente cliente = null;
                        String nomcli = "";

                        if (!tipdoccli.getCodigo().equals("0")) {
                            System.out.println("doc cli " + rs.getInt("tipdoccli") + "-" + rs.getString("doccli"));
                            cliente = daoCliente.buscaCliente(rs.getString("tipdoccli"), rs.getString("doccli"));
                        } else {
                            cliente = new Cliente();
                            cliente.setTipdoccli(tipdoccli);
                            cliente.setNomcli(rs.getString("nomcli"));
                        }
                        nomcli = cliente.getNomcli();

//                        Cliente cliente = new Cliente();
                        
                        if (rs.getString("tipent").equals("1")){
                            cuartel.setCementerio(cementerio);
                            cuartel.setCodcuar(rs.getInt("codcuar"));
                            cuartel.setNomcuar(rs.getString("nomcuar"));
                            cuartel.setTipcuar(pTipCuar);
                            cuartel.setFilas(rs.getInt("filas"));
                            cuartel.setColumnas(rs.getInt("columnas"));
                            
                            nicho.setFila1(rs.getInt("fila1"));
                            nicho.setFila2(rs.getString("fila2"));
                            nicho.setCol1(rs.getInt("columna1"));
                            nicho.setCol2(rs.getInt("columna2"));
                        } else if (rs.getString("tipent").equals("2")){
                            Parmae pTipoMau = new Parmae();
                            pTipoMau.setTipo("TIPMAU");
                            pTipoMau.setCodigo(rs.getString("tipomau"));
                            pTipoMau.setDescri(rs.getString("destipmau"));
                        
                            mausoleo.setCementerio(cementerio);
                            mausoleo.setCodmau(rs.getInt("codmau"));
                            mausoleo.setLotizado(rs.getString("lotizado"));
                            mausoleo.setNomlote(rs.getString("nomlote"));
                            mausoleo.setTipomau(pTipoMau);
                            mausoleo.setUbicacion(rs.getString("ubicacion"));
                            mausoleo.setFamilia(rs.getString("familia"));
                            mausoleo.setTotdif(rs.getInt("totdif"));
                            mausoleo.setNumdif(rs.getInt("numdif"));
                        }
                        
                        Parmae pTipoEnt = new Parmae();
                        pTipoEnt.setTipo("TIPENT");
                        pTipoEnt.setCodigo(rs.getString("tipent"));
                        pTipoEnt.setDescri(rs.getString("destipent"));
    
                        Difunto difunto = new Difunto();
                        difunto.setCoddif(rs.getInt("coddif"));
                        difunto.setApepat(rs.getString("apepat"));
                        difunto.setApemat(rs.getString("apemat"));
                        difunto.setNomdif(rs.getString("nomdif"));
                        difunto.setNombres(rs.getString("nombres"));
                        difunto.setReservado(rs.getString("reservado"));
                        difunto.setOcufut(ocufut);
                        difunto.setEstado(estado);
                        difunto.setEstvta(estvta);
                        difunto.setFecfall(rs.getString("fecfall"));
                        difunto.setFecsep(rs.getString("fecsep"));
                        difunto.setEdad_a(rs.getInt("edad_a"));
                        difunto.setEdad_m(rs.getInt("edad_m"));
                        difunto.setEdad_d(rs.getInt("edad_d"));
                        difunto.setSexodif(rs.getString("sexo"));
                        difunto.setCliente(cliente);
                        difunto.setNomcli(nomcli);
                        difunto.setTipo_entierro(pTipoEnt);
                        difunto.setCementerio(cementerio);
                        difunto.setCuartel(cuartel);
                        difunto.setNicho(nicho);
                        difunto.setMausoleo(mausoleo);
                        difunto.setDiamed(rs.getString("diamed"));
                        difunto.setRecing(rs.getString("recing"));
                        difunto.setFecri(rs.getString("fecri"));
                        difunto.setMtori(rs.getDouble("mtori"));
                        difunto.setObserv(rs.getString("observ"));
                        difunto.setUser(rs.getString("user"));
                        difunto.setDuser(rs.getString("duser"));
                        return difunto;
                    }
        }, new Object[] {coddif});
        return matches.size() > 0? (Difunto)matches.get(0): null;
    }
    
    @Override
    public List<Difunto> listaDifunto(String condicion, String limit, String orden) {
    	String sql = "SELECT d.codcem, cem.nomcem, cem.local, d.codcuar, cu.nomcuar, cu.tipcuar, p.descri destipcuar, cu.filas, cu.columnas, " + 
    			"       d.codmau, m.tipomau, p3.descri destipmau, m.lotizado, m.nomlote, m.familia, m.ubicacion, m.totdif, m.numdif, " + 
    			"       d.tipent, p2.descri destipent, d.coddif, DATE_FORMAT(d.fecfall,'%d/%m/%Y') fecfall, DATE_FORMAT(d.fecsep,'%d/%m/%Y') fecsep, " + 
    			"       d.apepat, d.apemat, d.nombres, CONCAT(TRIM(d.apepat),' ',TRIM(d.apemat),', ',TRIM(d.nombres)) nomdif, d.edad_a, d.edad_m, d.edad_d, " + 
    			"       d.sexo, d.reservado, d.codocu, d.codnic, d.fila1, d.fila2, d.columna1, d.columna2, d.estado, d.estvta, "
    			+ "     d.diamed, d.recing, date_format(d.fecri,'%d/%m/%Y') fecri, d.mtori, d.observ, " + 
    			"       d.tipdoccli, d.doccli, d.nomcli, cl.dircli " + 
    			"  FROM difunto d LEFT JOIN cementerio cem ON d.codcem =cem.codcem " + 
    			"                 LEFT JOIN cuartel    cu  ON d.codcem=cu.codcem AND d.codcuar=cu.codcuar " + 
    			"                 LEFT JOIN (SELECT ma.codcem, ma.codmau, ma.lotizado, ma.nomlote, " + 
    			"				                    ma.tipomau, p.descri destipmau, ma.ubicacion, ma.familia, " + 
    			"			                        ma.estado, ma.totdif, ma.numdif, ma.estvta " + 
    			"			                   FROM mausoleo ma LEFT JOIN parmae p ON ma.tipomau=p.codigo AND p.tipo='TIPMAU' " + 
    			"			                                    LEFT JOIN parmae e ON ma.estado=e.codigo AND e.tipo='ESTMAU') m   ON d.codcem=m.codcem AND d.codmau=m.codmau " + 
    			"                 LEFT JOIN parmae     p   ON p.tipo='TIPCUA'  AND cu.tipcuar=p.codigo " + 
    			"                 LEFT JOIN parmae     p2  ON p2.tipo='TIPENT' AND d.tipent=p2.codigo " + 
    			"                 LEFT JOIN parmae     p3  ON p3.tipo='TIPMAU' AND m.tipomau=p3.codigo " + 
    			"                 LEFT JOIN clientesunat cl ON cl.tipdoc=d.tipdoccli AND cl.doccli=d.doccli " + 
    			"WHERE 1=1 "
                + condicion + " ";

        if (!orden.isEmpty()){
            sql += " order by " + orden;
        }
        sql += (!limit.isEmpty() ? limit : "  limit  0, 100 ");
        
//        logger.info("sql-=> " + sql);
        List<Difunto> matches = jdbcTemplate.query (sql,
                new RowMapper<Difunto>() {
                @Override
                    public Difunto mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Cementerio cementerio = new Cementerio();
                        cementerio.setCodcem(rs.getInt("codcem"));
                        cementerio.setNomcem(rs.getString("nomcem"));
                        cementerio.setLocal(rs.getBoolean("local"));
                        
                        Parmae pTipCuar = new Parmae("TIPCUA",rs.getString("tipcuar"),rs.getString("destipcuar"), "");
//                        pTipCuar.setTipo("TIPCUA");
//                        pTipCuar.setCodigo(rs.getString("tipcuar"));
//                        pTipCuar.setDescri(rs.getString("destipcuar"));
                        
                        Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoccli"), "", "");
                        Parmae estado = daoParmae.buscaParmae("ESTDIF", rs.getString("estado"), "");
//                        Parmae estvta = servParmae.buscaParmae("ESTVTA", rs.getString("estvta"), "");
                        
                        Cliente cliente = null;
//                        String nomcli = "";

                        if (!tipdoccli.getCodigo().equals("0")) {
                            //System.out.println("doc cli " + rs.getInt("tipdoccli") + "-" + rs.getString("doccli"));
                            cliente = daoCliente.buscaCliente(rs.getString("tipdoccli"), rs.getString("doccli"));
                        } else {
                        	cliente = daoCliente.buscaCliente(rs.getString("tipdoccli"), rs.getString("doccli"));
//                            cliente = new Clientesunat();
//                            cliente.setTipdoccli(tipdoccli);
//                            cliente.setNomcli(rs.getString("nomcli"));
                        }
//                        nomcli = cliente.getNomcli();
                        
                        Cuartel cuartel = new Cuartel();
                        Nicho_t nicho = new Nicho_t();
                        Mausoleo mausoleo = new Mausoleo();
                        if (rs.getString("tipent").equals("1")){
                            cuartel.setCementerio(cementerio);
                            cuartel.setCodcuar(rs.getInt("codcuar"));
                            cuartel.setNomcuar(rs.getString("nomcuar"));
                            cuartel.setTipcuar(pTipCuar);
                            cuartel.setFilas(rs.getInt("filas"));
                            cuartel.setColumnas(rs.getInt("columnas"));
                            
                            nicho.setFila1(rs.getInt("fila1"));
                            nicho.setFila2(rs.getString("fila2"));
                            nicho.setCol1(rs.getInt("columna1"));
                            nicho.setCol2(rs.getInt("columna2"));
                            
                        } else if (rs.getString("tipent").equals("2")){
                            Parmae pTipoMau = new Parmae();
                            pTipoMau.setTipo("TIPMAU");
                            pTipoMau.setCodigo(rs.getString("tipomau"));
                            pTipoMau.setDescri(rs.getString("destipmau"));
                        
                            mausoleo.setCementerio(cementerio);
                            mausoleo.setCodmau(rs.getInt("codmau"));
                            mausoleo.setLotizado(rs.getString("lotizado"));
                            mausoleo.setNomlote(rs.getString("nomlote"));
                            mausoleo.setTipomau(pTipoMau);
                            mausoleo.setUbicacion(rs.getString("ubicacion"));
                            mausoleo.setFamilia(rs.getString("familia"));
                            mausoleo.setTotdif(rs.getInt("totdif"));
                            mausoleo.setNumdif(rs.getInt("numdif"));
                        }
                        
                        Parmae pTipoEnt = new Parmae();
                        pTipoEnt.setTipo("TIPENT");
                        pTipoEnt.setCodigo(rs.getString("tipent"));
                        pTipoEnt.setDescri(rs.getString("destipent"));
    
                        Difunto difunto = new Difunto();
                        difunto.setCoddif(rs.getInt("coddif"));
                        difunto.setApepat(rs.getString("apepat"));
                        difunto.setApemat(rs.getString("apemat"));
                        difunto.setNomdif(rs.getString("nomdif"));
                        difunto.setNombres(rs.getString("nombres"));
                        difunto.setReservado(rs.getString("reservado"));
                        difunto.setFecfall(rs.getString("fecfall"));
                        difunto.setFecsep(rs.getString("fecsep"));
//                        difunto.setHorasep(rs.getString("horasep"));
                        difunto.setEdad_a(rs.getInt("edad_a"));
                        difunto.setEdad_m(rs.getInt("edad_m"));
                        difunto.setEdad_d(rs.getInt("edad_d"));
                        difunto.setSexodif(rs.getString("sexo"));
                        difunto.setCliente(cliente);
                        difunto.setNomcli(rs.getString("nomcli"));
                        difunto.setTipo_entierro(pTipoEnt);
                        difunto.setEstado(estado);
//                        difunto.setEstvta(estvta);
                        difunto.setCementerio(cementerio);
                        difunto.setCuartel(cuartel);
                        difunto.setNicho(nicho);
                        difunto.setMausoleo(mausoleo);
//                        difunto.setOrdinm(rs.getString("ordinm"));
//                        difunto.setActdef(rs.getString("actdef"));
                        difunto.setDiamed(rs.getString("diamed"));
                        difunto.setRecing(rs.getString("recing"));
                        difunto.setFecri(rs.getString("fecri"));
                        difunto.setMtori(rs.getDouble("mtori"));
                        difunto.setObserv(rs.getString("observ"));
                        
                        return difunto;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }
}