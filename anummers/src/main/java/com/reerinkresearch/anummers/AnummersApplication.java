package com.reerinkresearch.anummers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reerinkresearch.anummers.repo.AnummerRepository;

import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@RestController
public class AnummersApplication {

	private final AtomicLong counter = new AtomicLong();

	@Autowired
	AnummerRepository repo;

	@GetMapping("/anummers")
	public Anummer getAnummer(@RequestParam(value = "startFrom", required = false) Long startFrom,
			@RequestParam(value = "maxIterations", required = false) Long maxIiterations) {
		if (startFrom == null) {
			startFrom = 1010101025L;
		}
		if (maxIiterations == null) {
			maxIiterations = 1L;
		}
		Anummer a = new Anummer(counter.incrementAndGet(), startFrom);
		long iter = 0L;
		while (!a.isValid() && iter < maxIiterations) {
			long skipTo = a.getSkipTo();
			a = new Anummer(counter.incrementAndGet(), skipTo);
			iter++;
		}
		return a;
	}

	@PostMapping("/anummers")
	Anummer storeAnummer(@RequestBody Anummer a) {
		// Check if we received a valid A number
		if (!a.isValid()) {
			throw new InvalidAnummerException(a);
		}
		// Store it in the database
		com.reerinkresearch.anummers.model.Anummer persistedAnummer = new com.reerinkresearch.anummers.model.Anummer(
				a.getAnummer(), null);
		repo.save(persistedAnummer);

		return a;
	}

	public static void main(String[] args) {
		SpringApplication.run(AnummersApplication.class, args);
	}

}
