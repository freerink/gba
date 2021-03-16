package com.reerinkresearch.pl;

import java.util.ArrayList;
import java.util.List;

public class Persoon {

	private long anummer;
	
	private Long bsn;
	
	private Naam naam;
	
	private Geboorte geboorte;
	
	private Geslacht geslacht;
	
	public Persoon() {	
	}
	
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
	
	public Long getBsn() {
		return bsn;
	}

	public void setBsn(Long bsn) {
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

	public Geboorte getGeboorte() {
		return geboorte;
	}

	public void setGeboorte(Geboorte geboorte) {
		this.geboorte = geboorte;
	}

	public Geslacht getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(Geslacht geslacht) {
		this.geslacht = geslacht;
	}
	
}
