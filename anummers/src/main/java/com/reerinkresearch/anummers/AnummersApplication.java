package com.reerinkresearch.anummers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
import com.reerinkresearch.anummers.model.AllocatedAnummer;
import com.reerinkresearch.anummers.repo.AddressRepository;
import com.reerinkresearch.anummers.repo.NameRepository;
import com.reerinkresearch.anummers.service.AnummerService;
import com.reerinkresearch.pl.Adres;
import com.reerinkresearch.pl.Anummer;
import com.reerinkresearch.pl.Datum;
import com.reerinkresearch.pl.Geboorte;
import com.reerinkresearch.pl.Geslacht;
import com.reerinkresearch.pl.PersoonsLijst;
import com.reerinkresearch.pl.util.Util;

@SpringBootApplication
@RestController
public class AnummersApplication implements CommandLineRunner {

	private static final long FIRST_ANUMMER = 1010101025L;

	private static final long ANUMMER_GENERATE_MAXITERATIONS = 100000L;

	private static final Logger LOG = LoggerFactory.getLogger(AnummersApplication.class);

	@Autowired
	AnummerService anummerService;

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
	public AllocatedAnummer getAnummer(@RequestParam(value = "startFrom", required = false) Long startFrom,
			@RequestParam(value = "maxIterations", required = false) Long maxIiterations,
			@RequestParam(value = "anummer", required = false) Long anummer,
			@RequestParam(value = "firstAvailable", required = false) Boolean firstAvailable) {

		// Check the repo for the existence of the A-number

		if (anummer != null) {
			if (startFrom != null) {
				throw new BadRequestException("Specify anummer or startFrom");
			}
			if (maxIiterations != null) {
				throw new BadRequestException("Specify anummer or maxIiterations");
			}
			int gemeenteCode = this.anummerService.getGemeenteCode(anummer);
			if (gemeenteCode == 0) {
				throw new NotFoundException("A nummer: " + anummer);
			}
			return new AllocatedAnummer(anummer, gemeenteCode);
		} else if (firstAvailable != null && firstAvailable) {
			// Return the first available A nummer (where gemeenteCode == 0) from the store
			LOG.info("Get first available A nummer");
			anummer = this.anummerService.popUnallocatedAnummer();
			if (anummer > 0) {
				return new AllocatedAnummer(anummer, 0);
			}
			throw new NotFoundException("Geen vrij A nummer gevonden");
		} else {
			if (startFrom == null) {
				startFrom = FIRST_ANUMMER;
			}
			if (maxIiterations == null) {
				maxIiterations = 1L;
			}
			Anummer n = iterateToFindAnumber(startFrom, maxIiterations);
			if (n.isValid()) {
				return new AllocatedAnummer(n.getAnummer(), 0);
			}
		}
		return null;
	}

	private Anummer iterateToFindAnumber(long startFrom, long maxIiterations) {
		Anummer n = new Anummer(startFrom);
		long iter = 0L;
		while (!n.isValid() && iter < maxIiterations) {
			long skipTo = n.getSkipTo();
			n = new Anummer(skipTo);
			iter++;
		}
		return n;
	}

	@PostMapping("/anummers")
	public AllocatedAnummer storeAnummer(@RequestBody AllocatedAnummer a) {
		if (a.getAnummer() == null || a.getGemeenteCode() == null || a.getGemeenteCode() == 0) {
			throw new BadRequestException("Specify anummer and gemeenteCode (>0)");
		}

		// Check if we received a valid A number
		Anummer c = new Anummer(a.getAnummer());
		if (!c.isValid()) {
			throw new InvalidAnummerException(c);
		}

		// Check if we have it already in the repository
		int gemeenteCode = this.anummerService.getGemeenteCode(a.getAnummer());
		if (gemeenteCode == 0) {
			// Store it in the database
			this.anummerService.storeAnummer(a.getAnummer(), a.getGemeenteCode());
		} else {
			throw new AlreadyExistsException("Anummer " + a.getAnummer());
		}
		return a;
	}

	@DeleteMapping("/anummers")
	public long deleteAllAnummers() {
		return this.anummerService.deleteAll();
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
		AllocatedAnummer a = this.generateAnummer();

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
		String gebDatum = this.generateGeboorteDatum();
		pl.getPersoon().get(0).setGeboorte(
				new Geboorte(new Datum(gebDatum), Util.zeroPrefix(addr.getGemeenteCode(), 4), Util.zeroPrefix(0, 4)));
		pl.getPersoon().get(0).setGeslacht(new Geslacht(this.generateGeslacht()));

		pl.getVerblijfplaats().get(0).setAdres(addr);
		return pl;
	}

	private String generateGeboorteDatum() {
		int year = getRandomNumber(1970, 2020);
		int month = getRandomNumber(1, 12);
		int day = getRandomNumber(1, 31);

		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, day);

		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

		return format.format(c.getTime());
	}

	private AllocatedAnummer generateAnummer() {
		long free = this.anummerService.popUnallocatedAnummer();
		LOG.info("Free A nummer: " + (free > 0 ? free : "not found"));

		if (free > 0) {
			return new AllocatedAnummer(free, 0);
		}

		// Generate a fresh one, start from the last (highest) anummer
		long lastAnummer = this.anummerService.getLastAnummer();
		boolean stopSearching = false;
		long anummer = (lastAnummer > 0 ? lastAnummer + 1 : FIRST_ANUMMER);
		while (!stopSearching) {
			Anummer a = iterateToFindAnumber(anummer, ANUMMER_GENERATE_MAXITERATIONS);
			if (a.isValid()) {
				// Check if it was already allocated
				int gemeenteCode = this.anummerService.getGemeenteCode(a.getAnummer());
				if (gemeenteCode == 0) {
					LOG.info("New unallocated Anummer generated: " + a.getAnummer());
					return new AllocatedAnummer(a.getAnummer(), 0);
				} else {
					LOG.info("Anummer " + a.getAnummer() + " already allocated to " + gemeenteCode
							+ ", generating a new one");
				}
			}
			// Stop if we're at the end of the anummer range
			if (a.getErrorCode() == 99) {
				stopSearching = true;
			} else {
				// Try the suggested next value
				anummer = a.getSkipTo();
			}
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
		String[] geslacht = { "M", "V", "O" };
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

	@Override
	public void run(String... args) throws Exception {
		int waitSec = 10;
		LOG.info("Start generating Anummers after " + waitSec + " seconds");
		Thread.sleep(waitSec * 1000L);
		// Use the runner to generate Anummers in a relaxed tempo
		long lastAnummer = this.anummerService.getLastAnummer();
		long anummer = (lastAnummer > 0 ? lastAnummer + 1 : FIRST_ANUMMER);
		LOG.info("Start generating Anummers from " + anummer);
		boolean isDone = false;
		while (!isDone) {
			Anummer a = iterateToFindAnumber(anummer, ANUMMER_GENERATE_MAXITERATIONS);
			if (a.isValid()) {
				// Check if the anummer is allocated
				int gemeenteCode = this.anummerService.getGemeenteCode(a.getAnummer());
				if (gemeenteCode == 0) {
					// No, so store it in the free list
					this.anummerService.storeAnummer(a.getAnummer());
				}
			}
			// Stop if we're at the end of the anummer range
			if (a.getErrorCode() == 99) {
				isDone = true;
			} else {
				// Try the suggested next value
				anummer = a.getSkipTo();
			}
		}
		LOG.info("Finished generating Anummers, last: " + anummer);
	}
}
