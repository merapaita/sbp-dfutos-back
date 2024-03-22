package com.rosist.difunto.venta.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "pagacta")
public class Pagacta {
	
	@EqualsAndHashCode.Include
	@Id
	@Column(length = 9)
	private String codpac;
	
	private LocalDate fecpac;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "codcre", nullable = false, foreignKey = @ForeignKey(name = "FK_pagacta_credito"))
	private Credito credito;
	
	@Column(name = "compag", nullable = false, length = 2)
	private String compag;
	
	@Column(name = "serie", nullable = false, length = 4)
	private String serie;
	
	@Column(name = "codcp", nullable = false, length = 10)
	private String codcp;
	
	private LocalDate feccp;
	
	@ManyToOne
	@JoinColumn(name = "codcli", nullable = false, foreignKey = @ForeignKey(name = "FK_cliente_pagacta"))
	private Cliente cliente;
	
	@Column(nullable = false)
	private Double interes;
	
	@Column(nullable = false)
	private Double mtoamo;
	
	@Column(nullable = false)
	private Double mtoaju;
	
	@Column(nullable = true)
	private String observ;
	
	@Column(nullable = false, length = 2)
	private String estado;
	
	@Column(name = "userup", length = 15, nullable = false)
	private String userup;
	
	@Column(name = "usercr", length = 15, nullable = true)
	private String usercr;
	
	@Column(name = "duserup", nullable = false)
	private LocalDateTime duserup;
	
	@Column(name = "dusercr", nullable = true)
	private LocalDateTime dusercr;

}
