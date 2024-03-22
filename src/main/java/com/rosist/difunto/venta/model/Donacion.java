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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "donacion")
public class Donacion {

	@EqualsAndHashCode.Include
	@Id
	@Column(length = 9)
	private String coddon;
	
	@Column(name = "fecdon", nullable = false)
	private LocalDate fecdon;
	
	@JsonIgnore
	@OneToOne(mappedBy = "donacion")
//	@JoinColumn(name = "codvta", nullable = true, foreignKey = @ForeignKey(name = "FK_venta_donacion"))
    private Venta venta;
	
	@Column(name = "docref", nullable = true, length = 3)
	private String docref;
	
	@Column(name = "numref", nullable = true, length = 15)
	private String numref;
	
	@Column(name = "fecref", nullable = true)
	private LocalDate fecref;
	
	@Column(name = "expediente", nullable = true, length = 15)
	private String expediente;
	
	@Column(nullable = false)
	private Double mtodon;
	
	@Column(nullable = false, length = 2)
	private String estado;
	
	private boolean pagado;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "codpd", nullable = true, foreignKey = @ForeignKey(name = "FK_pagdon"))
    private Pagdon pagdon;
	
	@Column(name = "userup", length = 15, nullable = false)
	private String userup;
	
	@Column(name = "usercr", length = 15, nullable = true)
	private String usercr;
	
	@Column(name = "duserup", nullable = false)
	private LocalDateTime duserup;
	
	@Column(name = "dusercr", nullable = true)
	private LocalDateTime dusercr;
	
}
