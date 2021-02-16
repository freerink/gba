package com.reerinkresearch.lo3pl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reerinkresearch.lo3pl.model.PLException;
import com.reerinkresearch.lo3pl.model.PersoonsLijst;
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
		List<PersoonsLijst> list = new ArrayList<PersoonsLijst>();
		boolean searchInHistory = (isHistorischZoeken != null && isHistorischZoeken);
		if (id != null) {
			Optional<PersoonsLijst> result = plRepo.findById(id);
			if (result.isPresent()) {
				list.add(result.get());
			}
		} else if (anummer != null) {
			Iterator<PersoonsLijst> it = plRepo.findAll().iterator();
			while (it.hasNext()) {
				PersoonsLijst pl = it.next();
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
			}
		} else if (geslachtsnaam != null && geslachtsnaam.length() > 0) {
			String surname = geslachtsnaam;
			boolean exactMatch = true;
			if (geslachtsnaam.endsWith("*")) {
				if (geslachtsnaam.length() > 1) {
					surname = geslachtsnaam.substring(0, geslachtsnaam.length() - 1);
					exactMatch = false;
				} else {
					throw new PLException("Mag niet zoeken met alleen *");
				}
			}
			Iterator<PersoonsLijst> it = plRepo.findAll().iterator();
			while (it.hasNext()) {
				PersoonsLijst pl = it.next();
				if (pl.getPersoon() != null) {
					// Actueel zoeken of eventueel ook historisch
					int i = 0;
					while (i < (searchInHistory ? pl.getPersoon().size() : 1)) {
						if (pl.getPersoon().get(i).getNaam() != null
								&& pl.getPersoon().get(i).getNaam().getGeslachtsnaam() != null
								&& ((exactMatch && pl.getPersoon().get(i).getNaam().getGeslachtsnaam().equals(surname))
										|| (!exactMatch && pl.getPersoon().get(i).getNaam().getGeslachtsnaam()
												.startsWith(surname)))) {
							list.add(pl);
							break;
						}
						i++;
					}
				}
			}
		}
		return list;
	}

	@GetMapping("/generatePersoonslijst")
	public PersoonsLijst generatePL(@RequestParam(value = "anummer", required = true) Long anummer,
			@RequestParam(value = "geslachtsnaam", required = true) String geslachtsnaam,
			@RequestParam(value = "gemeenteCode", required = true) Integer gemeenteCode) {
		long count = plRepo.count();
		PersoonsLijst pl = new PersoonsLijst(count, anummer, geslachtsnaam, gemeenteCode);
		return pl;
	}

	@PostMapping("/persoonslijsten")
	public PersoonsLijst storePL(@RequestBody PersoonsLijst pl) {
		plRepo.save(pl);
		return pl;
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
