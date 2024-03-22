package com.rosist.difunto.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponse {

	// @NotNull
	private LocalDateTime fecha;
	// @NotNull
	private String mensaje;
	// @NotNull
	private String detalles;

}
