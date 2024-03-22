package com.rosist.difunto.venta.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rosist.difunto.venta.model.Pagsub;

public interface IPagsubRepo extends IGenericRepo<Pagsub, String> {

	@Query(value = "call pr_idPagsub(:anno)", nativeQuery = true)
	String idPagsub(
			@Param("anno") String anno
			);
	
	@Query(value = "call pr_pagsub_count(:anno, :mes) ", nativeQuery = true)
	Integer countPagsub(
			@Param("anno") Integer anno, 
			@Param("mes") Integer mes
			);
	
	@Query(value = "call pr_pagsub(:anno, :mes, :page, :size) ", nativeQuery = true)
	List<Object[]> listarPagsub(
			@Param("anno") Integer anno, 
			@Param("mes") Integer mes, 
			@Param("page") Integer page, @Param("size") Integer size );
	
}
