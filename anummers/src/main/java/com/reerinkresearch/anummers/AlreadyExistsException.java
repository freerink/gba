package com.reerinkresearch.anummers;

public class AlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 3917109710189780251L;

	public AlreadyExistsException(String message) {
		super(message + " already exists");
	}
}
