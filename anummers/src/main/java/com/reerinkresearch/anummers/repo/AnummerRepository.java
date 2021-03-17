package com.reerinkresearch.anummers.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.reerinkresearch.anummers.model.StoredAnummer;

@Repository
public interface AnummerRepository extends CrudRepository<StoredAnummer, Long> {}
