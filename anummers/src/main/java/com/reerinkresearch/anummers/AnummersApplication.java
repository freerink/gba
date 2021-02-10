package com.reerinkresearch.anummers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reerinkresearch.anummers.repo.AnummerRepository;

@SpringBootApplication
@RestController
public class AnummersApplication {

	@Autowired
	AnummerRepository repo;

	@GetMapping("/anummers")
	public Anummer getAnummer(@RequestParam(value = "startFrom", required = false) Long startFrom,
			@RequestParam(value = "maxIterations", required = false) Long maxIiterations,
			@RequestParam(value = "anummer", required = false) Long anummer) {

		// Check the repo for the existence of the A-number
		Anummer a;
		if (anummer != null) {
			if (startFrom != null) {
				throw new BadRequestException("Specify anummer or startFrom");
			}
			if (maxIiterations != null) {
				throw new BadRequestException("Specify anummer or maxIiterations");
			}
			Optional<com.reerinkresearch.anummers.model.Anummer> persistedAnummer = repo.findById(anummer);
			if ( ! persistedAnummer.isPresent()) {
				throw new NotFoundException(anummer);
			}
			com.reerinkresearch.anummers.model.Anummer p = persistedAnummer.get();
			a = new Anummer(anummer, p.getGemeenteCode() != null ? p.getGemeenteCode() : 0);
		} else {
			if (startFrom == null) {
				startFrom = 1010101025L;
			}
			if (maxIiterations == null) {
				maxIiterations = 1L;
			}
			a = new Anummer(startFrom, 0);
			long iter = 0L;
			while (!a.isValid() && iter < maxIiterations) {
				long skipTo = a.getSkipTo();
				a = new Anummer(skipTo, 0);
				iter++;
			}
		}
		return a;
	}

	@PostMapping("/anummers")
	Anummer storeAnummer(@RequestBody Anummer a) {
		// Check if we received a valid A number
		if (!a.isValid()) {
			throw new InvalidAnummerException(a);
		}
		// Check if we have it already
		if (!repo.existsById(a.getAnummer())) {
			// Store it in the database
			com.reerinkresearch.anummers.model.Anummer persistedAnummer = new com.reerinkresearch.anummers.model.Anummer(
					a.getAnummer(), a.getGemeenteCode());
			repo.save(persistedAnummer);
		} else {
			throw new AlreadyExistsException(a.getAnummer());
		}
		return a;
	}

	public static void main(String[] args) {
		SpringApplication.run(AnummersApplication.class, args);
	}

}
