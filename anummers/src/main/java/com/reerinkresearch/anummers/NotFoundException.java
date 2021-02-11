package com.reerinkresearch.anummers;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotFoundException(String message) {
		super("Not found: " + message);
	}
}
