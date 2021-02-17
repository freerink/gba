package com.reerinkresearch.lo3pl.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table
public class PersoonsLijstWrapper {
	
	@PrimaryKey
	private final String id;
	
	private final String pl; // As a JSON string
	
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

}
