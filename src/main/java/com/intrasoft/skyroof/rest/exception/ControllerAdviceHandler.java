package com.intrasoft.skyroof.rest.exception;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ControllerAdviceHandler extends ResponseEntityExceptionHandler {

	  @ExceptionHandler(ResponseStatusException.class)
	    public ResponseEntity handleException(ResponseStatusException ex) {
	        return ResponseEntity.status(ex.getStatus())//
	                .contentType(MediaType.APPLICATION_JSON)//
	                .body(new ExceptionMessage(ex));
	    }
}