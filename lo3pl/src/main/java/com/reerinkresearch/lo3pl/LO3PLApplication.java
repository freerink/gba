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
			// The search string may only contain 1 wildcard character and it must be the last
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
		PersoonsLijstWrapper wrapper = new PersoonsLijstWrapper(UUID.randomUUID().toString(), null);
		wrapper.setPl(pl);

		PersoonsLijstWrapper saved = plRepo.save(wrapper);
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
