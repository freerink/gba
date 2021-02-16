package com.reerinkresearch.pl;

import java.util.ArrayList;
import java.util.List;

public class Verblijfplaats {

	private Gemeente gemeente;

	public Verblijfplaats() {
	}

	public Verblijfplaats(int gemeenteCode) {
		this.gemeente = new Gemeente(gemeenteCode);
	}

	public Gemeente getGemeente() {
		return gemeente;
	}

	public void setGemeente(Gemeente gemeente) {
		this.gemeente = gemeente;
	}

	public static List<Verblijfplaats> createMinimumVerblijfplaatsStapel(int gemeenteCode) {
		List<Verblijfplaats> verblijfplaatsStapel = new ArrayList<Verblijfplaats>();
		
		verblijfplaatsStapel.add(new Verblijfplaats(gemeenteCode));
		return verblijfplaatsStapel;
	}

}
