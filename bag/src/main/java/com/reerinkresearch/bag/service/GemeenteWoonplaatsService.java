package com.reerinkresearch.bag.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class GemeenteWoonplaatsService {
	// Store gemeente - woonplaatsen
	private HashMap<Integer, List<Integer>> gemeenteWoonplaats = new HashMap<Integer, List<Integer>>();

	// Store woonplaats - gemeente
	private HashMap<Integer, Integer> woonplaatsGemeente = new HashMap<Integer, Integer>();

	public Integer getGemeente(int woonplaats) {
		if (this.woonplaatsGemeente.containsKey(woonplaats)) {
			return this.woonplaatsGemeente.get(woonplaats);
		}
		return null;
	}

	public List<Integer> getWoonplaatsen(int gemeente) {
		return this.gemeenteWoonplaats.get(gemeente);
	}

	public boolean store(int gemeente, int woonplaats) {
		// Check that the record does not already exist
		List<Integer> woonplaatsen = this.getWoonplaatsen(gemeente);
		if (woonplaatsen != null) {
			// Gemeente exists, check if the list has the woonplaats
			if (woonplaatsen.contains(woonplaats)) {
				return false;
			}
		}
		if (this.getGemeente(woonplaats) != null) {
			return false;
		}
		// Store the new combination in both hash maps
		// gemeenteWoonplaats
		if (woonplaatsen != null) {
			woonplaatsen.add(woonplaats);
		} else {
			woonplaatsen = new ArrayList<Integer>();
			woonplaatsen.add(woonplaats);
			this.gemeenteWoonplaats.put(gemeente, woonplaatsen);
		}
		// woonplaatsGemeente
		this.woonplaatsGemeente.put(woonplaats, gemeente);
		return true;
	}
	
	public int getGemeenteCount() {
		return this.gemeenteWoonplaats.size();
	}
	
	public int getWoonplaatsCount() {
		return this.woonplaatsGemeente.size();
	}
}
