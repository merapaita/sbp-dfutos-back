package com.rosist.difunto.venta.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cliente_venta", uniqueConstraints = @UniqueConstraint(columnNames={"tipdoccli","doccli"}))
public class Cliente {

	@EqualsAndHashCode.Include
	@Id
//	@GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer codcli;
    
	@Column(name = "tipdoccli", nullable = true, length = 1)
    private String tipdoccli;
    
	@Column(name = "doccli", nullable = true, length = 15)
    private String doccli;
    
	@Column(name = "nomcli", nullable = true, length = 70)
    private String nomcli;
    
	@Column(name = "dircli", nullable = true, length = 70)
    private String dircli;
    
	@Transient
	private String desTipdoccli;
	
}
