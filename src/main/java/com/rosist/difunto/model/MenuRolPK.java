package com.rosist.difunto.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Embeddable
public class MenuRolPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name = "id_menu", nullable = false)
	private Menu menu;
	
	@ManyToOne
	@JoinColumn(name = "id_rol", nullable = false)
	private Rol rol;

}