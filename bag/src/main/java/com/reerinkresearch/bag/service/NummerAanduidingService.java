package com.reerinkresearch.bag.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.reerinkresearch.bag.model.NummerAanduiding;

@Service
public class NummerAanduidingService {
	
	// private HashMap<Long, NummerAanduiding> nummerAanduiding = new HashMap<Long, NummerAanduiding>();
	private List<NummerAanduiding> nummerAanduiding = new ArrayList<NummerAanduiding>();

	public boolean store(NummerAanduiding nummerAanduiding) {
		this.nummerAanduiding.add(nummerAanduiding);
		return true;
	}
	
	public int getCount() {
		return this.nummerAanduiding.size();
	}

	public NummerAanduiding get(int index) {
		return this.nummerAanduiding.get(index);
	}
}
