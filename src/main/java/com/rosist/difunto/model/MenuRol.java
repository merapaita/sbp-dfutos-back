package com.rosist.difunto.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "menu_rol")
public class MenuRol {

    @EmbeddedId
    private MenuRolPK id;

    @ManyToOne
    @MapsId("Idmenu")
    @JoinColumn(name = "id_menu")
    private Menu menu;

    @ManyToOne
    @MapsId("idRol")
    @JoinColumn(name = "id_rol")
    private Rol rol;

	@Column(name = "nuevo")
	private Boolean nuevo;
	
	@Column(name = "modifica")
	private Boolean modifica;

}
