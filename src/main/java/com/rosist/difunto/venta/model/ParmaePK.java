package com.rosist.difunto.venta.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Embeddable
public class ParmaePK {

	@Column(name = "tipo", length = 6, nullable = true)
	private String tipo;

	@Column(name = "codigo", length = 6, nullable = true)
	private String codigo;
	
	@Column(name = "codigoaux", length = 6, nullable = true)
	private String codigoaux;
	
}
