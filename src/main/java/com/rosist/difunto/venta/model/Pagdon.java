package com.rosist.difunto.venta.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "pagdon")
public class Pagdon {

	@EqualsAndHashCode.Include
	@Id
	@Column(length = 6)
	private String codpd;
	
	private LocalDate fecpd;
	
	@Column(nullable = false, length = 2)
	private String codent;
	
	@Transient
	private String desCodent;
	
	@Column(nullable = false, length = 15)
	private String codcp;
	
	@Column(nullable = false, length = 15)
	private String cheque;
	
	@Column(nullable = false)
	private Double mtotot;
	
	@Column(nullable = false)
	private String observ;
	
	@OneToMany(mappedBy = "pagdon", cascade = { CascadeType.ALL }, orphanRemoval = true)
	List<Donacion> donaciones;
	
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
