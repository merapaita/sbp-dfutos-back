package com.rosist.difunto.venta.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rosist.difunto.venta.model.Subvencion;

public interface ISubvencionRepo extends IGenericRepo<Subvencion, String> {

	@Query(value = "call pr_idSubvencion(:anno, :mes)", nativeQuery = true)
	String idSubvencion(
			@Param("anno") String anno, 
			@Param("mes") String mes
			);
	
	@Query(value = "call pr_subvencion_count(:codvta, :cliente, :estado)", nativeQuery = true)
	Integer listaSubvencionesCount(
			@Param("codvta") String codvta, 
			@Param("cliente") String cliente,
			@Param("estado") String estado
			);
	
	@Query(value = "call pr_subvencion(:codvta, :cliente, :estado, :page, :size)", nativeQuery = true)
	List<Object[]> listaSubvenciones(
			@Param("codvta") String codvta, 
			@Param("cliente") String cliente,
			@Param("estado") String estado,
			@Param("page") Integer page,
			@Param("size") Integer size
			);

}
