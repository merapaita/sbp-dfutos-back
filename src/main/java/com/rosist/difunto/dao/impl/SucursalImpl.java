package com.rosist.difunto.dao.impl;

import com.rosist.difunto.modelSbp.Sucursal;
import com.rosist.difunto.modelSbp.Ubigeo;
import com.rosist.difunto.dao.SucursalDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class SucursalImpl implements SucursalDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public int getNewIdSucursal() {
    	String sql = "select ifnull(max(tiping),0)+1 from sucursal";
        int nCorrel = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nCorrel;
    }
    
    @Override
    public int getSucursalCount() {
        String sql = "select count(*) as count from sucursal";
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int getSucursalCount(String condicion) {
        String sql = "select count(*) as count from sucursal where 1=1 " + condicion;
        int nNumReg = this.jdbcTemplate.queryForObject(sql, Integer.class);
        return nNumReg;
    }

    @Override
    public int insertaSucursal(Sucursal sucursal) {
        int resp=0;
        System.out.println("guadando...");
        String sql = "insert into sucursal(tiping, descri, coddep, codprv, coddis, direcc, telefono, fax, email) values(?,?,?,?,?,?,?,?,?)";
        
        int xTipIng = getNewIdSucursal();
        sucursal.setTiping(xTipIng);
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            sucursal.getTiping(), sucursal.getDescri(), sucursal.getUbigeo().getCoddep(),
                              sucursal.getUbigeo().getCodprv(), sucursal.getUbigeo().getCoddis(), sucursal.getDireccion(),
                              sucursal.getTelefono(), sucursal.getFax(), sucursal.getEmail()
                          }
        );
        return resp;
    }

    @Override
    public int modificaSucursal(Sucursal sucursal) {
        int resp=0;
        String sql = "update sucursal set descri=?, coddel=?, codprv=?, coddis=?, direcc=?, telefono=?, fax=?, email=? where tiping=?";
        resp = jdbcTemplate.update(sql,
                          new Object[]{
                            sucursal.getDescri(), sucursal.getUbigeo().getCoddep(), sucursal.getUbigeo().getCodprv(),
                              sucursal.getUbigeo().getCoddis(), sucursal.getDireccion(),
                              sucursal.getTelefono(), sucursal.getFax(), sucursal.getEmail(),sucursal.getTiping()
                          }
        );
        return resp;
    }

    @Override
    public int eliminaSucursal(int tiping) {
        ////////////////////////////////////////////////////
        //      O J O      L E E R       E S T O          //
        // buscamos clientes con en mausoleos, difuntos, otros //
        // si NO se encuentran que proceda la emilinacion //
        ////////////////////////////////////////////////////
        
        int resp=0;
        String sql = "delete from sucursal where tiping=?";
        resp = jdbcTemplate.update(sql, new Object[]{tiping});
        return resp;
    }

    @Override
    public Sucursal buscaSucursal(int tiping) {
        String sql = "select s.tiping, s.descri, s.coddep, u.nomdep, s.codprv, u.nomprv, s.coddis, u.nomdis,"
                + "          s.direcc, s.telefono, s.fax, s.email "
                + "     from sucursal s left join ubigeo u on s.coddep=u.coddep and s.codprv=u.codprv and "
                + "                                           s.coddis=u.coddis"
                + "    where tiping=?";
        
        List<Sucursal> matches = jdbcTemplate.query (sql,
                new RowMapper<Sucursal>() {
                @Override
                    public Sucursal mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Ubigeo ubigeo = new Ubigeo();
                        ubigeo.setCoddep(rs.getString("coddep"));
                        ubigeo.setCodprv(rs.getString("codprv"));
                        ubigeo.setCoddis(rs.getString("coddis"));
                        ubigeo.setNomdep(rs.getString("nomdep"));
                        ubigeo.setNomprv(rs.getString("nomprv"));
                        ubigeo.setNomdis(rs.getString("nomdis"));
                        
                        Sucursal sucursal = new Sucursal();
                        sucursal.setTiping(rs.getInt("tiping"));
                        sucursal.setDescri(rs.getString("descri"));
                        sucursal.setUbigeo(ubigeo);
                        sucursal.setDireccion(rs.getString("direcc"));
                        sucursal.setTelefono(rs.getString("telefono"));
                        sucursal.setFax(rs.getString("fax"));
                        sucursal.setEmail(rs.getString("email"));
//                           tiping, descri, coddep, codprv, coddis, direcc, telefono, fax, email
                        return sucursal;
                    }
        }, new Object[] {tiping});
        return matches.size() > 0? (Sucursal)matches.get(0): null;
    }
    
    @Override
    public List<Sucursal> listaSucursal(String condicion) {
        String sql = "select s.tiping, s.descri, s.coddep, u.nomdep, s.codprv, u.nomprv, s.coddis, u.nomdis,"
                + "          s.direcc, s.telefono, s.fax, s.email "
                + "     from sucursal s left join ubigeo u on s.coddep=u.coddep and s.codprv=u.codprv and "
                + "                                           s.coddis=u.coddis"
                + "    where 1=1" + condicion
                + "    order by s.tiping";
                
        List<Sucursal> matches = jdbcTemplate.query (sql,
                new RowMapper<Sucursal>() {
                @Override
                    public Sucursal mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Ubigeo ubigeo = new Ubigeo();
                        ubigeo.setCoddep(rs.getString("coddep"));
                        ubigeo.setCodprv(rs.getString("codprv"));
                        ubigeo.setCoddis(rs.getString("coddis"));
                        ubigeo.setNomdep(rs.getString("nomdep"));
                        ubigeo.setNomprv(rs.getString("nomprv"));
                        ubigeo.setNomdis(rs.getString("nomdis"));
                        
                        Sucursal sucursal = new Sucursal();
                        sucursal.setTiping(rs.getInt("tiping"));
                        sucursal.setDescri(rs.getString("descri"));
                        sucursal.setUbigeo(ubigeo);
                        sucursal.setDireccion(rs.getString("direcc"));
                        sucursal.setTelefono(rs.getString("telefono"));
                        sucursal.setFax(rs.getString("fax"));
                        sucursal.setEmail(rs.getString("email"));
                        
                        return sucursal;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }
}