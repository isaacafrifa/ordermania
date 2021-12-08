package com.iam.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(ProductNotFound.class)
	public ResponseEntity<APIError> handleUserNotFoundException(ProductNotFound ex, WebRequest request) {
		APIError errorDetails = new APIError(HttpStatus.NOT_FOUND.value(), ex.getMessage(), ex.getLocalizedMessage(),
				request.getDescription(false) + "", LocalDateTime.now());
		return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
	}

	
	@ExceptionHandler(ProductAlreadyExists.class)
	public ResponseEntity<APIError> handleUserAlreadyExistsException(ProductAlreadyExists ex, WebRequest request) {
		APIError errorDetails = new APIError(HttpStatus.CONFLICT.value(), ex.getMessage(), ex.getLocalizedMessage(),
				request.getDescription(false) + "",LocalDateTime.now());
		return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
	}
	
	
	/*
	 * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
	 */
	 @Override
	    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
	    		HttpHeaders headers, HttpStatus status, WebRequest request) {
	       // ServletWebRequest servletWebRequest = (ServletWebRequest) request;
	       // log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
	        String error = "Malformed JSON request";
	        APIError apiError = new APIError(HttpStatus.BAD_REQUEST.value(), 
	      	      ex.getLocalizedMessage(), error,
	      	      request.getDescription(false) + "", LocalDateTime.now());
	        return new ResponseEntity<Object>(apiError,HttpStatus.BAD_REQUEST);
	    }
}
