package com.reerinkresearch.lo3pl.model;

import java.util.List;

public class PersoonsLijst {

	//private Persoon[] persoon;
	private List<Persoon> persoon;
	
	// Ouder1 -> index = 0
	// Ouder2 -> index = 1
	private List<List<Ouder>> ouder;

	private Inschrijving inschrijving;
	
	private List<Verblijfplaats> verblijfplaats;
	
	/*
	 * Constructor
	 */
	public PersoonsLijst(long anummer, String geslachtsnaam, int gemeenteCode) {
		this.persoon = Persoon.createMinimumPersoonStapel(anummer, geslachtsnaam);
		this.verblijfplaats = Verblijfplaats.createMinimumVerblijfplaatsStapel(gemeenteCode);
	}
	
	/*
	 * Getters / Setters
	 */
	public long getAnummer() {
		if( this.persoon == null || this.persoon.size() == 0) {
			throw new PLException("Need at least 1 Persoon object");
		}
		return this.persoon.get(0).getAnummer();
	}

	public void setAnummer(long anummer) {
		this.persoon.get(0).setAnummer(anummer);
	}
	
	public Persoon getCurrentPersoon() {
		if( this.persoon == null || this.persoon.size() == 0 ) {
			return null;
		}
		return this.persoon.get(0);
	}
	
	public List<Persoon> getPersoon() {
		return this.persoon;
	}

	public Ouder getCurrentOuder(int ouder) {
		if( this.ouder == null || this.ouder.size() < ouder || this.ouder.get(ouder).size() == 0 ) {
			throw new PLException("Need at least 1 Ouder" + (ouder + 1) + " object");
		}
		return this.ouder.get(ouder).get(0);
	}
	
	public List<Ouder> getOuder(int ouder) {
		if( this.ouder == null || this.ouder.size() < ouder) {
			return null;
		}
		return this.ouder.get(ouder);
	}

	public Inschrijving getInschrijving() {
		return inschrijving;
	}

	public void setInschrijving(Inschrijving inschrijving) {
		this.inschrijving = inschrijving;
	}

	Verblijfplaats getCurrentVerblijfplaats() {
		if( this.verblijfplaats == null || this.verblijfplaats.size() == 0) {
			throw new PLException("Need at least 1 Verblijfplaats object");
		}
		return this.verblijfplaats.get(0);
	}
}
