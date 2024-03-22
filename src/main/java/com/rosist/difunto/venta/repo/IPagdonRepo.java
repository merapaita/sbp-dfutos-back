package com.rosist.difunto.venta.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rosist.difunto.venta.model.Pagdon;

public interface IPagdonRepo extends IGenericRepo<Pagdon, String> {

	@Query(value = "call pr_idPagdon(:anno)", nativeQuery = true)
	String idPagdon(
			@Param("anno") String anno
			);
	
	@Query(value = "call pr_pagdon_count(:anno, :mes) ", nativeQuery = true)
	Integer countPagdon(
			@Param("anno") Integer anno, 
			@Param("mes") Integer mes
			);
	
	@Query(value = "call pr_pagdon(:anno, :mes, :page, :size) ", nativeQuery = true)
	List<Object[]> listarPagdon(
			@Param("anno") Integer anno, 
			@Param("mes") Integer mes, 
			@Param("page") Integer page, @Param("size") Integer size );
	
}
