package com.reerinkresearch.bag.service;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.reerinkresearch.bag.model.NummerAanduiding;

@Service
public class NummerAanduidingService {
	private HashMap<Long, NummerAanduiding> nummerAanduiding = new HashMap<Long, NummerAanduiding>();

	public boolean store(NummerAanduiding nummerAanduiding) {
		this.nummerAanduiding.put(nummerAanduiding.getCode(), nummerAanduiding);
		return true;
	}
	
	public int getCount() {
		return this.nummerAanduiding.size();
	}

}
