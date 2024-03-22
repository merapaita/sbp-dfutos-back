package com.rosist.difunto.venta.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.rosist.difunto.venta.model.Cliente;
import com.rosist.difunto.venta.model.Credito;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AjusteDto {
	
	private String codaju;
	private LocalDate fecaju;
	private Credito credito;
	private String compag;
	private String serie;
	private String codcp;
	private LocalDate feccp;
	private Cliente cliente;
	private Double mtoaju;
	private String observ;
	private String estado;
	private String userup;
	private String usercr;
	private LocalDateTime duserup;
	private LocalDateTime dusercr;

}