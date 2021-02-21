package com.reerinkresearch.lo3pl.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reerinkresearch.pl.PersoonsLijst;

@Table
public class PersoonsLijstWrapper {

	@PrimaryKey
	private final String id;

	// Some data from the PL used to query on
	private long anummer;

	private String geslachtsnaam;

	// The complete PL as a JSON string
	private String pl;

	public void setPl(PersoonsLijst pl) {
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			this.pl = objectMapper.writeValueAsString(pl);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		setAnummer(pl.getPersoon().get(0).getAnummer());
		setGeslachtsnaam(pl.getPersoon().get(0).getNaam().getGeslachtsnaam());
	}

	public PersoonsLijstWrapper(String id, String pl) {
		this.id = id;
		this.pl = pl;
	}

	public String getId() {
		return id;
	}

	public String getPl() {
		return pl;
	}

	public long getAnummer() {
		return anummer;
	}

	public void setAnummer(long anummer) {
		this.anummer = anummer;
	}

	public String getGeslachtsnaam() {
		return geslachtsnaam;
	}

	public void setGeslachtsnaam(String geslachtsnaam) {
		this.geslachtsnaam = geslachtsnaam;
	}

}
