package com.reerinkresearch.anummers.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("freeAnummer")
public class FreeAnummer implements Serializable  {

	private static final long serialVersionUID = -7412557049832202007L;

	@Id
	private Long anummer;

	public FreeAnummer(long anummer) {
		this.anummer = anummer;
	}

	public Long getAnummer() {
		return anummer;
	}

	public void setAnummer(Long anummer) {
		this.anummer = anummer;
	}

}
