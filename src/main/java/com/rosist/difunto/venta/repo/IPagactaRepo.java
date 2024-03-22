package com.rosist.difunto.venta.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rosist.difunto.venta.model.Pagacta;

public interface IPagactaRepo extends IGenericRepo<Pagacta, String> {

	@Query(value = "call pr_idPagacta(:anno, :mes)", nativeQuery = true)
	String idPagacta(
			@Param("anno") String anno, 
			@Param("mes") String mes
			);
	
	@Query(value = "call pr_pagacta_count(:anno, :mes, :cliente, :estado) ", nativeQuery = true)
	Integer countVenta(
			@Param("anno") Integer anno, 
			@Param("mes") Integer mes, 
			@Param("cliente") String cliente, 
			@Param("estado") String estado
			);
	
	@Query(value = "call pr_pagacta(:anno, :mes, :_cliente, :estado, :page, :size) ", nativeQuery = true)
	List<Object[]> listarVenta(
			@Param("anno") Integer anno, 
			@Param("mes") Integer mes, 
			@Param("_cliente") String _cliente, 
			@Param("estado") String estado,
			@Param("page") Integer page, @Param("size") Integer size );
	
}