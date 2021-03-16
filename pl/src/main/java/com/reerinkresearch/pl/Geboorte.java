package com.reerinkresearch.pl;

public class Geboorte {

	private Datum datum;
	private String plaats;
	private String land;
	
	public Geboorte(Datum datum, String plaats, String land) {
		this.datum = datum;
		this.plaats = plaats;
		this.land = land;
	}

	public Datum getDatum() {
		return datum;
	}
	
	public void setDatum(Datum datum) {
		this.datum = datum;
	}

	public String getPlaats() {
		return plaats;
	}

	public void setPlaats(String plaats) {
		this.plaats = plaats;
	}

	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		this.land = land;
	}
	
}
