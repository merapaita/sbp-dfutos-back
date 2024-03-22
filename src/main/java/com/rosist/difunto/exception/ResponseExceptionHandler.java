package com.rosist.difunto.exception;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler  {

	private static final Logger logger = LoggerFactory.getLogger(ResponseExceptionHandler.class);
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<ExceptionResponse> manejarTodasExcepciones(Exception ex, WebRequest request){
		ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));		
//logger.info("mensajeTodas lasExecpciones" + ex.getMessage());
		return new ResponseEntity<>(er, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
//	@Override
//	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
//			HttpHeaders headers, HttpStatus status, WebRequest request) {
////		logger.info("mensajeTodas handleMEtod");
//		
//		String mensaje = ex.getBindingResult().getAllErrors().stream().map(e -> {
//			return e.getDefaultMessage().concat(", ");
//		}).collect(Collectors.joining());
//
//		ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), mensaje, request.getDescription(false));
//		return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
//
//	}

	@ExceptionHandler(ModelNotFoundException.class)
	public ResponseEntity<ExceptionResponse> manejarModelNotFoundException(ModelNotFoundException ex, WebRequest request) {
		ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(), request.getDescription(false));
//		logger.info("mensajeTodas modelNoFound");
		return new ResponseEntity<>(er, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> accesDeniedException(AccessDeniedException ex,
			WebRequest request) {
		ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), ex.getMessage(),
				request.getDescription(false));
		return new ResponseEntity<>(er, HttpStatus.NOT_FOUND);
	}
	
}
