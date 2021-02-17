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
			@RequestParam(value = "isHistorischZoeken", required = false) Boolean isHistorischZoeken,
			@RequestParam(value = "geslachtsnaam", required = false) String geslachtsnaam,
			@RequestParam(value = "id", required = false) String id) {

		ObjectMapper objectMapper = new ObjectMapper();

		List<PersoonsLijst> list = new ArrayList<PersoonsLijst>();
		boolean searchInHistory = (isHistorischZoeken != null && isHistorischZoeken);
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
			Iterator<PersoonsLijstWrapper> it = plRepo.findAll().iterator();
			while (it.hasNext()) {
				PersoonsLijstWrapper w = it.next();
				PersoonsLijst pl;
				try {
					pl = objectMapper.readValue(w.getPl(), PersoonsLijst.class);
					if (pl.getPersoon() != null) {
						// Actueel zoeken of eventueel ook historisch
						int i = 0;
						while (i < (searchInHistory ? pl.getPersoon().size() : 1)) {
							if (pl.getPersoon().get(i).getAnummer() == anummer) {
								list.add(pl);
								break;
							}
							i++;
						}
					}
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		} else if (geslachtsnaam != null && geslachtsnaam.length() > 0) {
			String surname = geslachtsnaam;
			boolean exactMatch = true;
			if (geslachtsnaam.endsWith("*")) {
				if (geslachtsnaam.length() > 1) {
					surname = geslachtsnaam.substring(0, geslachtsnaam.length() - 1);
					exactMatch = false;
				} else {
					throw new PLException("Er mag niet gezocht worden met alleen *");
				}
			}
			Iterator<PersoonsLijstWrapper> it = plRepo.findAll().iterator();
			while (it.hasNext()) {
				PersoonsLijstWrapper w = it.next();
				PersoonsLijst pl;
				try {
					pl = objectMapper.readValue(w.getPl(), PersoonsLijst.class);
					if (pl.getPersoon() != null) {
						// Actueel zoeken of eventueel ook historisch
						int i = 0;
						while (i < (searchInHistory ? pl.getPersoon().size() : 1)) {
							if (pl.getPersoon().get(i).getNaam() != null
									&& pl.getPersoon().get(i).getNaam().getGeslachtsnaam() != null
									&& ((exactMatch
											&& pl.getPersoon().get(i).getNaam().getGeslachtsnaam().equals(surname))
											|| (!exactMatch && pl.getPersoon().get(i).getNaam().getGeslachtsnaam()
													.startsWith(surname)))) {
								list.add(pl);
								break;
							}
							i++;
						}
					}
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	@PostMapping("/persoonslijsten")
	public String storePL(@RequestBody PersoonsLijst pl) {
		ObjectMapper objectMapper = new ObjectMapper();
		String plAsString;
		try {
			plAsString = objectMapper.writeValueAsString(pl);
			PersoonsLijstWrapper wrapper = new PersoonsLijstWrapper(UUID.randomUUID().toString(), plAsString);

			PersoonsLijstWrapper saved = plRepo.save(wrapper);
			return saved.getId();
		} catch (JsonProcessingException e) {
			throw new PLException("JsonProcessingException: " + e.getMessage());
		}
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
