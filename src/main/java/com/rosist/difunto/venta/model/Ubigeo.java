package com.rosist.difunto.venta.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "ubigeo", uniqueConstraints=@UniqueConstraint(columnNames={"codDepartamento","codProvincia","codDistrito"}))
public class Ubigeo {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idUbigeo;
	
	@Column(length = 2, nullable = false)
	private String codDepartamento;
	
	@Column(length = 30, nullable = false)
	private String nomDepartamento;
	
	@Column(length = 2, nullable = false)
	private String codProvincia;
	
	@Column(length = 30, nullable = false)
	private String nomProvincia;
	
	@Column(length = 2, nullable = false)
	private String codDistrito;
	
	@Column(length = 30, nullable = false)
	private String nomDistrito;
	
}
