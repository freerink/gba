package com.reerinkresearch.pl;

import java.util.ArrayList;
import java.util.List;

public class PersoonsLijst {

	private List<Persoon> persoon;

	// Ouder1 -> index = 0
	// Ouder2 -> index = 1
	private List<List<Ouder>> ouder;

	private Inschrijving inschrijving;

	private List<Verblijfplaats> verblijfplaats;

	/*
	 * Constructor
	 */
	public PersoonsLijst() {
	}

	public PersoonsLijst(long anummer, String geslachtsnaam, int gemeenteCode) {
		this.persoon = Persoon.createMinimumPersoonStapel(anummer, geslachtsnaam);
		this.verblijfplaats = Verblijfplaats.createMinimumVerblijfplaatsStapel(gemeenteCode);
	}

	public boolean isValid() {
		// At least one Person record
		if (this.getPersoon() == null || this.getPersoon().size() == 0) {
			return false;
		}
		// groep 01 We need a valid A number
		Anummer anummer = new Anummer(this.getPersoon().get(0).getAnummer());
		if (!anummer.isValid()) {
			return false;
		}
		// groep 02
		if (this.getPersoon().get(0).getNaam() == null
				|| this.getPersoon().get(0).getNaam().getGeslachtsnaam() == null
				|| this.getPersoon().get(0).getNaam().getGeslachtsnaam().length() < 1) {
			return false;
		}
		return true;
	}

	/*
	 * Getters / Setters
	 */
	public void setAnummer(long anummer) {
		if (this.persoon == null) {
			this.persoon = new ArrayList<Persoon>();
			this.persoon.add(new Persoon());
		}
		this.persoon.get(0).setAnummer(anummer);
	}

	public void setPersoon(List<Persoon> persoon) {
		this.persoon = persoon;
	}

	public List<Persoon> getPersoon() {
		return this.persoon;
	}

	public Ouder getCurrentOuder(int ouder) {
		if (this.ouder == null || this.ouder.size() < ouder || this.ouder.get(ouder).size() == 0) {
			throw new PLException("Need at least a current Ouder" + (ouder + 1));
		}
		return this.ouder.get(ouder).get(0);
	}

	public void setOuder(List<List<Ouder>> ouder) {
		this.ouder = ouder;
	}

	public List<List<Ouder>> getOuder() {
		return this.ouder;
	}

	public List<Ouder> getOuder(int ouder) {
		if (this.ouder == null || this.ouder.size() < ouder) {
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

	public void setVerblijfplaats(List<Verblijfplaats> verblijfplaats) {
		this.verblijfplaats = verblijfplaats;
	}

	public List<Verblijfplaats> getVerblijfplaats() {
		return this.verblijfplaats;
	}

}
