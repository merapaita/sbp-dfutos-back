package com.rosist.difunto.dao.impl;

import com.rosist.difunto.dao.NichonDao;
import com.rosist.difunto.modelSbp.Nicho_n;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class NichonImpl implements NichonDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insertaNichon(int codcem, int codcuar, Nicho_n nichon) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int modificaNichon(int codcem, int codcuar, int fila1, Nicho_n nichon) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int eliminaNichon(int codcem, int codcuar, int fila1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Nicho_n buscaNichon(int codcem, int codcuar, int fila1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Nicho_n> listaNichon(String condicion) {
        String sql = "select codcem, codcuar, fila1, fila2,"
                + "     col001, col002, col003, col004, col005, col006, col007, col008, col009, col010, col011, col012, col013, col014, col015, col016, col017, col018, col019, col020, "
                + "     col021, col022, col023, col024, col025, col026, col027, col028, col029, col030, col031, col032, col033, col034, col035, col036, col037, col038, col039, col040, "
                + "     col041, col042, col043, col044, col045, col046, col047, col048, col049, col050, col051, col052, col053, col054, col055, col056, col057, col058, col059, col060, "
                + "     col061, col062, col063, col064, col065, col066, col067, col068, col069, col070, col071, col072, col073, col074, col075, col076, col077, col078, col079, col080, "
                + "     col081, col082, col083, col084, col085, col086, col087, col088, col089, col090, col091, col092, col093, col094, col095, col096, col097, col098, col099, col100, "
                + "     col101, col102, col103, col104, col105, col106, col107, col108, col109, col110, col111, col112, col113, col114, col115, col116, col117, col118, col119, col120, "
                + "     col121, col122, col123, col124, col125, col126, col127, col128, col129, col130, col131, col132, col133, col134, col135, col136, col137, col138, col139, col140, "
                + "     col141, col142, col143, col144, col145, col146, col147, col148, col149, col150, col151, col152, col153, col154, col155, col156, col157, col158, col159, col160, "
                + "     col161, col162, col163, col164, col165, col166, col167, col168, col169, col170, col171, col172, col173, col174, col175, col176, col177, col178, col179, col180, "
                + "     col181, col182, col183, col184, col185, col186, col187, col188, col189, col190, col191, col192, col193, col194, col195, col196, col197, col198, col199, col200 "
                + "   from nicho_n"
                + " where 1=1 " + condicion;
        System.out.println("Condicion==> " + condicion);
        System.out.println("SQL ==> " + sql);
//        System.out.println("condicion++> " + condicion);
        List<Nicho_n> matches = jdbcTemplate.query (sql,
                new RowMapper<Nicho_n>() {
                @Override
                    public Nicho_n mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Nicho_n nichos_n = new Nicho_n();
                        
                        nichos_n.setFila1(rs.getInt("fila1"));
                        nichos_n.setFila2(rs.getString("fila2"));
                        nichos_n.setCol001(rs.getInt("col001"));
                        nichos_n.setCol002(rs.getInt("col002"));
                        nichos_n.setCol003(rs.getInt("col003"));
                        nichos_n.setCol004(rs.getInt("col004"));
                        nichos_n.setCol005(rs.getInt("col005"));
                        nichos_n.setCol006(rs.getInt("col006"));
                        nichos_n.setCol007(rs.getInt("col007"));
                        nichos_n.setCol008(rs.getInt("col008"));
                        nichos_n.setCol009(rs.getInt("col009"));
                        nichos_n.setCol010(rs.getInt("col010"));
                        nichos_n.setCol011(rs.getInt("col011"));
                        nichos_n.setCol012(rs.getInt("col012"));
                        nichos_n.setCol013(rs.getInt("col013"));
                        nichos_n.setCol014(rs.getInt("col014"));
                        nichos_n.setCol015(rs.getInt("col015"));
                        nichos_n.setCol016(rs.getInt("col016"));
                        nichos_n.setCol017(rs.getInt("col017"));
                        nichos_n.setCol018(rs.getInt("col018"));
                        nichos_n.setCol019(rs.getInt("col019"));
                        nichos_n.setCol020(rs.getInt("col020"));
                        nichos_n.setCol021(rs.getInt("col021"));
                        nichos_n.setCol022(rs.getInt("col022"));
                        nichos_n.setCol023(rs.getInt("col023"));
                        nichos_n.setCol024(rs.getInt("col024"));
                        nichos_n.setCol025(rs.getInt("col025"));
                        nichos_n.setCol026(rs.getInt("col026"));
                        nichos_n.setCol027(rs.getInt("col027"));
                        nichos_n.setCol028(rs.getInt("col028"));
                        nichos_n.setCol029(rs.getInt("col029"));
                        nichos_n.setCol030(rs.getInt("col030"));
                        nichos_n.setCol031(rs.getInt("col031"));
                        nichos_n.setCol032(rs.getInt("col032"));
                        nichos_n.setCol033(rs.getInt("col033"));
                        nichos_n.setCol034(rs.getInt("col034"));
                        nichos_n.setCol035(rs.getInt("col035"));
                        nichos_n.setCol036(rs.getInt("col036"));
                        nichos_n.setCol037(rs.getInt("col037"));
                        nichos_n.setCol038(rs.getInt("col038"));
                        nichos_n.setCol039(rs.getInt("col039"));
                        nichos_n.setCol040(rs.getInt("col040"));
                        nichos_n.setCol041(rs.getInt("col041"));
                        nichos_n.setCol042(rs.getInt("col042"));
                        nichos_n.setCol043(rs.getInt("col043"));
                        nichos_n.setCol044(rs.getInt("col044"));
                        nichos_n.setCol045(rs.getInt("col045"));
                        nichos_n.setCol046(rs.getInt("col046"));
                        nichos_n.setCol047(rs.getInt("col047"));
                        nichos_n.setCol048(rs.getInt("col048"));
                        nichos_n.setCol049(rs.getInt("col049"));
                        nichos_n.setCol050(rs.getInt("col050"));
                        nichos_n.setCol051(rs.getInt("col051"));
                        nichos_n.setCol052(rs.getInt("col052"));
                        nichos_n.setCol053(rs.getInt("col053"));
                        nichos_n.setCol054(rs.getInt("col054"));
                        nichos_n.setCol055(rs.getInt("col055"));
                        nichos_n.setCol056(rs.getInt("col056"));
                        nichos_n.setCol057(rs.getInt("col057"));
                        nichos_n.setCol058(rs.getInt("col058"));
                        nichos_n.setCol059(rs.getInt("col059"));
                        nichos_n.setCol060(rs.getInt("col060"));
                        nichos_n.setCol061(rs.getInt("col061"));
                        nichos_n.setCol062(rs.getInt("col062"));
                        nichos_n.setCol063(rs.getInt("col063"));
                        nichos_n.setCol064(rs.getInt("col064"));
                        nichos_n.setCol065(rs.getInt("col065"));
                        nichos_n.setCol066(rs.getInt("col066"));
                        nichos_n.setCol067(rs.getInt("col067"));
                        nichos_n.setCol068(rs.getInt("col068"));
                        nichos_n.setCol069(rs.getInt("col069"));
                        nichos_n.setCol070(rs.getInt("col070"));
                        nichos_n.setCol071(rs.getInt("col071"));
                        nichos_n.setCol072(rs.getInt("col072"));
                        nichos_n.setCol073(rs.getInt("col073"));
                        nichos_n.setCol074(rs.getInt("col074"));
                        nichos_n.setCol075(rs.getInt("col075"));
                        nichos_n.setCol076(rs.getInt("col076"));
                        nichos_n.setCol077(rs.getInt("col077"));
                        nichos_n.setCol078(rs.getInt("col078"));
                        nichos_n.setCol079(rs.getInt("col079"));
                        nichos_n.setCol080(rs.getInt("col080"));
                        nichos_n.setCol081(rs.getInt("col081"));
                        nichos_n.setCol082(rs.getInt("col082"));
                        nichos_n.setCol083(rs.getInt("col083"));
                        nichos_n.setCol084(rs.getInt("col084"));
                        nichos_n.setCol085(rs.getInt("col085"));
                        nichos_n.setCol086(rs.getInt("col086"));
                        nichos_n.setCol087(rs.getInt("col087"));
                        nichos_n.setCol088(rs.getInt("col088"));
                        nichos_n.setCol089(rs.getInt("col089"));
                        nichos_n.setCol090(rs.getInt("col090"));
                        nichos_n.setCol091(rs.getInt("col091"));
                        nichos_n.setCol092(rs.getInt("col092"));
                        nichos_n.setCol093(rs.getInt("col093"));
                        nichos_n.setCol094(rs.getInt("col094"));
                        nichos_n.setCol095(rs.getInt("col095"));
                        nichos_n.setCol096(rs.getInt("col096"));
                        nichos_n.setCol097(rs.getInt("col097"));
                        nichos_n.setCol098(rs.getInt("col098"));
                        nichos_n.setCol099(rs.getInt("col099"));
                        nichos_n.setCol100(rs.getInt("col100"));
                        nichos_n.setCol101(rs.getInt("col101"));
                        nichos_n.setCol102(rs.getInt("col102"));
                        nichos_n.setCol103(rs.getInt("col103"));
                        nichos_n.setCol104(rs.getInt("col104"));
                        nichos_n.setCol105(rs.getInt("col105"));
                        nichos_n.setCol106(rs.getInt("col106"));
                        nichos_n.setCol107(rs.getInt("col107"));
                        nichos_n.setCol108(rs.getInt("col108"));
                        nichos_n.setCol109(rs.getInt("col109"));
                        nichos_n.setCol110(rs.getInt("col110"));
                        nichos_n.setCol111(rs.getInt("col111"));
                        nichos_n.setCol112(rs.getInt("col112"));
                        nichos_n.setCol113(rs.getInt("col113"));
                        nichos_n.setCol114(rs.getInt("col114"));
                        nichos_n.setCol115(rs.getInt("col115"));
                        nichos_n.setCol116(rs.getInt("col116"));
                        nichos_n.setCol117(rs.getInt("col117"));
                        nichos_n.setCol118(rs.getInt("col118"));
                        nichos_n.setCol119(rs.getInt("col119"));
                        nichos_n.setCol120(rs.getInt("col120"));
                        nichos_n.setCol121(rs.getInt("col121"));
                        nichos_n.setCol122(rs.getInt("col122"));
                        nichos_n.setCol123(rs.getInt("col123"));
                        nichos_n.setCol124(rs.getInt("col124"));
                        nichos_n.setCol125(rs.getInt("col125"));
                        nichos_n.setCol126(rs.getInt("col126"));
                        nichos_n.setCol127(rs.getInt("col127"));
                        nichos_n.setCol128(rs.getInt("col128"));
                        nichos_n.setCol129(rs.getInt("col129"));
                        nichos_n.setCol130(rs.getInt("col130"));
                        nichos_n.setCol131(rs.getInt("col131"));
                        nichos_n.setCol132(rs.getInt("col132"));
                        nichos_n.setCol133(rs.getInt("col133"));
                        nichos_n.setCol134(rs.getInt("col134"));
                        nichos_n.setCol135(rs.getInt("col135"));
                        nichos_n.setCol136(rs.getInt("col136"));
                        nichos_n.setCol137(rs.getInt("col137"));
                        nichos_n.setCol138(rs.getInt("col138"));
                        nichos_n.setCol139(rs.getInt("col139"));
                        nichos_n.setCol140(rs.getInt("col140"));
                        nichos_n.setCol141(rs.getInt("col141"));
                        nichos_n.setCol142(rs.getInt("col142"));
                        nichos_n.setCol143(rs.getInt("col143"));
                        nichos_n.setCol144(rs.getInt("col144"));
                        nichos_n.setCol145(rs.getInt("col145"));
                        nichos_n.setCol146(rs.getInt("col146"));
                        nichos_n.setCol147(rs.getInt("col147"));
                        nichos_n.setCol148(rs.getInt("col148"));
                        nichos_n.setCol149(rs.getInt("col149"));
                        nichos_n.setCol150(rs.getInt("col150"));
                        nichos_n.setCol151(rs.getInt("col151"));
                        nichos_n.setCol152(rs.getInt("col152"));
                        nichos_n.setCol153(rs.getInt("col153"));
                        nichos_n.setCol154(rs.getInt("col154"));
                        nichos_n.setCol155(rs.getInt("col155"));
                        nichos_n.setCol156(rs.getInt("col156"));
                        nichos_n.setCol157(rs.getInt("col157"));
                        nichos_n.setCol158(rs.getInt("col158"));
                        nichos_n.setCol159(rs.getInt("col159"));
                        nichos_n.setCol160(rs.getInt("col160"));
                        nichos_n.setCol161(rs.getInt("col161"));
                        nichos_n.setCol162(rs.getInt("col162"));
                        nichos_n.setCol163(rs.getInt("col163"));
                        nichos_n.setCol164(rs.getInt("col164"));
                        nichos_n.setCol165(rs.getInt("col165"));
                        nichos_n.setCol166(rs.getInt("col166"));
                        nichos_n.setCol167(rs.getInt("col167"));
                        nichos_n.setCol168(rs.getInt("col168"));
                        nichos_n.setCol169(rs.getInt("col169"));
                        nichos_n.setCol170(rs.getInt("col170"));
                        nichos_n.setCol171(rs.getInt("col171"));
                        nichos_n.setCol172(rs.getInt("col172"));
                        nichos_n.setCol173(rs.getInt("col173"));
                        nichos_n.setCol174(rs.getInt("col174"));
                        nichos_n.setCol175(rs.getInt("col175"));
                        nichos_n.setCol176(rs.getInt("col176"));
                        nichos_n.setCol177(rs.getInt("col177"));
                        nichos_n.setCol178(rs.getInt("col178"));
                        nichos_n.setCol179(rs.getInt("col179"));
                        nichos_n.setCol180(rs.getInt("col180"));
                        nichos_n.setCol181(rs.getInt("col181"));
                        nichos_n.setCol182(rs.getInt("col182"));
                        nichos_n.setCol183(rs.getInt("col183"));
                        nichos_n.setCol184(rs.getInt("col184"));
                        nichos_n.setCol185(rs.getInt("col185"));
                        nichos_n.setCol186(rs.getInt("col186"));
                        nichos_n.setCol187(rs.getInt("col187"));
                        nichos_n.setCol188(rs.getInt("col188"));
                        nichos_n.setCol189(rs.getInt("col189"));
                        nichos_n.setCol190(rs.getInt("col190"));
                        nichos_n.setCol191(rs.getInt("col191"));
                        nichos_n.setCol192(rs.getInt("col192"));
                        nichos_n.setCol193(rs.getInt("col193"));
                        nichos_n.setCol194(rs.getInt("col194"));
                        nichos_n.setCol195(rs.getInt("col195"));
                        nichos_n.setCol196(rs.getInt("col196"));
                        nichos_n.setCol197(rs.getInt("col197"));
                        nichos_n.setCol198(rs.getInt("col198"));
                        nichos_n.setCol199(rs.getInt("col199"));
                        nichos_n.setCol200(rs.getInt("col200"));
                        return nichos_n;
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }
    
}
