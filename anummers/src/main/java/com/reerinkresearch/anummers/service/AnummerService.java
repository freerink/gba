package com.reerinkresearch.anummers.service;

import java.util.Iterator;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.reerinkresearch.anummers.AlreadyExistsException;
import com.reerinkresearch.anummers.model.AllocatedAnummer;
import com.reerinkresearch.anummers.model.FreeAnummer;
import com.reerinkresearch.anummers.repo.AnummerRepository;
import com.reerinkresearch.anummers.repo.FreeAnummerRepository;

@Service
public class AnummerService {

	private static final Logger LOG = LoggerFactory.getLogger(AnummerService.class);

	@Autowired
	AnummerRepository anummerRepo;

	@Autowired
	FreeAnummerRepository freeAnummerRepo;

	/**
	 * Add an unallocated anummer to the pool (unless is already exists).
	 * 
	 * @param anummer
	 */
	public void storeAnummer(long anummer) {
		if (!this.freeAnummerRepo.existsById(anummer)) {
			this.freeAnummerRepo.save(new FreeAnummer(anummer));
		} else {
			throw new AlreadyExistsException("Anummer " + anummer + " already exists in the free pool.");
		}
	}

	/**
	 * Add an allocated anummer to the list (unless it already exists).
	 * 
	 * @param anummer
	 * @param gemeenteCode
	 */
	public void storeAnummer(long anummer, int gemeenteCode) {
		Optional<AllocatedAnummer> result = this.anummerRepo.findById(anummer);
		if (result.isEmpty()) {
			this.anummerRepo.save(new AllocatedAnummer(anummer, gemeenteCode));
		} else {
			throw new AlreadyExistsException(
					"Anummer " + anummer + " is already allocated for gemeente " + result.get().getGemeenteCode());
		}
	}

	/**
	 * Pop the first unallocated anummer for the pool.
	 * 
	 * @return the popped anummer or 0 if there are no more unallocated anummers in
	 *         the pool.
	 */
	public long popUnallocatedAnummer() {
		Pageable page = PageRequest.of(0, 1) ;
		
		Iterator<FreeAnummer> all = this.freeAnummerRepo.findAll(page).iterator();
		if (all.hasNext()) {
			FreeAnummer free = all.next();
			LOG.info("Found free anummer " + free.getAnummer());
			this.freeAnummerRepo.deleteById(free.getAnummer());
			LOG.info("Popped " + free.getAnummer());
			return free.getAnummer();
		}
		return 0L;
	}

	/**
	 * Return the last unallocated or allocated anummer (usefull in resuming anummer
	 * generation)
	 * 
	 * @return
	 */
	public long getLastAnummer() {
		// Find the anummer with the highest value
		Iterator<FreeAnummer> allFree = this.freeAnummerRepo.findAll().iterator();
		long lastAnummer = 0;
		while (allFree.hasNext()) {
			FreeAnummer free = allFree.next();
			if (free.getAnummer() > lastAnummer) {
				lastAnummer = free.getAnummer();
			}
		}
		Iterator<AllocatedAnummer> allAllocated = this.anummerRepo.findAll().iterator();
		while (allAllocated.hasNext()) {
			AllocatedAnummer allocated = allAllocated.next();
			if (allocated.getAnummer() > lastAnummer) {
				lastAnummer = allocated.getAnummer();
			}
		}
		return lastAnummer;
	}

	/**
	 * If the anummer is allocated to a gemeente, return the gemeenteCode;
	 * 
	 * @param anummer
	 * @return the gemeenteCode or 0 if the anummer is not allocated
	 */
	public int getGemeenteCode(long anummer) {
		Optional<AllocatedAnummer> result = this.anummerRepo.findById(anummer);
		if (result.isPresent()) {
			return result.get().getGemeenteCode();
		}
		return 0;
	}

	/**
	 * Delete allocated and free Anummers.
	 * 
	 * @return the deleted number of Anummers.
	 */
	public long deleteAll() {
		// Delete allocated anummers
		long count = this.anummerRepo.count();
		this.anummerRepo.deleteAll();

		count += this.freeAnummerRepo.count();
		this.freeAnummerRepo.deleteAll();
		return count;
	}
}
