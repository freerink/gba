package com.reerinkresearch.anummers;

import com.reerinkresearch.pl.Anummer;

public class InvalidAnummerException extends RuntimeException {

	private static final long serialVersionUID = 7197614277344895892L;

	InvalidAnummerException(Anummer a) {
		super("Invalid Anummer: " + a.getAnummer() + ": " + a.getError());
	}
}
