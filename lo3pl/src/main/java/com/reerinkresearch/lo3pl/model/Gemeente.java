package com.reerinkresearch.lo3pl.model;

public class Gemeente {

	private int gemeenteCode;
	private Datum datumVanInschrijving;
	
	public Gemeente(int gemeenteCode) {
		this.setGemeenteCode(gemeenteCode);
	}
	
	public int getGemeenteCode() {
		return gemeenteCode;
	}
	
	public void setGemeenteCode(int gemeenteCode) {
		this.gemeenteCode = gemeenteCode;
	}
	
	public Datum getDatumVanInschrijving() {
		return datumVanInschrijving;
	}
	public void setDatumVanInschrijving(Datum datumVanInschrijving) {
		this.datumVanInschrijving = datumVanInschrijving;
	}
	
	
}
