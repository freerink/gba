package com.reerinkresearch.lo3pl.model;

public class Gemeente {

	private Integer gemeenteCode;
	private Datum datumVanInschrijving;

	public Gemeente() {
	}

	public Gemeente(int gemeenteCode) {
		this.setGemeenteCode(gemeenteCode);
	}
	
	public Integer getGemeenteCode() {
		return gemeenteCode;
	}
	
	public void setGemeenteCode(Integer gemeenteCode) {
		this.gemeenteCode = gemeenteCode;
	}
	
	public Datum getDatumVanInschrijving() {
		return datumVanInschrijving;
	}
	public void setDatumVanInschrijving(Datum datumVanInschrijving) {
		this.datumVanInschrijving = datumVanInschrijving;
	}
	
	
}
