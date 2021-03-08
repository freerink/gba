package com.reerinkresearch.bag.service;

import java.util.HashMap;

import org.springframework.stereotype.Service;

import com.reerinkresearch.bag.model.OpenbareRuimte;

@Service
public class OpenbareRuimteService {

	private HashMap<Long, OpenbareRuimte> openbareRuimte = new HashMap<Long, OpenbareRuimte>();

	public OpenbareRuimte getOpenbareRuimte(long code) {
		return this.openbareRuimte.get(code);
	}

	public boolean store(OpenbareRuimte ruimte) {
		if (getOpenbareRuimte(ruimte.getCode()) != null) {
			return false;
		}
		this.openbareRuimte.put(ruimte.getCode(), ruimte);
		return true;
	}
	
	public int getCount() {
		return this.openbareRuimte.size();
	}
}
