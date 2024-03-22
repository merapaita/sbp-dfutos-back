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
@Table(name = "sucursal")
public class Sucursal {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idSucursal;
	
	@Column(length = 50, nullable = false)
    private String descri;
	
	@Column(length = 70, nullable = false)
    private String direccion;
	
	@Column(length = 20, nullable = false)
    private String telefono;
	
	@Column(length = 30, nullable = false)
    private String email;
	
	@ManyToOne
	@JoinColumn(name = "id_empresa", nullable = false, foreignKey = @ForeignKey(name = "FK_empresa"))
	private Empresa empresa;
	
	@ManyToOne
	@JoinColumn(name = "id_ubigeo", nullable = false, foreignKey = @ForeignKey(name = "FK_ubigeo2"))
	private Ubigeo ubigeo;
	
}
