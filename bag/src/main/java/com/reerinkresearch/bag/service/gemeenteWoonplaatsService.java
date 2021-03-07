package com.reerinkresearch.bag.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class gemeenteWoonplaatsService {
	// Store gemeente - woonplaatsen
	private HashMap<Integer, List<Integer>> gemeenteWoonplaats = new HashMap<Integer,  List<Integer>>();
	
	// Store woonplaats - gemeente
	private HashMap<Integer, Integer> woonplaatsGemeente = new HashMap<Integer, Integer>();
	
	public int getGemeente(int woonplaats) {
		return 0;
	}
	
	public int[] getWoonplaatsen(int gemeente) {
		return null;
	}
	
	public boolean store(int gemeente, int woonplaats) {
		// Check that the record does not already exist
		List<Integer> woonplaatsen = this.gemeenteWoonplaats.get(gemeente);
		if(woonplaatsen != null) {
			// Gemeente exists, check if the list has the woonplaats
			if(woonplaatsen.contains(woonplaats)) {
				return false;
			}
		}
		if(this.woonplaatsGemeente.containsKey(woonplaats)) {
			return false;
		}
		// Store the new combination in both hash maps
		// gemeenteWoonplaats
		if(woonplaatsen != null) {
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
}
