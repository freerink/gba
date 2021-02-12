package com.reerinkresearch.lo3pl.model;

public class Naam {

	private String Voornamen, Voorvoegsel, Geslachtsnaam;

	public Naam(String geslachtsnaam) {
		this.setGeslachtsnaam(geslachtsnaam);
	}

	public String getVoornamen() {
		return Voornamen;
	}

	public void setVoornamen(String voornamen) {
		Voornamen = voornamen;
	}

	public String getVoorvoegsel() {
		return Voorvoegsel;
	}

	public void setVoorvoegsel(String voorvoegsel) {
		Voorvoegsel = voorvoegsel;
	}

	public String getGeslachtsnaam() {
		return Geslachtsnaam;
	}

	public void setGeslachtsnaam(String geslachtsnaam) {
		Geslachtsnaam = geslachtsnaam;
	}
	
}
