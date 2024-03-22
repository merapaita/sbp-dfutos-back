package com.rosist.difunto.venta.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rosist.difunto.service.impl.CRUDImpl;
import com.rosist.difunto.venta.model.Cliente;
import com.rosist.difunto.venta.repo.IClienteRepo;
import com.rosist.difunto.venta.repo.IGenericRepo;
import com.rosist.difunto.venta.service.IClienteService;

@Service
public class ClienteServiceImpl extends CRUDImpl<Cliente, Integer> implements IClienteService {

	@Autowired
	private IClienteRepo repo;
	
	@Override
	protected IGenericRepo<Cliente, Integer> getRepo() {
		return repo;
	}
	
	private static final Logger log = LoggerFactory.getLogger(ClienteServiceImpl.class);

	@Override
	public List<Cliente> listarCliente(Integer codcli, String tipdoccli, String doccli, String nomcli, Integer page, Integer size) throws Exception {
		List<Cliente> clientes = new ArrayList<>();
		List<Object[]> registros = repo.listarCliente(codcli, tipdoccli, doccli, nomcli, page, size);

		registros.forEach(reg -> {
			
			Cliente cliente = new Cliente();
			cliente.setCodcli(Integer.parseInt(String.valueOf(reg[0])));
			cliente.setTipdoccli(String.valueOf(reg[1]));
			cliente.setDoccli(String.valueOf(reg[2]));
			cliente.setNomcli(String.valueOf(reg[4]));
			cliente.setDircli(String.valueOf(reg[5]));
//			venta.setDesEstado(lEstados.stream()
//					.filter(xx -> xx.getCodigo().equals(String.valueOf(reg[13])))
//					.collect(Collectors.toList()).get(0).getDescri());

			clientes.add(cliente);
		});
		return clientes;
	}
	
	@Override
	public Cliente registraTransaccion(Cliente cliente) throws SQLException, Exception {
		return repo.save(cliente);
	}

	@Override
	public Cliente modificaTransaccion(Cliente cliente) throws Exception {
		return repo.save(cliente);
	}

}
