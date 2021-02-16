package com.reerinkresearch.lo3pl.model;

public class Naam {

	private String voornamen;
	private String voorvoegsel;
	private String geslachtsnaam;

	public Naam() {	
	}
	
	public Naam(String geslachtsnaam) {
		this.setGeslachtsnaam(geslachtsnaam);
	}

	public String getVoornamen() {
		return voornamen;
	}

	public void setVoornamen(String voornamen) {
		this.voornamen = voornamen;
	}

	public String getVoorvoegsel() {
		return voorvoegsel;
	}

	public void setVoorvoegsel(String voorvoegsel) {
		this.voorvoegsel = voorvoegsel;
	}

	public String getGeslachtsnaam() {
		return geslachtsnaam;
	}

	public void setGeslachtsnaam(String geslachtsnaam) {
		this.geslachtsnaam = geslachtsnaam;
	}
	
}
