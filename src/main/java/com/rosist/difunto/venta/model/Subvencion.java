package com.rosist.difunto.venta.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "subvencion")
public class Subvencion {

	@EqualsAndHashCode.Include
	@Id
	private String codsub;
	
	@Column(name = "fecsub", nullable = false)
	private LocalDate fecsub;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "codvta", nullable = true, foreignKey = @ForeignKey(name = "FK_venta"))
    private Venta venta;
	
	@Column(name = "codent", nullable = false, length = 3)
	private String codent;
	
	@Column(name = "docref", nullable = false, length = 3)
	private String docref;
	
	@Column(name = "numref", nullable = false, length = 15)
	private String numref;
	
	@Column(name = "fecref", nullable = false)
	private LocalDate fecref;
	
	@Column(name = "expediente", nullable = false, length = 15)
	private String expediente;
	
	@Column(nullable = false)
	private Double mtosub;
	
	@Column(nullable = false, length = 2)
	private String estado;
	
	private boolean pagado;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "codps", nullable = true, foreignKey = @ForeignKey(name = "FK_pagsub"))
    private Pagsub pagsub;
	
	@Column(name = "userup", length = 15, nullable = false)
	private String userup;
	
	@Column(name = "usercr", length = 15, nullable = true)
	private String usercr;
	
	@Column(name = "duserup", nullable = false)
	private LocalDateTime duserup;
	
	@Column(name = "dusercr", nullable = true)
	private LocalDateTime dusercr;
	
}
