package com.reerinkresearch.lo3pl.model;

import java.util.ArrayList;
import java.util.List;

public class Persoon {

	private long anummer;
	
	private long bsn;
	
	private Naam naam;
	
	public Persoon(long anummer, String geslachtsnaam) {
		this.anummer = anummer;
		this.naam = new Naam(geslachtsnaam);
	}

	public long getAnummer() {
		return anummer;
	}
	
	public void setAnummer(long anummer) {
		this.anummer = anummer;
	}
	
	public long getBsn() {
		return bsn;
	}

	public void setBsn(long bsn) {
		this.bsn = bsn;
	}

	public Naam getNaam() {
		return naam;
	}
	
	public void setNaam(Naam naam) {
		this.naam = naam;
	}
	
	static List<Persoon> createMinimumPersoonStapel(long anummer, String geslachtsnaam) {
		List<Persoon> persoonStapel = new ArrayList<Persoon>();
		
		persoonStapel.add(new Persoon(anummer, geslachtsnaam));
		
		return persoonStapel;
	}
	
}
