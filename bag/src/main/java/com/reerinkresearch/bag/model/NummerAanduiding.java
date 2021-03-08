package com.reerinkresearch.bag.model;

public class NummerAanduiding {
	private long code;
	private String postCode;
	private int huisNummer;
	private String huisLetter;
	private long openbareRuimteCode;
	
	public NummerAanduiding(long nummerAanduidingId, String postCode, int huisNummer, String huisLetter,
			long openbareRuimteRef) {
		this.code = nummerAanduidingId;
		this.postCode = postCode;
		this.huisNummer = huisNummer;
		this.huisLetter = huisLetter;
		this.openbareRuimteCode = openbareRuimteRef;
	}

	public long getCode() {
		return code;
	}
	
	public void setCode(long code) {
		this.code = code;
	}
	
	public String getPostCode() {
		return postCode;
	}
	
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	
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
	
	public long getOpenbareRuimteCode() {
		return openbareRuimteCode;
	}
	
	public void setOpenbareRuimteCode(long openbareRuimteCode) {
		this.openbareRuimteCode = openbareRuimteCode;
	}

}
