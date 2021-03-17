package com.reerinkresearch.anummers.repo;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.reerinkresearch.anummers.model.FreeAnummer;

@Repository
public interface FreeAnummerRepository extends PagingAndSortingRepository<FreeAnummer, Long> {}
