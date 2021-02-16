package com.reerinkresearch.anummers;

import java.util.ArrayList;
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

import com.reerinkresearch.anummers.model.Name;
import com.reerinkresearch.anummers.repo.AnummerRepository;
import com.reerinkresearch.anummers.repo.NameRepository;
import com.reerinkresearch.pl.PersoonsLijst;

@SpringBootApplication
@RestController
public class AnummersApplication {

	@Autowired
	AnummerRepository anummerRepo;

	@Autowired
	NameRepository nameRepo;

	@GetMapping("/names")
	public List<Name> getNames(@RequestParam(value = "id", required = false) Integer id,
			@RequestParam(value = "count", required = false) Boolean isCount) {
		List<Name> names = new ArrayList<Name>();
		if (id != null) {
			Optional<Name> foundName = nameRepo.findById(id);
			if (foundName.isPresent()) {
				names.add(foundName.get());
			} else {
				throw new NotFoundException("Name with id:" + id);
			}
		} else if (isCount != null && isCount) {
			// Return the last index
			long count = nameRepo.count();
			if (count > 0) {
				Optional<Name> foundName = nameRepo.findById((int) count - 1);
				if (foundName.isPresent()) {
					names.add(foundName.get());
				} else {
					throw new NotFoundException("Name with id:" + id);
				}
			}
		} else {
			long count = nameRepo.count();
			int random = getRandomNumber(0, (int) count - 1);
			Optional<Name> foundName = nameRepo.findById(random);
			if (foundName.isPresent()) {
				names.add(foundName.get());
			} else {
				throw new NotFoundException("Name with id:" + id);
			}
		}
		return names;
	}

	private String getRandomName() {
		long count = nameRepo.count();
		if (count > 0) {
			int random = getRandomNumber(0, (int) count - 1);
			Optional<Name> foundName = nameRepo.findById(random);
			if (foundName.isPresent()) {
				return foundName.get().getName();
			}
		}
		return null;
	}

	private int getRandomNumber(int min, int max) {
		double rand = Math.random();
		return (int) ((rand * rand * (max + 1 - min)) + min);
	}

	private long getRandomNumber(long min, long max) {
		double rand = Math.random();
		return (long) ((rand * (max + 1 - min)) + min);
	}

	@GetMapping("/randoms")
	public int[] getRandomFrequencies(@RequestParam(value = "iterations", required = false) Integer iterations,
			@RequestParam(value = "min", required = false) Integer min,
			@RequestParam(value = "max", required = false) Integer max) {
		int localMin = (min != null ? min : 1);
		int localMax = (max != null ? max : 100);
		int localIterations = (iterations != null ? iterations : 100000);

		int[] freqs = new int[localMax - localMin + 1];
		for (int i = 0; i < localIterations; i++) {
			int rand = getRandomNumber(localMin, localMax);
			freqs[rand - localMin]++;
		}
		return freqs;
	}

	@PostMapping("/names")
	Name storeName(@RequestBody Name name) {
		if (name == null) {
			throw new BadRequestException("Specify a name object to add to the data store.");
		}
		if (name == null || name.getName() == null || name.getName().length() == 0) {
			throw new BadRequestException("Specify a name to add to the data store.");
		}
		if (nameRepo.existsById(name.getId())) {
			throw new AlreadyExistsException("Name id: " + name.getId());
		}
		nameRepo.save(name);
		return name;
	}

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
			Optional<com.reerinkresearch.anummers.model.Anummer> persistedAnummer = anummerRepo.findById(anummer);
			if (!persistedAnummer.isPresent()) {
				throw new NotFoundException("A nummer: " + anummer);
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
	public Anummer storeAnummer(@RequestBody Anummer a) {
		// Check if we received a valid A number
		if (!a.isValid()) {
			throw new InvalidAnummerException(a);
		}
		// Check if we have it already
		if (!anummerRepo.existsById(a.getAnummer())) {
			// Store it in the database
			com.reerinkresearch.anummers.model.Anummer persistedAnummer = new com.reerinkresearch.anummers.model.Anummer(
					a.getAnummer(), a.getGemeenteCode());
			anummerRepo.save(persistedAnummer);
		} else {
			throw new AlreadyExistsException("Anummer " + a.getAnummer());
		}
		return a;
	}

	@DeleteMapping("/anummers")
	public long deleteAllAnummers() {
		long count = anummerRepo.count();
		anummerRepo.deleteAll();
		return count;
	}

	@DeleteMapping("/names")
	public long deleteAllNames() {
		long count = nameRepo.count();
		nameRepo.deleteAll();
		return count;
	}

	@PostMapping("/generatePersoonslijst")
	public PersoonsLijst generateAndStorePL() {
		// Gemeente code bepalen (random tussen 1 en 200 incl.)
		int gemeenteCode = getRandomNumber(1, 200);
		
		// A nummer ophalen voor gemeente code
		long randStartFrom = this.getRandomNumber(1010101025L, 9010101010L);
		Anummer a = this.getAnummer(randStartFrom, 10000L, null);
		if( !a.isValid()) {
			throw new InvalidAnummerException(a);
		}
		// Store the A nummer for this gemeente
		//this.storeAnummer(a);
		
		// Namen ophalen: voornamen 1-4, voorvoegsel 0-2, geslachtsnaam 1-3
		String voornamen = this.generateNames(1, 4);
		String geslachtsnaam = this.generateNames(1, 3);
		String voorvoegsel = this.generateVoorvoegsel(0, 2);
		
		// PL genereren
		PersoonsLijst pl = new PersoonsLijst(a.getAnummer(), geslachtsnaam, gemeenteCode);
		pl.getPersoon().get(0).getNaam().setVoornamen(voornamen);
		pl.getPersoon().get(0).getNaam().setVoorvoegsel(voorvoegsel);
		return pl;
	}

	private String generateVoorvoegsel(int min, int max) {
		StringBuffer buf = new StringBuffer();
		String[] voorvoegsels = {"a", "'t", "auf", "den", "der", "am", "bij", "da", "del", "du", "de", "van", "der", "den", "tot"};
		
		int rand = this.getRandomNumber(min, max);
		for (int i = 0; i < rand; i++) {
			buf.append(voorvoegsels[this.getRandomNumber(0, voorvoegsels.length - 1)] + " ");
		}
		return buf.length() == 0 ? null : buf.toString().strip();
	}

	private String generateNames(int min, int max) {
		StringBuffer buf = new StringBuffer();
		
		int rand = getRandomNumber(min, max);
		for (int i = 0; i < rand; i++) {
			buf.append(getRandomName() + " ");
		}
		return buf.toString().strip();
	}

	public static void main(String[] args) {
		SpringApplication.run(AnummersApplication.class, args);
	}

}
