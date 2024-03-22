package com.rosist.difunto.venta.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "karcre", uniqueConstraints=@UniqueConstraint(columnNames={"codcre", "correl"}))
public class Karcre {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idKarcre;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "codcre", nullable = false, foreignKey = @ForeignKey(name = "FK_credito"))
	private Credito credito;
	
	@Column(name = "correl", nullable = false, length = 4)
	private Integer correl;

	@Column(name = "estado", nullable = false, length = 2)
	private String estado;
	
	@Column(name = "fecha", nullable = false)
	private LocalDate fecha;
	
	@Column(name = "tipmov", nullable = false, length = 3)
	private String tipmov;
	
	@Column(name = "codmov", nullable = false, length = 9)
	private String codmov;
	
	@Column(name = "mtocre", nullable = false)
	private Double mtocre;
	
	@Column(name = "mtoamo", nullable = false)
	private Double mtoamo;
	
	@Transient
	private Double totamo;
	
	@Column(name = "mtoint", nullable = false)
	private Double mtoint;
	
	@Transient
	private Double totint;
	
	@Column(name = "mtoaju", nullable = false)
	private Double mtoaju;
	
	@Transient
	private Double totaju;
	
	@Transient
	private Double saldo;
	
	@Column(name = "userup", length = 15, nullable = true)
	private String userup;

	@Column(name = "usercr", length = 15, nullable = true)
	private String usercr;
	
	@Column(name = "duserup", nullable = true)
	private LocalDateTime duserup;

	@Column(name = "dusercr", nullable = true)
	private LocalDateTime dusercr;
	
}