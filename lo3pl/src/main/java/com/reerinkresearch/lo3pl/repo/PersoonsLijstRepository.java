package com.reerinkresearch.lo3pl.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import com.reerinkresearch.lo3pl.model.PersoonsLijst;

@Repository
public interface PersoonsLijstRepository extends CrudRepository<PersoonsLijst, Long>, QueryByExampleExecutor<PersoonsLijst> {}
