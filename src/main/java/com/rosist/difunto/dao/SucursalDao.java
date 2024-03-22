package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Sucursal;
import java.util.List;

public interface SucursalDao {
    public int getSucursalCount();
    public int getSucursalCount(String condicion);
    public int getNewIdSucursal();
    public int insertaSucursal(Sucursal sucursal);
    public int modificaSucursal(Sucursal sucursal);
    public int eliminaSucursal(int tiping);
    public Sucursal buscaSucursal(int tiping);
    public List<Sucursal> listaSucursal(String condicion);
}