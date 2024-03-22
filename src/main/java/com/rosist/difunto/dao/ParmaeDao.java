package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Parmae;
import java.util.List;

/**
 *
 * @author Administrador
 */
public interface ParmaeDao {
    public int getParmaeCount();
    public int getParmaeCount(String condicion);
    public Parmae insertaParmae(Parmae parmae);
    public Parmae modificaParmae(Parmae parmae);
    public String eliminaParmae(String tipo, String codigo, String codaux);
    public List<Parmae> listaParmae(String condicion, String limit, String order);
    public Parmae buscaParmae(String tipo, String codigo, String codaux);
}
