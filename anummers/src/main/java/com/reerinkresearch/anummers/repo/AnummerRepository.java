package com.reerinkresearch.anummers.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.reerinkresearch.anummers.model.Anummer;

@Repository
public interface AnummerRepository extends CrudRepository<Anummer, Long> {}
