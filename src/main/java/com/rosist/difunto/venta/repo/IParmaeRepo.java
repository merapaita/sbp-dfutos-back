package com.rosist.difunto.venta.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rosist.difunto.venta.model.Parmae;
import com.rosist.difunto.venta.model.ParmaePK;

public interface IParmaeRepo extends JpaRepository<Parmae, ParmaePK> {

	@Query(value = "call pr_parmae_count(:tipo, :codigo, :codigoaux) ", nativeQuery = true)
	Integer countParmae(
			@Param("tipo") String tipo, 
			@Param("codigo") String codigo, 
			@Param("codigoaux") String codigoaux
			);
	
	@Query(value = "call pr_parmae(:tipo, :codigo, :codigoaux, :page, :size) ", nativeQuery = true)
	List<Object[]> listarParmae(
			@Param("tipo") String tipo, 
			@Param("codigo") String codigo, 
			@Param("codigoaux") String codigoaux, 
			@Param("page") Integer page, @Param("size") Integer size );
	
}