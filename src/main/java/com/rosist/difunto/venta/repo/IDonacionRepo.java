package com.rosist.difunto.venta.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rosist.difunto.venta.model.Donacion;
import com.rosist.difunto.venta.model.Subvencion;

public interface IDonacionRepo extends IGenericRepo<Donacion, String> {

	@Query(value = "call pr_idDonacion(:anno, :mes)", nativeQuery = true)
	String idDonacion(
			@Param("anno") String anno, 
			@Param("mes") String mes
			);
	
	@Query(value = "call pr_donacion_count(:codvta, :cliente, :estado)", nativeQuery = true)
	Integer listaDonacionesCount(
			@Param("codvta") String codvta, 
			@Param("cliente") String cliente,
			@Param("estado") String estado
			);
	
	@Query(value = "call pr_donacion(:codvta, :cliente, :estado, :page, :size)", nativeQuery = true)
	List<Object[]> listaDonaciones(
			@Param("codvta") String codvta, 
			@Param("cliente") String cliente,
			@Param("estado") String estado,
			@Param("page") Integer page,
			@Param("size") Integer size
			);
	
}
