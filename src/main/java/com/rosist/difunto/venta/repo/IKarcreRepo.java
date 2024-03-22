package com.rosist.difunto.venta.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.rosist.difunto.venta.model.Karcre;

public interface IKarcreRepo extends IGenericRepo<Karcre, Integer> {

	@Query(value = "select ifnull(max(correl),0)+1 from karcre where codcre=:codcre", nativeQuery = true)
	Integer getNewCorrel(@Param("codcre") String codcre);

	@Query(value = "select * "
			+ "       from karcre k "
			+ "      where k.tipmov=:tipmov and k.codmov=:codmov ", nativeQuery = true)
	Karcre buscaPorItem(@Param("tipmov") String tipmov, @Param("codmov") String codmov);
	
	@Query(value = "CALL pr_karcre_count(:codcre, :tipmov, :codmov)", nativeQuery = true)
	Integer listaKarcre_count(
			@Param("codcre") String codcre,
			@Param("tipmov") String tipmov,
			@Param("codmov") String codmov
			);
	
	@Query(value = "CALL pr_karcre(:codcre, :tipmov, :codmov, :page, :size)", nativeQuery = true)
	List<Object[]> listaKarcre(
			@Param("codcre") String codcre,
			@Param("tipmov") String tipmov, 
			@Param("codmov") String codmov, 
			@Param("page") Integer page, @Param("size") Integer size);
	
}