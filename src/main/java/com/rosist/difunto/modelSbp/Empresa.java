package com.rosist.difunto.modelSbp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Empresa {
	private String ruc; 
	private String razsoc; 
	private String nomcom; 
	private Ubigeo ubigeo; 
	private String direc; 
	private String urban; 
	private String ususol; 
	private String clasol; 
	private String ruta; 
	private String cert; 
	private String clacert;
}