package com.rosist.difunto.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.dao.TrasladoDao;
import com.rosist.difunto.modelSbp.Cementerio;
import com.rosist.difunto.modelSbp.Cliente;
import com.rosist.difunto.modelSbp.Difunto;
import com.rosist.difunto.modelSbp.Mausoleo;
import com.rosist.difunto.modelSbp.Nicho_t;
import com.rosist.difunto.modelSbp.Parmae;
import com.rosist.difunto.modelSbp.Traslado;
import com.rosist.difunto.dao.CementerioDao;
import com.rosist.difunto.dao.ClienteDao;
import com.rosist.difunto.dao.DifuntoDao;
import com.rosist.difunto.dao.MausoleoDao;
import com.rosist.difunto.dao.NichotDao;

/**
 *
 * @author Marco
 */
@Repository
public class TrasladoImpl implements TrasladoDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private CementerioDao daoCementerio;
    
    @Autowired
    private DifuntoDao daoDifunto;
    
    @Autowired
    private NichotDao daoNichot;
    
    @Autowired
    private MausoleoDao daoMausoleo;
    
    @Autowired
    private ClienteDao daoCliente;
    
    @Override
    public int getTrasladoCount() {
        String sql = "select count(*) as count from traslado";
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }
    
    @Override
    public int getTrasladoCount(String condicion) {
        String sql = "select count(*) as count from traslado where 1=1" + condicion;
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getNewIdTraslado() {
    	String sql = "select ifnull(max(codtras),0)+1 from traslado";
        int nCorrel = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nCorrel;
    }

    @Override
    public Traslado insertaTraslado(Traslado traslado) {
        int resp=0;
        System.out.println("guadando...");
        
        int xCodCem = traslado.getDifuntoant().getCementerio().getCodcem();
        if (traslado.getDifuntoant().getTipo_entierro().getCodigo().equals("1")){
            // estado del nicho = 4
            int xCodCuar = traslado.getDifuntoant().getCuartel().getCodcuar();
            int xFila1 = traslado.getDifuntoant().getNicho().getFila1();
            int xCol1  = traslado.getDifuntoant().getNicho().getCol1();
            String sqlNic  = "update nicho_t set estado=? where codcem=? and codcuar=? and fila1=? and col1=?";
            resp = jdbcTemplate.update(sqlNic,
                              new Object[]{
                                  "4", xCodCem, xCodCuar, xFila1, xCol1
                              });
            String sqlNicE = "update nicho_e set col" + String.format("%1$03d",xCol1) + "=? where codcem=? and codcuar=? and fila1=?";
            resp = jdbcTemplate.update(sqlNicE,
                              new Object[]{
                                  "4",xCodCem, xCodCuar, xFila1
                              });
        }
        if (traslado.getDifuntoant().getTipo_entierro().getCodigo().equals("2")){
            // decrementa numero difuntos en mausoleo
            int xNumDif = traslado.getDifuntoant().getMausoleo().getNumdif()-1;
            int xCodMau = traslado.getDifuntoant().getMausoleo().getCodmau();
            
            String sqlMau = "update mausoleo set estado=?, numdif=? where codcem=? and codmau=?";
                resp = jdbcTemplate.update(sqlMau,
                                  new Object[]{
                                      "20",xNumDif, xCodCem, xCodMau
                                  });
        }
        //estado difunto = 30
        traslado.setCodtras(this.getNewIdTraslado());
        String sqlMau = "update difunto set estado=?, codtras=? where codcem=? and coddif=?";
        resp = jdbcTemplate.update(sqlMau,
                            new Object[]{
                                  "30", traslado.getCodtras() ,xCodCem, traslado.getDifuntoant().getCoddif()
                            });
        
        //si local es si
        //    enterramos nuevo difunto
        int xCodDifNew = 0;
        if (traslado.isLocal()){
            Difunto newDifunto = traslado.getDifuntonew();
            if (newDifunto.getFecfall().equals("0000-00-00")) newDifunto.setFecfall("0001-01-01");
            if (newDifunto.getFecsep().equals("0000-00-00")) newDifunto.setFecsep("0001-01-01");
            newDifunto.setSexodif(traslado.getDifuntoant().getSexodif());
            insertaDifunto(newDifunto);
            xCodDifNew = newDifunto.getCoddif();
//            System.out.println("codigo de difunto nuevo.-=> " + newDifunto.getCoddif());
        }
        
        String sql = "insert into traslado(codtras, tipdoccli, doccli, nomcli, fectras, docref, coddocrf, fecdocrf, tiptras, coddifan, coddifnw, local, observ, estado, user, duser) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//        int xCodTras = this.getNewIdTraslado();
//        traslado.setCodtras(xCodTras);
        String cUser = "usuario";
//        String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Date   dUser = new Date();
        String xEstado = "00";
        
//        int xCodDifNew = (traslado.getDifuntonew()==null?traslado.getDifuntonew().getCoddif():0);
                
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                              traslado.getCodtras(), traslado.getCliente().getTipdoccli().getCodigo(), traslado.getCliente().getDoccli(),
                              traslado.getCliente().getNomcli(),traslado.getFectras(),traslado.getDocref().getCodigo(),
                              traslado.getCoddocrf(),traslado.getFecdocrf(),traslado.getTiptras().getCodigo(),
                              traslado.getDifuntoant().getCoddif(),xCodDifNew,traslado.isLocal(),
                              traslado.getObserv(),
                              xEstado, 
                              cUser,dUser
                          }
        );
        if (resp>0){
            Cliente xCliente = null;
            if (!traslado.getCliente().getTipdoccli().getCodigo().equals("0")) {
                xCliente = daoCliente.buscaCliente(traslado.getCliente().getTipdoccli().getCodigo(), traslado.getCliente().getDoccli());
            }

            if (xCliente == null && !traslado.getCliente().getTipdoccli().getCodigo().equals("0")) {
                sql = "insert into clientesunat(tipdoc, doccli, nomcli, dircli) values(?,?,?,?)";
                resp = jdbcTemplate.update(sql,
                        new Object[]{
                            traslado.getCliente().getTipdoccli().getCodigo(),
                            traslado.getCliente().getDoccli(),
                            traslado.getCliente().getNomcli(),
                            traslado.getCliente().getDircli()
                        });
            }
        }
		return (resp>0?buscaTraslado(traslado.getCodtras()):null);
    }

    @Override
    public Traslado modificaTraslado(Traslado traslado) {
        String cUser = "usuario";
//        String cUser = SecurityContextHolder.getContext().getAuthentication().getName();
        Date   dUser = new Date();
        int resp=0;
        String sql = "update traslado set tipdoccli=?, doccli=?, nomcli=?, fectras=?, docref=?, coddocrf=?, fecdocrf=?, observ=?, usercr=?, dusercr=? where codcem=? and codtras=?";
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            traslado.getCliente().getTipdoccli().getCodigo(), traslado.getCliente().getDoccli(), 
                            traslado.getNomcli(), traslado.getFectras(), traslado.getDocref().getCodigo(),
                            traslado.getCoddocrf(), traslado.getFecdocrf(), traslado.getObserv(), cUser, dUser, 
                            traslado.getCemanterior().getCodcem(),traslado.getCodtras()
                          });
        if (resp>0){
            Cliente xCliente = null;
            if (!traslado.getCliente().getTipdoccli().getCodigo().equals("0")) {
                xCliente = daoCliente.buscaCliente(traslado.getCliente().getTipdoccli().getCodigo(), traslado.getCliente().getDoccli());
            }

            if (xCliente == null && !traslado.getCliente().getTipdoccli().getCodigo().equals("0")) {
                sql = "insert into clientesunat(tipdoc, doccli, nomcli, dircli) values(?,?,?,?)";
                resp = jdbcTemplate.update(sql,
                        new Object[]{
                            traslado.getCliente().getTipdoccli().getCodigo(),
                            traslado.getCliente().getDoccli(),
                            traslado.getCliente().getNomcli(),
                            traslado.getCliente().getDircli()
                        });
            }
        }
		return (resp>0?buscaTraslado(traslado.getCodtras()):null);
    }

    @Override
    public int eliminaTraslado(int codtras) throws Exception {
        int resp=0, xCodCem,xCodCuar,xFila1,xCol1, xCodDif, xCodMau;
        Traslado traslado = buscaTraslado(codtras);
        String sql = "delete from traslado where codtras=?";
        resp = jdbcTemplate.update(sql, new Object[]{codtras});
        if (resp>0){
            xCodCem = traslado.getDifuntoant().getCementerio().getCodcem();
            if (traslado.getDifuntoant().getTipo_entierro().getCodigo().equals("1")){
                xCodCuar = traslado.getDifuntoant().getCuartel().getCodcuar();
                xFila1 = traslado.getDifuntoant().getNicho().getFila1();
                xCol1  = traslado.getDifuntoant().getNicho().getCol1();
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
            }if (traslado.getDifuntoant().getTipo_entierro().getCodigo().equals("2")){
                // decrementa numero difuntos en mausoleo
                xCodDif = traslado.getDifuntoant().getMausoleo().getNumdif()+1;
                xCodMau = traslado.getDifuntoant().getMausoleo().getCodmau();
                
                String sqlMau = "update mausoleo set estado=?, numdif=? where codcem=? and codmau=?";
                    resp = jdbcTemplate.update(sqlMau,
                                      new Object[]{
                                          "20",xCodDif, xCodCem, xCodMau
                                      });
            }
            //estado difunto = 30
            
            String sqlMau = "update difunto set estado=?, codtras=? where codcem=? and coddif=?";
            resp = jdbcTemplate.update(sqlMau,
                                new Object[]{
                                      "00", 0 ,xCodCem, traslado.getDifuntoant().getCoddif()
                                });
            
            if (traslado.isLocal()){
                Difunto newDifunto = traslado.getDifuntonew();
                xCodCem = newDifunto.getCementerio().getCodcem();
                xCodDif = newDifunto.getCoddif();
                daoDifunto.eliminaDifunto(xCodCem, xCodDif);
            }
        }
        return resp;
    }

    @Override
    public Traslado buscaTraslado(int codtras) {
        String sql = "SELECT t.codtras, t.tipdoccli, t.doccli, t.nomcli, cl.dircli, "
                + "          date_format(t.fectras,'%d/%m/%Y') fectras, t.docref, t.coddocrf, date_format(t.fecdocrf,'%d/%m/%Y') fecdocrf, "
                + "          t.tiptras, tt.descri destiptras, t.coddifan, t.coddifnw, t.codceman, "
                + "          cant.nomcem nomcemant, t.codcemnw, cnew.nomcem nomcemnew, t.local, t.observ, t.estado, e.descri desestado"
                + "     FROM traslado t LEFT JOIN parmae tt ON tt.tipo='TIPTRS' AND t.tiptras=tt.codigo"
                + "                     LEFT JOIN cementerio cant ON t.codceman=cant.codcem"
                + "                     LEFT JOIN cementerio cnew ON t.codcemnw=cnew.codcem"
                + "                     left join clientesunat cl  on  cl.tipdoc=t.tipdoccli and cl.doccli=t.doccli"
                + "                     LEFT JOIN parmae e ON e.tipo='ESTTRS' AND t.estado=e.codigo"
                + "    WHERE codtras=?";
        
//        System.out.println("condicion++> " + condicion);
        List<Traslado> matches = jdbcTemplate.query (sql,
                new RowMapper<Traslado>() {
                @Override
                    public Traslado mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoccli"), "", "");
                        
                        Cliente cliente = null;
                        String nomcli = "";

                        if (!tipdoccli.getCodigo().equals("0")) {
                            //System.out.println("doc cli " + rs.getInt("tipdoccli") + "-" + rs.getString("doccli"));
                            cliente = daoCliente.buscaCliente(rs.getString("tipdoccli"), rs.getString("doccli"));
                        } else {
                            cliente = new Cliente();
                            cliente.setTipdoccli(tipdoccli);
                            cliente.setNomcli(rs.getString("nomcli"));
                        }
                        nomcli = cliente.getNomcli();
                        
                        Parmae docref = new Parmae();
                        docref.setTipo("TIPDOC");
                        docref.setCodigo(rs.getString("docref"));
                        docref.setDescri(rs.getString("coddocrf"));
                        
                        Parmae tiptras = new Parmae();
                        tiptras.setTipo("TIPTRS");
                        tiptras.setCodigo(rs.getString("tiptras"));
                        tiptras.setDescri(rs.getString("destiptras"));
                        
                        Parmae estado = new Parmae();
                        estado.setTipo("ESTTRS");
                        estado.setCodigo(rs.getString("estado"));
                        estado.setDescri(rs.getString("desestado"));
                        
                        Cementerio cemAnterior = daoCementerio.buscaCementerio(rs.getInt("codceman"));
                        Cementerio cemNuevo    = daoCementerio.buscaCementerio(rs.getInt("codcemnw"));
                        
                        Difunto difuntoant = daoDifunto.buscaDifunto(rs.getInt("codceman"), rs.getInt("coddifan"));
                        Difunto difuntonew = daoDifunto.buscaDifunto(rs.getInt("codcemnw"), rs.getInt("coddifnw"));
                        
                        Traslado traslado = new Traslado();
                        traslado.setCodtras(rs.getInt("codtras"));
                        traslado.setCliente(cliente);
                        traslado.setNomcli(nomcli);
                        traslado.setFectras(rs.getString("fectras"));
                        traslado.setDocref(docref);
                        traslado.setCoddocrf(rs.getString("coddocrf"));
                        traslado.setFecdocrf(rs.getString("fecdocrf"));
                        traslado.setTiptras(tiptras);
                        traslado.setCemanterior(cemAnterior);
                        traslado.setDifuntoant(difuntoant);
                        traslado.setCemnuevo(cemNuevo);
                        traslado.setDifuntonew(difuntonew);
                        traslado.setLocal(rs.getBoolean("local"));
                        traslado.setObserv(rs.getString("observ"));
                        traslado.setEstado(estado);
                        return traslado;
                    }
        }, new Object[] {codtras});
        return matches.size() > 0? (Traslado)matches.get(0): null;
    }

    @Override
    public List<Traslado> listaTraslado(String condicion, String limit, String orden) {
        String sql = "SELECT t.codtras, t.tipdoccli, t.doccli, t.nomcli, cl.dircli, "
                + "          date_format(t.fectras,'%d/%m/%Y') fectras, t.docref, t.coddocrf, date_format(t.fecdocrf,'%d/%m/%Y') fecdocrf,"
                + "          t.tiptras, tt.descri destiptras,t.coddifan,t.coddifnw,t.codceman, "
                + "          cant.nomcem nomcemant, t.codcemnw, cnew.nomcem nomcemnew, t.local,t.observ, t.estado, e.descri desestado"
                + "     FROM traslado t LEFT JOIN parmae tt ON tt.tipo='TIPTRS' AND t.tiptras=tt.codigo"
                + "                     LEFT JOIN cementerio cant ON t.codceman=cant.codcem"
                + "                     LEFT JOIN cementerio cnew ON t.codcemnw=cnew.codcem"
                + "                     left join clientesunat cl  on  cl.tipdoc=t.tipdoccli and cl.doccli=t.doccli"
                + "                     LEFT JOIN parmae e ON e.tipo='ESTTRS' AND t.estado=e.codigo"
                + "    WHERE 1=1 " + condicion ;
		sql += (!orden.isEmpty() ? " order by " + orden: " ");
		sql += (!limit.isEmpty() ? limit : "  limit  0, 100 ");
        
//        System.out.println("condicion++> " + sql);
        List<Traslado> matches = jdbcTemplate.query (sql,
                new RowMapper<Traslado>() {
                @Override
                    public Traslado mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Parmae tipdoccli = new Parmae("TDCLI ", rs.getString("tipdoccli"), "", "");
                        
                        Cliente cliente = null;
                        String nomcli = "";

//                            System.out.println("doc cli " + rs.getInt("tipdoccli") + "-" + rs.getString("doccli"));
                        if (!tipdoccli.getCodigo().equals("0")) {
                            cliente = daoCliente.buscaCliente(rs.getString("tipdoccli"), rs.getString("doccli"));
                        } else {
                            cliente = new Cliente();
                            cliente.setTipdoccli(tipdoccli);
                            cliente.setNomcli(rs.getString("nomcli"));
                        }
                        nomcli = cliente.getNomcli();
                        
                        Parmae docref = new Parmae();
                        docref.setTipo("TIPDOC");
                        docref.setCodigo(rs.getString("docref"));
//                        docref.setDescri(rs.getString("desdocref"));
                        
                        Parmae tiptras = new Parmae();
                        tiptras.setTipo("TIPTRS");
                        tiptras.setCodigo(rs.getString("tiptras"));
                        tiptras.setDescri(rs.getString("destiptras"));
                        
                        Parmae estado = new Parmae();
                        estado.setTipo("ESTTRS");
                        estado.setCodigo(rs.getString("estado"));
                        estado.setDescri(rs.getString("desestado"));
                        
                        Cementerio cemAnterior = daoCementerio.buscaCementerio(rs.getInt("codceman"));
                        Cementerio cemNuevo    = daoCementerio.buscaCementerio(rs.getInt("codcemnw"));
                        
                        Difunto difuntoant = daoDifunto.buscaDifunto(rs.getInt("codceman"), rs.getInt("coddifan"));
                        Difunto difuntonew = daoDifunto.buscaDifunto(rs.getInt("codcemnw"), rs.getInt("coddifnw"));
                        
                        Traslado traslado = new Traslado();
                        traslado.setCodtras(rs.getInt("codtras"));
                        traslado.setCliente(cliente);
                        traslado.setNomcli(nomcli);
                        traslado.setFectras(rs.getString("fectras"));
                        traslado.setDocref(docref);
                        traslado.setCoddocrf(rs.getString("coddocrf"));
                        traslado.setFecdocrf(rs.getString("fecdocrf"));
                        traslado.setTiptras(tiptras);
                        traslado.setCemanterior(cemAnterior);
                        traslado.setCemnuevo(cemNuevo);
                        traslado.setDifuntoant(difuntoant);
                        traslado.setDifuntonew(difuntonew);
                        traslado.setLocal(rs.getBoolean("local"));
                        traslado.setObserv(rs.getString("observ"));
                        traslado.setEstado(estado);
                        return traslado;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }

    public int insertaDifunto(Difunto difunto) {
        int resp=0;
        System.out.println("guadando...");
        String sql = "insert into difunto(coddif, fecfall, fecsep, apepat, apemat, nombres, sexo, edad_a, edad_m, edad_d, tipent, codcem, codcuar, codnic, fila1, columna1, fila2, columna2, codmau, tipdoccli, doccli, nomcli, codtras, codocu, estado, reservado, estvta, observ, user, duser) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//                                        coddif, fecfall, fecsep, apepat, apemat, nombres, nomdif, sexo, edad_a, edad_m, edad_d, tipent, codcem, codcuar, codnic, fila, columna, codmau, codcli, codtras, codocu, estado, reservado, estvta, observ 
        int xCodDif = daoDifunto.getNewIdDifunto(difunto.getCementerio().getCodcem());
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
            nicho = daoNichot.buscaNichot(difunto.getCementerio().getCodcem(), difunto.getCuartel().getCodcuar(), difunto.getNicho().getFila1(), difunto.getNicho().getCol1());
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
            mausoleo = daoMausoleo.buscaMausoleo(xCodCem, xCodMau);
            xNumDif = mausoleo.getNumdif()+1;
//            System.out.println("xNumDif.-=> " + xNumDif);
        } if (difunto.getTipo_entierro().getCodigo().equals("3")){
            
        }
//        int xCliente  = (difunto.getCliente()!=null?difunto.getCliente().getCodcli():0);
        int xTraslado = (difunto.getTraslado()!=null?difunto.getTraslado().getCodtras():0);
        int xCodOcu = (difunto.getOcufut()!=null?difunto.getOcufut().getCodocu():0);
        String xEstado = "00";
        String xEstVta = "00";
        
//        System.out.println("difunto en impl -=> " + difunto.toString());
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                              difunto.getCoddif(),difunto.getFecfall(),difunto.getFecsep(),difunto.getApepat(),
                              difunto.getApemat(),difunto.getNombres(),difunto.getSexodif(),
                              difunto.getEdad_a(),difunto.getEdad_m(),difunto.getEdad_d(),
                              difunto.getTipo_entierro().getCodigo(),difunto.getCementerio().getCodcem(),
                              difunto.getCuartel().getCodcuar(),xCodNic,xFila1,xCol1,xFila2,xCol2,
                              xCodMau,difunto.getCliente().getTipdoccli().getCodigo(), difunto.getCliente().getDoccli(),
                              difunto.getCliente().getNomcli(),xTraslado,xCodOcu,
                              xEstado, difunto.getReservado(),xEstVta,difunto.getObserv(),
                              cUser,dUser
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
        return resp;
    }
}