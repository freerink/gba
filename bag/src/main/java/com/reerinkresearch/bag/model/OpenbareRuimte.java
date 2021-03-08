package com.reerinkresearch.bag.model;

public class OpenbareRuimte {

	private long code;
	private String naam;
	private int woonplaatsCode;
	
	public OpenbareRuimte(long openbareRuimteId, int woonplaatsRef, String openbareRuimteName) {
		this.code = openbareRuimteId;
		this.naam = openbareRuimteName;
		this.woonplaatsCode = woonplaatsRef;
	}

	public long getCode() {
		return code;
	}
	
	public void setCode(long code) {
		this.code = code;
	}
	
	public String getNaam() {
		return naam;
	}
	
	public void setNaam(String naam) {
		this.naam = naam;
	}
	
	public int getWoonplaatsCode() {
		return woonplaatsCode;
	}
	
	public void setWoonplaatsCode(int woonplaatsCode) {
		this.woonplaatsCode = woonplaatsCode;
	}
	
}
