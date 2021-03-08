package com.reerinkresearch.bag.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.reerinkresearch.bag.model.Woonplaats;

@Service
public class GemeenteWoonplaatsService {
	// To store gemeenteCode - woonplaatsCodes
	private HashMap<Integer, List<Integer>> gemeenteWoonplaats = new HashMap<Integer, List<Integer>>();

	// To store woonplaatsCode - Woonplaats (woonplaatsCode, naam, gemeente)
	private HashMap<Integer, Woonplaats> woonplaatsGemeente = new HashMap<Integer, Woonplaats>();

	public Integer getGemeente(int woonplaats) {
		Woonplaats w = getWoonplaats(woonplaats);
		if (w != null) {
			return w.getGemeenteCode();
		}
		return null;
	}

	public Woonplaats getWoonplaats(int woonplaats) {
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
		this.woonplaatsGemeente.put(woonplaats, new Woonplaats(woonplaats, gemeente));
		return true;
	}
	
	public int getGemeenteCount() {
		return this.gemeenteWoonplaats.size();
	}
	
	public int getWoonplaatsCount() {
		return this.woonplaatsGemeente.size();
	}

	public boolean updateWoonplaats(int woonplaatsCode, String woonplaatsNaam) {
		Woonplaats w = getWoonplaats(woonplaatsCode);
		if( w == null) {
			return false;
		}
		w.setNaam(woonplaatsNaam);
		return true;
	}
}
