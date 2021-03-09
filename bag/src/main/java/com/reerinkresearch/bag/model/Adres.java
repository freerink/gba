package com.reerinkresearch.bag.model;

public class Adres {
	private String straat;
	private int huisNummer;
	private String huisLetter;
	private String postCode;
	private String woonplaats;
	private int gemeenteCode;

	public int getHuisNummer() {
		return huisNummer;
	}
	
	public void setHuisNummer(int huisNummer) {
		this.huisNummer = huisNummer;
	}

	public String getHuisLetter() {
		return huisLetter;
	}

	public void setHuisLetter(String huisLetter) {
		this.huisLetter = huisLetter;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getWoonplaats() {
		return woonplaats;
	}

	public void setWoonplaats(String woonplaats) {
		this.woonplaats = woonplaats;
	}

	public String getStraat() {
		return straat;
	}

	public void setStraat(String straat) {
		this.straat = straat;
	}

	public int getGemeenteCode() {
		return gemeenteCode;
	}

	public void setGemeenteCode(int gemeenteCode) {
		this.gemeenteCode = gemeenteCode;
	}
}
