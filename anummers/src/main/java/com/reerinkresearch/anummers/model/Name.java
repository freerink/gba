package com.reerinkresearch.anummers.model;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("name")
public class Name implements Serializable {

	private static final long serialVersionUID = -5186628336898939128L;

	@Id
	private int id;
	private String name;

	public Name(int id, String name) {
		this.id = id;
		this.setName(name);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Name{id=" + id + ", name=\"" + name + "\"}";
	}

}
