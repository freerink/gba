package com.reerinkresearch.lo3pl.model;

public class PLException extends RuntimeException {
	
	private static final long serialVersionUID = 1407614277344895892L;
	
	PLException(String message) {
		super("PLException: "+ message);
	}
}
