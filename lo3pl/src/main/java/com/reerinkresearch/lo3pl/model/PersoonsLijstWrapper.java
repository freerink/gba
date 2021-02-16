package com.reerinkresearch.lo3pl.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import com.reerinkresearch.pl.PersoonsLijst;

@RedisHash("persoonslijst")
public class PersoonsLijstWrapper {
	
	@Id
	private String id;
	
	private PersoonsLijst pl;
	
	public PersoonsLijstWrapper() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PersoonsLijst getPl() {
		return pl;
	}

	public void setPl(PersoonsLijst pl) {
		this.pl = pl;
	}

}
