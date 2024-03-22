package com.rosist.difunto.dao.impl;

import com.rosist.difunto.modelSbp.Nicho_e;
import com.rosist.difunto.dao.NichoeDao;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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
public class NichoeImpl implements NichoeDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int insertaNichoe(int codcem, int codcuar, int fila1, Nicho_e nichoe) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int modificaNichoe(int codcem, int codcuar, int fila1,Nicho_e nichoe) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Nicho_e buscaNichoe(int codcem, int codcuar, int fila1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public List<Nicho_e> listaNichoe(String condicion) {
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
                + "   from nicho_e"
                + " where 1=1 " + condicion;
        List<Nicho_e> matches = jdbcTemplate.query (sql,
                new RowMapper<Nicho_e>() {
                @Override
                    public Nicho_e mapRow (ResultSet rs, int rowNum) throws SQLException, DataAccessException {
                        Nicho_e nichos_e = new Nicho_e();
                        
                        nichos_e.setFila1(rs.getInt("fila1"));
                        nichos_e.setFila2(rs.getString("fila2"));
                        nichos_e.setCol001(rs.getString("col001"));
                        nichos_e.setCol002(rs.getString("col002"));
                        nichos_e.setCol003(rs.getString("col003"));
                        nichos_e.setCol004(rs.getString("col004"));
                        nichos_e.setCol005(rs.getString("col005"));
                        nichos_e.setCol006(rs.getString("col006"));
                        nichos_e.setCol007(rs.getString("col007"));
                        nichos_e.setCol008(rs.getString("col008"));
                        nichos_e.setCol009(rs.getString("col009"));
                        nichos_e.setCol010(rs.getString("col010"));
                        nichos_e.setCol011(rs.getString("col011"));
                        nichos_e.setCol012(rs.getString("col012"));
                        nichos_e.setCol013(rs.getString("col013"));
                        nichos_e.setCol014(rs.getString("col014"));
                        nichos_e.setCol015(rs.getString("col015"));
                        nichos_e.setCol016(rs.getString("col016"));
                        nichos_e.setCol017(rs.getString("col017"));
                        nichos_e.setCol018(rs.getString("col018"));
                        nichos_e.setCol019(rs.getString("col019"));
                        nichos_e.setCol020(rs.getString("col020"));
                        nichos_e.setCol021(rs.getString("col021"));
                        nichos_e.setCol022(rs.getString("col022"));
                        nichos_e.setCol023(rs.getString("col023"));
                        nichos_e.setCol024(rs.getString("col024"));
                        nichos_e.setCol025(rs.getString("col025"));
                        nichos_e.setCol026(rs.getString("col026"));
                        nichos_e.setCol027(rs.getString("col027"));
                        nichos_e.setCol028(rs.getString("col028"));
                        nichos_e.setCol029(rs.getString("col029"));
                        nichos_e.setCol030(rs.getString("col030"));
                        nichos_e.setCol031(rs.getString("col031"));
                        nichos_e.setCol032(rs.getString("col032"));
                        nichos_e.setCol033(rs.getString("col033"));
                        nichos_e.setCol034(rs.getString("col034"));
                        nichos_e.setCol035(rs.getString("col035"));
                        nichos_e.setCol036(rs.getString("col036"));
                        nichos_e.setCol037(rs.getString("col037"));
                        nichos_e.setCol038(rs.getString("col038"));
                        nichos_e.setCol039(rs.getString("col039"));
                        nichos_e.setCol040(rs.getString("col040"));
                        nichos_e.setCol041(rs.getString("col041"));
                        nichos_e.setCol042(rs.getString("col042"));
                        nichos_e.setCol043(rs.getString("col043"));
                        nichos_e.setCol044(rs.getString("col044"));
                        nichos_e.setCol045(rs.getString("col045"));
                        nichos_e.setCol046(rs.getString("col046"));
                        nichos_e.setCol047(rs.getString("col047"));
                        nichos_e.setCol048(rs.getString("col048"));
                        nichos_e.setCol049(rs.getString("col049"));
                        nichos_e.setCol050(rs.getString("col050"));
                        nichos_e.setCol051(rs.getString("col051"));
                        nichos_e.setCol052(rs.getString("col052"));
                        nichos_e.setCol053(rs.getString("col053"));
                        nichos_e.setCol054(rs.getString("col054"));
                        nichos_e.setCol055(rs.getString("col055"));
                        nichos_e.setCol056(rs.getString("col056"));
                        nichos_e.setCol057(rs.getString("col057"));
                        nichos_e.setCol058(rs.getString("col058"));
                        nichos_e.setCol059(rs.getString("col059"));
                        nichos_e.setCol060(rs.getString("col060"));
                        nichos_e.setCol061(rs.getString("col061"));
                        nichos_e.setCol062(rs.getString("col062"));
                        nichos_e.setCol063(rs.getString("col063"));
                        nichos_e.setCol064(rs.getString("col064"));
                        nichos_e.setCol065(rs.getString("col065"));
                        nichos_e.setCol066(rs.getString("col066"));
                        nichos_e.setCol067(rs.getString("col067"));
                        nichos_e.setCol068(rs.getString("col068"));
                        nichos_e.setCol069(rs.getString("col069"));
                        nichos_e.setCol070(rs.getString("col070"));
                        nichos_e.setCol071(rs.getString("col071"));
                        nichos_e.setCol072(rs.getString("col072"));
                        nichos_e.setCol073(rs.getString("col073"));
                        nichos_e.setCol074(rs.getString("col074"));
                        nichos_e.setCol075(rs.getString("col075"));
                        nichos_e.setCol076(rs.getString("col076"));
                        nichos_e.setCol077(rs.getString("col077"));
                        nichos_e.setCol078(rs.getString("col078"));
                        nichos_e.setCol079(rs.getString("col079"));
                        nichos_e.setCol080(rs.getString("col080"));
                        nichos_e.setCol081(rs.getString("col081"));
                        nichos_e.setCol082(rs.getString("col082"));
                        nichos_e.setCol083(rs.getString("col083"));
                        nichos_e.setCol084(rs.getString("col084"));
                        nichos_e.setCol085(rs.getString("col085"));
                        nichos_e.setCol086(rs.getString("col086"));
                        nichos_e.setCol087(rs.getString("col087"));
                        nichos_e.setCol088(rs.getString("col088"));
                        nichos_e.setCol089(rs.getString("col089"));
                        nichos_e.setCol090(rs.getString("col090"));
                        nichos_e.setCol091(rs.getString("col091"));
                        nichos_e.setCol092(rs.getString("col092"));
                        nichos_e.setCol093(rs.getString("col093"));
                        nichos_e.setCol094(rs.getString("col094"));
                        nichos_e.setCol095(rs.getString("col095"));
                        nichos_e.setCol096(rs.getString("col096"));
                        nichos_e.setCol097(rs.getString("col097"));
                        nichos_e.setCol098(rs.getString("col098"));
                        nichos_e.setCol099(rs.getString("col099"));
                        nichos_e.setCol100(rs.getString("col100"));
                        nichos_e.setCol101(rs.getString("col101"));
                        nichos_e.setCol102(rs.getString("col102"));
                        nichos_e.setCol103(rs.getString("col103"));
                        nichos_e.setCol104(rs.getString("col104"));
                        nichos_e.setCol105(rs.getString("col105"));
                        nichos_e.setCol106(rs.getString("col106"));
                        nichos_e.setCol107(rs.getString("col107"));
                        nichos_e.setCol108(rs.getString("col108"));
                        nichos_e.setCol109(rs.getString("col109"));
                        nichos_e.setCol110(rs.getString("col110"));
                        nichos_e.setCol111(rs.getString("col111"));
                        nichos_e.setCol112(rs.getString("col112"));
                        nichos_e.setCol113(rs.getString("col113"));
                        nichos_e.setCol114(rs.getString("col114"));
                        nichos_e.setCol115(rs.getString("col115"));
                        nichos_e.setCol116(rs.getString("col116"));
                        nichos_e.setCol117(rs.getString("col117"));
                        nichos_e.setCol118(rs.getString("col118"));
                        nichos_e.setCol119(rs.getString("col119"));
                        nichos_e.setCol120(rs.getString("col120"));
                        nichos_e.setCol121(rs.getString("col121"));
                        nichos_e.setCol122(rs.getString("col122"));
                        nichos_e.setCol123(rs.getString("col123"));
                        nichos_e.setCol124(rs.getString("col124"));
                        nichos_e.setCol125(rs.getString("col125"));
                        nichos_e.setCol126(rs.getString("col126"));
                        nichos_e.setCol127(rs.getString("col127"));
                        nichos_e.setCol128(rs.getString("col128"));
                        nichos_e.setCol129(rs.getString("col129"));
                        nichos_e.setCol130(rs.getString("col130"));
                        nichos_e.setCol131(rs.getString("col131"));
                        nichos_e.setCol132(rs.getString("col132"));
                        nichos_e.setCol133(rs.getString("col133"));
                        nichos_e.setCol134(rs.getString("col134"));
                        nichos_e.setCol135(rs.getString("col135"));
                        nichos_e.setCol136(rs.getString("col136"));
                        nichos_e.setCol137(rs.getString("col137"));
                        nichos_e.setCol138(rs.getString("col138"));
                        nichos_e.setCol139(rs.getString("col139"));
                        nichos_e.setCol140(rs.getString("col140"));
                        nichos_e.setCol141(rs.getString("col141"));
                        nichos_e.setCol142(rs.getString("col142"));
                        nichos_e.setCol143(rs.getString("col143"));
                        nichos_e.setCol144(rs.getString("col144"));
                        nichos_e.setCol145(rs.getString("col145"));
                        nichos_e.setCol146(rs.getString("col146"));
                        nichos_e.setCol147(rs.getString("col147"));
                        nichos_e.setCol148(rs.getString("col148"));
                        nichos_e.setCol149(rs.getString("col149"));
                        nichos_e.setCol150(rs.getString("col150"));
                        nichos_e.setCol151(rs.getString("col151"));
                        nichos_e.setCol152(rs.getString("col152"));
                        nichos_e.setCol153(rs.getString("col153"));
                        nichos_e.setCol154(rs.getString("col154"));
                        nichos_e.setCol155(rs.getString("col155"));
                        nichos_e.setCol156(rs.getString("col156"));
                        nichos_e.setCol157(rs.getString("col157"));
                        nichos_e.setCol158(rs.getString("col158"));
                        nichos_e.setCol159(rs.getString("col159"));
                        nichos_e.setCol160(rs.getString("col160"));
                        nichos_e.setCol161(rs.getString("col161"));
                        nichos_e.setCol162(rs.getString("col162"));
                        nichos_e.setCol163(rs.getString("col163"));
                        nichos_e.setCol164(rs.getString("col164"));
                        nichos_e.setCol165(rs.getString("col165"));
                        nichos_e.setCol166(rs.getString("col166"));
                        nichos_e.setCol167(rs.getString("col167"));
                        nichos_e.setCol168(rs.getString("col168"));
                        nichos_e.setCol169(rs.getString("col169"));
                        nichos_e.setCol170(rs.getString("col170"));
                        nichos_e.setCol171(rs.getString("col171"));
                        nichos_e.setCol172(rs.getString("col172"));
                        nichos_e.setCol173(rs.getString("col173"));
                        nichos_e.setCol174(rs.getString("col174"));
                        nichos_e.setCol175(rs.getString("col175"));
                        nichos_e.setCol176(rs.getString("col176"));
                        nichos_e.setCol177(rs.getString("col177"));
                        nichos_e.setCol178(rs.getString("col178"));
                        nichos_e.setCol179(rs.getString("col179"));
                        nichos_e.setCol180(rs.getString("col180"));
                        nichos_e.setCol181(rs.getString("col181"));
                        nichos_e.setCol182(rs.getString("col182"));
                        nichos_e.setCol183(rs.getString("col183"));
                        nichos_e.setCol184(rs.getString("col184"));
                        nichos_e.setCol185(rs.getString("col185"));
                        nichos_e.setCol186(rs.getString("col186"));
                        nichos_e.setCol187(rs.getString("col187"));
                        nichos_e.setCol188(rs.getString("col188"));
                        nichos_e.setCol189(rs.getString("col189"));
                        nichos_e.setCol190(rs.getString("col190"));
                        nichos_e.setCol191(rs.getString("col191"));
                        nichos_e.setCol192(rs.getString("col192"));
                        nichos_e.setCol193(rs.getString("col193"));
                        nichos_e.setCol194(rs.getString("col194"));
                        nichos_e.setCol195(rs.getString("col195"));
                        nichos_e.setCol196(rs.getString("col196"));
                        nichos_e.setCol197(rs.getString("col197"));
                        nichos_e.setCol198(rs.getString("col198"));
                        nichos_e.setCol199(rs.getString("col199"));
                        nichos_e.setCol200(rs.getString("col200"));
                        return nichos_e;
                        
                    }
        }, new Object[] {});
        return matches.size() > 0? matches: null;
    }

    @Override
    public int eliminaNichoe(int codcem, int codcuar, int fila1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
