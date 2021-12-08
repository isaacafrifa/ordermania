package com.iam.exception;

public class ProductNotFound extends RuntimeException{
	/** Default Serial Version UID*/
	private static final long serialVersionUID = 1L;

	//constructor
	public ProductNotFound() {
	    super("Product not found");
	  }
}