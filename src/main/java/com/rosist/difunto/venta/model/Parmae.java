package com.rosist.difunto.venta.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "parmae")
public class Parmae {

	@EmbeddedId
    private ParmaePK id;
	
//	@Column(name = "tipo", length = 6, nullable = true)
//	private String tipo;
//
//	@Column(name = "codigo", length = 6, nullable = true)
//	private String codigo;
//	
//	@Column(name = "codigoaux", length = 6, nullable = true)
//	private String codigoaux;
	
	@Column(name = "descri", length = 100, nullable = true)
	private String descri;
	
}
