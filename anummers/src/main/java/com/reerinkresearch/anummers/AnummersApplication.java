package com.reerinkresearch.anummers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reerinkresearch.anummers.model.Name;
import com.reerinkresearch.anummers.repo.AnummerRepository;
import com.reerinkresearch.anummers.repo.NameRepository;

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
			if( foundName.isPresent() ) {
				names.add(foundName.get());
			} else {
				throw new NotFoundException("Name with id:" + id);
			}
		} else if (isCount != null && isCount) {
			// Return the last index
			long count = nameRepo.count();
			Optional<Name> foundName = nameRepo.findById((int) count - 1);
			if( foundName.isPresent() ) {
				names.add(foundName.get());
			} else {
				throw new NotFoundException("Name with id:" + id);
			}
		} else {
			long count = nameRepo.count();
			int random = getRandomNumber(0, (int) count - 1);
			Optional<Name> foundName = nameRepo.findById(random);
			if( foundName.isPresent() ) {
				names.add(foundName.get());
			} else {
				throw new NotFoundException("Name with id:" + id);
			}
		}
		return names;
	}

	private int getRandomNumber(int min, int max) {
	    return (int) ((Math.random() * (max - min)) + min);
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
	Anummer storeAnummer(@RequestBody Anummer a) {
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

	public static void main(String[] args) {
		SpringApplication.run(AnummersApplication.class, args);
	}

}
