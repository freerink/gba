package com.reerinkresearch.anummers;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 8397614277344895892L;

	public NotFoundException(String message) {
		super("Not found: " + message);
	}
}
