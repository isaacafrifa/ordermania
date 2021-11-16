package com.iam.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ProductIdAlreadyExists extends RuntimeException{
	/** Default Serial Version UID*/
	private static final long serialVersionUID = 1L;

	//constructor
	public ProductIdAlreadyExists() {
	    super("Product ID already exists.");
	  }
}