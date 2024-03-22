package com.rosist.difunto.venta.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "venta")
public class Venta {

	@EqualsAndHashCode.Include
	@Id
	@Column(length = 9)
	private String codvta;
	
//	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Column(name = "fecvta", nullable = false)
	private LocalDate fecvta;
	
	@Column(name = "tipvta", nullable = true, length = 2)
	private String tipvta;
	
	@Column(name = "nvoTipvta", nullable = true, length = 2)
	private String nvotipvta;
	
	@Column(name = "convta", nullable = true, length = 2)
	private String convta;
	
	@Column(name = "compag", nullable = false, length = 2)
	private String compag;
	
	@Column(name = "serie", nullable = false, length = 4)
	private String serie;
	
	@Column(name = "codcp", nullable = false, length = 15)
	private String codcp;
	
	@Column(nullable = false)
	private LocalDate feccp;
	
	@ManyToOne
	@JoinColumn(name = "codcli", nullable = false, foreignKey = @ForeignKey(name = "FK_cliente_venta"))
	private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name = "codcli_otro", nullable = true, foreignKey = @ForeignKey(name = "FK_cliente_venta1"))
	private Cliente clienteOtro;
	
	@Column(nullable = false)
	private boolean bcredito;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinTable(name = "venta_credito", 
	           joinColumns = { @JoinColumn(name = "codvta", referencedColumnName = "codvta") },
	           inverseJoinColumns = { @JoinColumn(name = "codcre", referencedColumnName = "codcre") })	
	private Credito credito;
	
	@ManyToOne
	@JoinColumn(name = "codavl", nullable = true, foreignKey = @ForeignKey(name = "FK_cliente_aval"))
	private Cliente aval;

	@Column(nullable = false)
	private boolean bsubvencion;
	
	@OneToMany(mappedBy = "venta", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<Subvencion> subvenciones;

	@Column(nullable = false)
	private boolean bdonacion;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinTable(name = "venta_donacion", 
	           joinColumns = { @JoinColumn(name = "codvta", referencedColumnName = "codvta") },
	           inverseJoinColumns = { @JoinColumn(name = "coddon", referencedColumnName = "coddon") })	
	private Donacion donacion;
	
	@Column(nullable = false)
	private Double mtovta;
	
	@Column(nullable = false)
	private Double mtodon;
	
	@Column(nullable = false)
	private Double mtosub;
	
	@Column(nullable = false)
	private Double mtocan;
	
	@Column(nullable = false)
	private Double mtocre;
	
	@Column(nullable = false, length = 100)
	private String observ;
	
	@Column(nullable = false, length = 2)
	private String estado;

	@Transient
	private String desEstado;
	
	@Column(nullable = true, length = 2)
	private String codcem;
	
	@Column(nullable = true, length = 70)
	private String nomben;
	
	@Column(nullable = true, length = 40)
	private String cuartel;
	
	@Column(nullable = true, length = 5)
	private String nicho;
	
	@Column(nullable = false, length = 5)
	private boolean bcambioNombre;
	
	@Column(name = "userup", length = 15, nullable = false)
	private String userup;
	
	@Column(name = "usercr", length = 15, nullable = true)
	private String usercr;
	
	@Column(name = "duserup", nullable = false)
	private LocalDateTime duserup;
	
	@Column(name = "dusercr", nullable = true)
	private LocalDateTime dusercr;

}
