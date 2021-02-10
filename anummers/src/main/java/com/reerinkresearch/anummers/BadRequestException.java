package com.reerinkresearch.anummers;

public class BadRequestException extends RuntimeException {

	private static final long serialVersionUID = 5719181827213873661L;

	public BadRequestException(String message) {
		super("Bad request: " + message);
	}
}
