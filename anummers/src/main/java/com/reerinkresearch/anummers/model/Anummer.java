package com.reerinkresearch.anummers.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("anummer")
public class Anummer implements Serializable {

	private static final long serialVersionUID = -1698456345510392128L;
	
	@Id
	private Long anummer;
	private Integer gemeenteCode;

	public Anummer(Long anummer, Integer gemeenteCode) {
		this.anummer = anummer;
		this.gemeenteCode = gemeenteCode;
	}
	
	public Long getAnummer() {
		return anummer;
	}

	public void setAnummer(Long anummer) {
		this.anummer = anummer;
	}

	public Integer getGemeenteCode() {
		return gemeenteCode;
	}

	public void setGemeenteCode(Integer gemeenteCode) {
		this.gemeenteCode = gemeenteCode;
	}
	
	@Override
	public String toString() {
		return "Anummer{anummer=" + anummer + ", gemeenteCode=" + gemeenteCode + "}";
	}
}
