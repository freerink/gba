package com.reerinkresearch.lo3pl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reerinkresearch.pl.PLException;
import com.reerinkresearch.pl.PersoonsLijst;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reerinkresearch.lo3pl.model.PersoonsLijstWrapper;
import com.reerinkresearch.lo3pl.repo.PersoonsLijstRepository;

@SpringBootApplication
@RestController
public class LO3PLApplication {

	@Autowired
	PersoonsLijstRepository plRepo;

	@GetMapping("/persoonslijsten")
	public List<PersoonsLijst> getPL(@RequestParam(value = "anummer", required = false) Long anummer,
			@RequestParam(value = "geslachtsnaam", required = false) String geslachtsnaam,
			@RequestParam(value = "id", required = false) String id) {

		ObjectMapper objectMapper = new ObjectMapper();

		List<PersoonsLijst> list = new ArrayList<PersoonsLijst>();
		if (id != null) {
			Optional<PersoonsLijstWrapper> result = plRepo.findById(id);
			if (result.isPresent()) {
				PersoonsLijst pl;
				try {
					pl = objectMapper.readValue(result.get().getPl(), PersoonsLijst.class);
					list.add(pl);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		} else if (anummer != null) {
			Iterator<PersoonsLijstWrapper> it = plRepo.findByAnummer(anummer).iterator();
			while (it.hasNext()) {
				PersoonsLijstWrapper w = it.next();
				PersoonsLijst pl;
				try {
					pl = objectMapper.readValue(w.getPl(), PersoonsLijst.class);
					list.add(pl);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		} else if (geslachtsnaam != null && geslachtsnaam.length() > 0) {
			String surname;
			final String wildcard = "*";
			// The search string may only contain 1 wildcard character and it must be the
			// last
			int wildcardIndex = geslachtsnaam.indexOf(wildcard);
			if (wildcardIndex > -1) {
				// wildcard found
				if (wildcardIndex == (geslachtsnaam.length() - 1)) {
					if (wildcardIndex > 0) {
						surname = geslachtsnaam.replace(wildcard, "%");
					} else {
						throw new PLException("Er mag niet gezocht worden met alleen *");
					}
				} else {
					throw new PLException(
							"Er mag slechts 1 * in de zoekterm voorkomen en deze moet aan het einde staan");
				}
			} else {
				// Geen wildcard
				surname = geslachtsnaam;
			}
			Iterator<PersoonsLijstWrapper> it = plRepo.findByGeslachtsnaamLike(surname).iterator();
			while (it.hasNext()) {
				PersoonsLijstWrapper w = it.next();
				PersoonsLijst pl;
				try {
					pl = objectMapper.readValue(w.getPl(), PersoonsLijst.class);
					list.add(pl);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	@PostMapping("/persoonslijsten")
	public String storePL(@RequestBody PersoonsLijst pl) {
		PersoonsLijstWrapper saved;

		// Check if the A nummer already exists
		// If yes, only store the PL if it's about the same person and re-use the PL id
		if (!pl.isValid()) {
			throw new PLException("Nieuwe PL niet valide");
		}
		long anummer = pl.getPersoon().get(0).getAnummer();
		Iterator<PersoonsLijstWrapper> it = plRepo.findByAnummer(anummer).iterator();
		if (it.hasNext()) {
			// Er betaat al een PL met dit A nummer. Is het dezelfde persoon
			PersoonsLijstWrapper w = it.next();

			// Check if we find another PL with the same A nummer
			if (it.hasNext()) {
				throw new PLException("Meer dan 1 bestaande PL voor A nummer " + anummer + " gevonden");
			}

			ObjectMapper objectMapper = new ObjectMapper();

			try {
				PersoonsLijst existingPl = objectMapper.readValue(w.getPl(), PersoonsLijst.class);
				if (!existingPl.isValid()) {
					throw new PLException("Bestaande PL voor A nummer " + anummer + " niet valide");
				}
				if (!existingPl.getPersoon().get(0).getNaam().getGeslachtsnaam()
						.equals(pl.getPersoon().get(0).getNaam().getGeslachtsnaam())) {
					throw new PLException("Geslachtsnaam op bestaande PL ("
							+ existingPl.getPersoon().get(0).getNaam().getGeslachtsnaam()
							+ ") niet gelijk aan die op nieuwe PL ("
							+ pl.getPersoon().get(0).getNaam().getGeslachtsnaam() + ")");
				}
			} catch (JsonProcessingException e) {
				throw new PLException("Bestaande PL voor A nummer " + anummer + " is geen valide JSON");
			}
			// Overschrijf de bestaande PL
			PersoonsLijstWrapper wrapper = new PersoonsLijstWrapper(w.getId(), null);
			wrapper.setPl(pl);
			saved = plRepo.save(wrapper);
		} else {
			// New PL
			PersoonsLijstWrapper wrapper = new PersoonsLijstWrapper(UUID.randomUUID().toString(), null);
			wrapper.setPl(pl);
			saved = plRepo.save(wrapper);
		}
		return saved.getId();
	}

	@DeleteMapping("/persoonslijsten")
	public long deleteAllPersoonslijsten() {
		long count = plRepo.count();
		plRepo.deleteAll();
		return count;
	}

	public static void main(String[] args) {
		SpringApplication.run(LO3PLApplication.class, args);
	}
}
