package com.rosist.difunto.venta.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "credito")
public class Credito {

	@EqualsAndHashCode.Include
	@Id
	@Column(length = 9)
	private String codcre;
	
	@Column(name = "feccre", nullable = false)
	private LocalDate feccre;
	
	@JsonIgnore
	@OneToOne(mappedBy = "credito")
    private Venta venta;

	@OneToMany(mappedBy = "credito", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<Pagacta> pagacta;
	
	@OneToMany(mappedBy = "credito", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<Ajuste> ajuste;
	
	@OneToMany(mappedBy = "credito", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<Karcre> karcre;
	
	@Column(nullable = false)
	private Double mtocre;
	
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
