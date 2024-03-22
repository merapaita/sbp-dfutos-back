package com.rosist.difunto.dao;

import com.rosist.difunto.modelSbp.Cliente;
import java.util.List;

public interface ClienteDao {
    public int getClienteCount();
    public int getClienteCount(String condicion);
    public int getNewIdCliente();
    public Cliente insertaCliente(Cliente cliente);
    public Cliente modificaCliente(Cliente cliente);
    public String eliminaCliente(String tipdoccli, String doccli);
    public Cliente buscaCliente(String tipdoccli, String doccli);
    public List<Cliente> listaClientes(String condicion, String limit, String order);
}