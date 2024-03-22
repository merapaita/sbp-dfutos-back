package com.rosist.difunto.dao;

import java.util.List;

import com.rosist.difunto.modelSbp.Empresa;

public interface EmpresaDao {
    public int getEmpresaCount();
    public int getEmpresaCount(String condicion);
    public int getNewIdEmpresa();
    public int insertaEmpresa(Empresa empresa);
    public int modificaEmpresa(Empresa empresa);
    public String eliminaEmpresa(String ruc);
    public Empresa buscaEmpresa(String ruc);
    public List<Empresa> listaEmpresa(String condicion);
}
