package com.rosist.difunto.venta.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rosist.difunto.venta.model.Cliente;

public interface IClienteRepo extends IGenericRepo<Cliente, Integer> {

	@Query(value = "call pr_cliente_count(:codcli, :tipdoccli, :doccli, :nomcli) ", nativeQuery = true)
	Integer countCliente(@Param("codcli") Integer codcli, @Param("tipdoccli") String tipdoccli,
			@Param("doccli") String doccli, @Param("nomcli") String nomcli);

	@Query(value = "call pr_cliente(:codcli, :tipdoccli, :doccli, :nomcli, :page, :size) ", nativeQuery = true)
	List<Object[]> listarCliente(@Param("codcli") Integer codcli, @Param("tipdoccli") String tipdoccli,
			@Param("doccli") String doccli, @Param("nomcli") String nomcli, @Param("page") Integer page,
			@Param("size") Integer size);

	@Query(value = "select * from cliente_venta where tipdoccli=:tipdoccli and doccli=:doccli ", nativeQuery = true)
	Cliente buscaProTipo(@Param("tipdoccli") String tipdoccli, @Param("doccli") String doccli);

	@Query(value = "SELECT IFNULL(MAX(codcli),0) + 1 FROM cliente_venta ", nativeQuery = true)
	Integer getNewId();

}
