package com.reerinkresearch.bag.model;

public class Woonplaats {
	private int code;
	private int gemeenteCode;
	private String naam;

	public Woonplaats(int code) {
	}

	public Woonplaats(int code, int gemeenteCode) {
		this.code = code;
		this.gemeenteCode = gemeenteCode;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public int getGemeenteCode() {
		return gemeenteCode;
	}

	public void setGemeenteCode(int gemeenteCode) {
		this.gemeenteCode = gemeenteCode;
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}
}
