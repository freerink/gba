package com.reerinkresearch.anummers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.reerinkresearch.anummers.model.Name;
import com.reerinkresearch.anummers.repo.AddressRepository;
import com.reerinkresearch.anummers.repo.AnummerRepository;
import com.reerinkresearch.anummers.repo.NameRepository;
import com.reerinkresearch.pl.Adres;
import com.reerinkresearch.pl.Datum;
import com.reerinkresearch.pl.Geboorte;
import com.reerinkresearch.pl.Geslacht;
import com.reerinkresearch.pl.PersoonsLijst;

@SpringBootApplication
@RestController
public class AnummersApplication {

	private static final long ANUMMER_GENERATE_MAXITERATIONS = 100000L;

	private static final Logger LOG = LoggerFactory.getLogger(AnummersApplication.class);

	@Autowired
	AnummerRepository anummerRepo;

	@Autowired
	NameRepository nameRepo;

	@Autowired
	AddressRepository addressRepo;

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
	public Name storeName(@RequestBody Name name) {
		if (name == null || name.getName() == null || name.getName().length() == 0) {
			throw new BadRequestException("Specify a name to add to the data store.");
		}
		if (nameRepo.existsById(name.getId())) {
			throw new AlreadyExistsException("Name id: " + name.getId());
		}
		// Make sure the name starts with a capital and that the rest is lowercase
		String localName = name.getName().substring(0, 1).toUpperCase()
				+ name.getName().substring(1, name.getName().length()).toLowerCase();
		name.setName(localName);
		return nameRepo.save(name);
	}

	@GetMapping("/anummers")
	public Anummer getAnummer(@RequestParam(value = "startFrom", required = false) Long startFrom,
			@RequestParam(value = "maxIterations", required = false) Long maxIiterations,
			@RequestParam(value = "anummer", required = false) Long anummer,
			@RequestParam(value = "firstAvailable", required = false) Boolean firstAvailable) {

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
		} else if (firstAvailable != null && firstAvailable) {
			// Return the first available A nummer (where gemeenteCode == 0) from the store
			LOG.info("Get first available A nummer");
			Iterator<com.reerinkresearch.anummers.model.Anummer> it = anummerRepo.findAll().iterator();
			while (it.hasNext()) {
				com.reerinkresearch.anummers.model.Anummer p = it.next();
				LOG.debug("A nummer: " + p.getAnummer() + ", " + p.getGemeenteCode());
				if (p.getGemeenteCode() == null || p.getGemeenteCode() == 0) {
					a = new Anummer(p.getAnummer(), 0);
					return a;
				}
			}
			throw new NotFoundException("Geen vrij A nummer gevonden");
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
			// Update if the persisted gemeenteCode == 0
			Optional<com.reerinkresearch.anummers.model.Anummer> p = anummerRepo.findById(a.getAnummer());
			if (p.isPresent() && p.get().getGemeenteCode() != null && p.get().getGemeenteCode() == 0) {
				p.get().setGemeenteCode(a.getGemeenteCode());
				anummerRepo.save(p.get());
			} else {
				throw new AlreadyExistsException("Anummer " + a.getAnummer());
			}
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
	public PersoonsLijst generatePLAndStoreAnummer() {
		// Haal een random adres op en gebruik de bijbehorende gemeente code
		Adres addr = this.addressRepo.getRandomAddress();
		LOG.info("Random address gemeenteCode: " + addr.getGemeenteCode());

		// Eerst beschikbare A nummer ophalen
		Anummer a = this.generateAnummer();
		if (!a.isValid()) {
			throw new InvalidAnummerException(a);
		}
		// Store the A nummer for this gemeente
		a.setGemeenteCode(addr.getGemeenteCode());
		this.storeAnummer(a);

		// Namen ophalen: voornamen 1-4, voorvoegsel 0-2, geslachtsnaam 1-3
		String voornamen = this.generateNames(1, 4);
		String geslachtsnaam = this.generateNames(1, 3);
		String voorvoegsel = this.generateVoorvoegsel(0, 2);

		// PL genereren
		PersoonsLijst pl = new PersoonsLijst(a.getAnummer(), geslachtsnaam, addr.getGemeenteCode());
		pl.getPersoon().get(0).getNaam().setVoornamen(voornamen);
		pl.getPersoon().get(0).getNaam().setVoorvoegsel(voorvoegsel);
		pl.getPersoon().get(0).setGeboorte(new Geboorte(new Datum("20210315"), "" + addr.getGemeenteCode(), "0000"));
		pl.getPersoon().get(0).setGeslacht(new Geslacht(this.generateGeslacht()));
		
		pl.getVerblijfplaats().get(0).setAdres(addr);
		return pl;
	}

	private Anummer generateAnummer() {
		try {
			Anummer a = this.getAnummer(null, null, null, true);
			return a;
		} catch (Exception e) {
			LOG.warn("No available A-nummer, generate a new one");
		}
		boolean foundNewValidAnummer = false;
		long anummer = 0;
		while (!foundNewValidAnummer) {
			Anummer a = this.getAnummer(anummer == 0 ? null : anummer, ANUMMER_GENERATE_MAXITERATIONS, null, null);
			if (a.isValid()) {
				// Check if it already exists
				try {
					// Ignore the result
					this.getAnummer(null, null, a.getAnummer(), null);
				} catch (Exception e) {
					// Not found is OK, other exceptions are not :)
					return a;
				}
			}
			anummer = a.getSkipTo();
		}
		return null;
	}

	private String generateVoorvoegsel(int min, int max) {
		StringBuffer buf = new StringBuffer();
		String[] voorvoegsels = { "a", "'t", "auf", "den", "der", "am", "bij", "da", "del", "du", "de", "van", "der",
				"den", "tot" };

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
	
	private String generateGeslacht() {
		String[] geslacht = {"M", "V", "O"};
		int rand = this.getRandomNumber(0, geslacht.length - 1);
		
		return geslacht[rand];
	}

	public static void main(String[] args) {
		SpringApplication.run(AnummersApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

}
