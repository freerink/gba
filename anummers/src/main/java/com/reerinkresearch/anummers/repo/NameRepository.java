package com.reerinkresearch.anummers.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.reerinkresearch.anummers.model.Name;

@Repository
public interface NameRepository extends CrudRepository<Name, Integer> {}
