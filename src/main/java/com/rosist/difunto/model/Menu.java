package com.rosist.difunto.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@Table(name = "menu", uniqueConstraints=@UniqueConstraint(columnNames={"item"}))
public class Menu {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idMenu;

	@Column(name = "icono", length = 20)
	private String icono;

	@Column(name = "nombre", length = 30)
	private String nombre;

	@Column(name = "url", length = 50)
	private String url;

	@Column(name = "item", length = 10)
	private Integer item;

    @OneToMany(mappedBy = "menu")
    private Set<MenuRol> menuRol = new HashSet<>();

}
