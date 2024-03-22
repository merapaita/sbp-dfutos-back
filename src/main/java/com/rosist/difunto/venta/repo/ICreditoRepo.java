package com.rosist.difunto.venta.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rosist.difunto.venta.model.Credito;

public interface ICreditoRepo extends IGenericRepo<Credito, String>  {

	@Query(value = "call pr_idCredito(:anno, :mes)", nativeQuery = true)
	String idCredito(
			@Param("anno") String anno, 
			@Param("mes") String mes
			);
	
	@Query(value = "call pr_credito_count(:anno, :mes, :cliente, :estado) ", nativeQuery = true)
	Integer countCredito(
			@Param("anno") Integer anno, 
			@Param("mes") Integer mes, 
			@Param("cliente") String cliente, 
			@Param("estado") String estado
			);
	
	@Query(value = "call pr_credito(:anno, :mes, :_cliente, :estado, :page, :size) ", nativeQuery = true)
	List<Object[]> listarCredito(
			@Param("anno") Integer anno, 
			@Param("mes") Integer mes, 
			@Param("_cliente") String _cliente, 
			@Param("estado") String estado,
			@Param("page") Integer page, @Param("size") Integer size );
	
	@Query(value = "call pr_credito_control(:anno, :mes, :_cliente, :estado, :page, :size) ", nativeQuery = true)
	List<Object[]> listarCreditoControl(
			@Param("anno") Integer anno, 
			@Param("mes") Integer mes, 
			@Param("_cliente") String _cliente, 
			@Param("estado") String estado,
			@Param("page") Integer page, @Param("size") Integer size );
	
	@Query(value = "call pr_credito_control_resumen(:codcre) ", nativeQuery = true)
	List<Object[]> listarCreditoControlResumen(
			@Param("codcre") String codcre);
	
}