package com.rosist.difunto.venta.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rosist.difunto.venta.model.Ajuste;

public interface IAjusteRepo extends IGenericRepo<Ajuste, String> {

	@Query(value = "call pr_idAjuste(:anno, :mes)", nativeQuery = true)
	String idAjuste(
			@Param("anno") String anno, 
			@Param("mes") String mes
			);
	
	@Query(value = "call pr_ajuste_count(:anno, :mes, :cliente, :estado) ", nativeQuery = true)
	Integer countAjuste(
			@Param("anno") Integer anno, 
			@Param("mes") Integer mes, 
			@Param("cliente") String cliente, 
			@Param("estado") String estado
			);

	@Query(value = "call pr_ajuste(:anno, :mes, :_cliente, :estado, :page, :size) ", nativeQuery = true)
	List<Object[]> listarAjuste(
			@Param("anno") Integer anno, 
			@Param("mes") Integer mes, 
			@Param("_cliente") String _cliente, 
			@Param("estado") String estado,
			@Param("page") Integer page, @Param("size") Integer size );

}