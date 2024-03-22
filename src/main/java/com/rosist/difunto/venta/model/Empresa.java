package com.rosist.difunto.venta.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "empresa")
public class Empresa {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idEmpresa;
	
	@Column(length = 11, nullable = false)
	private String ruc;
	
	@Column(length = 50, nullable = false)
	private String razonSocial;
	
	@Column(length = 50, nullable = false)
	private String nombreComercial;
	
	@Column(length = 50, nullable = false)
	private String direcion;

	@ManyToOne
	@JoinColumn(name = "id_ubigeo", nullable = false, foreignKey = @ForeignKey(name = "FK_ubigeo"))
	private Ubigeo ubigeo;
	
}
