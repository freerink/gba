package com.reerinkresearch.anummers.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.reerinkresearch.anummers.model.AllocatedAnummer;

@Repository
public interface AnummerRepository extends CrudRepository<AllocatedAnummer, Long> {}
