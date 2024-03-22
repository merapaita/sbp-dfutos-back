package com.rosist.difunto.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.rosist.difunto.dao.EmpresaDao;
import com.rosist.difunto.modelSbp.Empresa;
import com.rosist.difunto.modelSbp.Ubigeo;

@Repository
public class EmpresaImpl implements EmpresaDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
//    @Autowired
//    private IngresoServicio servIngreso;
    
    @Override
    public int getEmpresaCount() {
        String sql = "select count(*) as count from empresa";
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getEmpresaCount(String condicion) {
        String sql = "select count(*) as count "
                + "     from empresa "
                + "    where 1=1 " + condicion;

        int nNumReg = this.jdbcTemplate.queryForObject(sql,Integer.class);
        return nNumReg;
    }

    @Override
    public int getNewIdEmpresa() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int insertaEmpresa(Empresa empresa) {
        int resp=0;
        System.out.println("guadando...");
        String sql = "insert into Empresa(ruc, razsoc, nomcom, ubigeo, direc, urban, ususol, clasol, ruta, clacert) values(?,?,?,?,?,?,?,?,?,?)";
        
//      e.ruc, e.razsoc, e.nomcom, e.ubigeo, "
//      e.direc, e.urban, e.ususol, "
//      e.clasol, e.ruta, e.cert, e.clacert
        
//        System.out.println("servicio em implement. -=>" + servicio.toString());
        String cUbigeo = empresa.getUbigeo().getCoddep() + empresa.getUbigeo().getCodprv() + empresa.getUbigeo().getCoddis();
        
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            empresa.getRuc(), empresa.getRazsoc(), empresa.getNomcom(), cUbigeo,
                            empresa.getDirec(), empresa.getUrban(), empresa.getUsusol(),
                            empresa.getClasol(), empresa.getRuta(), empresa.getCert(), empresa.getClacert()
                          });
        return resp;
    }
    
    @Override
    public int modificaEmpresa(Empresa empresa) {
        int resp=0;
        String sql = "update empresa set razsoc=?, nomcom=?, ubigeo=?, direc=?, urban=?, ususol=?, clasol=?, ruta=?, cert=?, clacert=? where ruc=?";
        String cUbigeo = empresa.getUbigeo().getCoddep() + empresa.getUbigeo().getCodprv() + empresa.getUbigeo().getCoddis();
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                                  empresa.getRazsoc(), empresa.getNomcom(), cUbigeo,
                                  empresa.getDirec(), empresa.getUrban(), empresa.getUsusol(),
                                  empresa.getClasol(), empresa.getRuta(), empresa.getCert(), empresa.getClacert(), empresa.getRuc()
                          }
        );
        return resp;
    }

    @Override
    public String eliminaEmpresa(String ruc) {
        int resp=0;
        String mRet = "";
//        List<Ingreso> lIngreso = null;
//        List<Ingreso> lIngreso = servIngreso.listaIngresos(" and i.tipdoccli='" + tipdoccli + "' and i.doccli='" + doccli+"'","", "asc");
//        if (lIngreso!=null){
//            mRet = "Cliente ya existe en ingreso";
//        }
        if (mRet.isEmpty()){
            String sql = "delete from empresa where ruc=?";
            resp = jdbcTemplate.update(sql, new Object[]{ruc});
        }
        
        if (resp>0){
            mRet = "ok";
        }
        return mRet;
    }

    @Override
    public Empresa buscaEmpresa(String ruc) {
        String sql =  "select e.ruc, e.razsoc, e.nomcom, e.ubigeo, u.coddep, u.nomdep, u.codprv, u.nomprv, u.coddis, "
        		+ "           u.nomdis, e.direc, e.urban, e.ususol, "
        		+ "           e.clasol, e.ruta, e.cert, e.clacert "
        		+ "      from empresa e LEFT JOIN ubigeo u ON e.ubigeo=u.ubigeo "
                + "     where e.ruc=?";

        List<Empresa> matches = jdbcTemplate.query (sql,
                new RowMapper<Empresa>() {
                @Override
                    public Empresa mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                		Ubigeo ubigeo = new Ubigeo();
                		ubigeo.setUbigeo(rs.getString("coddep") + rs.getString("codprv") + rs.getString("coddis"));
                		ubigeo.setCoddep(rs.getString("coddep"));
                		ubigeo.setCodprv(rs.getString("codprv"));
                		ubigeo.setCoddis(rs.getString("coddis"));
                		ubigeo.setNomdep(rs.getString("nomdep"));
                		ubigeo.setNomprv(rs.getString("nomprv"));
                		ubigeo.setNomdis(rs.getString("nomdis"));
                		
                		Empresa empresa = new Empresa();
                		empresa.setRuc(rs.getString("ruc"));
                		empresa.setRazsoc(rs.getString("razsoc"));
                		empresa.setNomcom(rs.getString("nomcom"));
                		empresa.setUbigeo(ubigeo);
                		empresa.setDirec(rs.getString("direc"));
                		empresa.setUrban(rs.getString("urban"));
                		empresa.setUsusol(rs.getString("ususol"));
                		empresa.setClasol(rs.getString("clasol"));
                		empresa.setRuta(rs.getString("ruta"));
                		empresa.setCert(rs.getString("cert"));
                		empresa.setClacert(rs.getString("clacert"));
                        
                        return empresa;
                    }
        }, new Object[] {ruc});
        return matches.size() > 0? (Empresa)matches.get(0): null;
    }

    @Override
    public List<Empresa> listaEmpresa(String condicion) {
        String sql =  "select e.ruc, e.razsoc, e.nomcom, e.ubigeo, u.coddep, u.nomdep, u.codprv, u.nomprv, u.coddis, "
        		+ "           u.nomdis, e.direc, e.urban, e.ususol, "
        		+ "           e.clasol, e.ruta, e.cert, e.clacert "
        		+ "      from empresa e LEFT JOIN ubigeo u ON e.ubigeo=u.ubigeo "
        		+ "     where 1=1 " + condicion;
        
        List<Empresa> matches = jdbcTemplate.query (sql,
                new RowMapper<Empresa>() {
                @Override
                    public Empresa mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                		Ubigeo ubigeo = new Ubigeo();
                		ubigeo.setUbigeo(rs.getString("coddep") + rs.getString("codprv") + rs.getString("coddis"));
                		ubigeo.setCoddep(rs.getString("coddep"));
                		ubigeo.setCodprv(rs.getString("codprv"));
                		ubigeo.setCoddis(rs.getString("coddis"));
                		ubigeo.setNomdep(rs.getString("nomdep"));
                		ubigeo.setNomprv(rs.getString("nomprv"));
                		ubigeo.setNomdis(rs.getString("nomdis"));
                        
                        Empresa empresa = new Empresa();
                        empresa.setRuc(rs.getString("ruc"));
                        empresa.setRazsoc(rs.getString("razsoc"));
                        empresa.setNomcom(rs.getString("nomcom"));
                        empresa.setUbigeo(ubigeo);
                        empresa.setDirec(rs.getString("direc"));
                        empresa.setUrban(rs.getString("urban"));
                        empresa.setUsusol(rs.getString("ususol"));
                        empresa.setClasol(rs.getString("clasol"));
                        empresa.setRuta(rs.getString("ruta"));
                        empresa.setCert(rs.getString("cert"));
                        empresa.setClacert(rs.getString("clacert"));
                        
//                      e.ruc, e.razsoc, e.nomcom, e.ubigeo, "
//                      e.direc, e.urban, e.ususol, "
//                      e.clasol, e.ruta, e.cert, e.clacert
                        return empresa;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }
}