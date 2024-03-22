package com.rosist.difunto.modelSbp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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
@Entity
@Table(name = "cementerio")
public class Cementerio {

	@EqualsAndHashCode.Include
	@Id
	@Column(length = 9)
    private Integer codcem;
	
	@Column(name = "nomcem", nullable = true, length = 60)
    private String nomcem;
	
	@Column(name = "local", nullable = false)
    private boolean  local;
    
}
