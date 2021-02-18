package com.reerinkresearch.lo3pl.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.reerinkresearch.lo3pl.model.PersoonsLijstWrapper;

@Repository
public interface PersoonsLijstRepository extends CrudRepository<PersoonsLijstWrapper, String> {
	
	List<PersoonsLijstWrapper> findByAnummer(long anummer);
	
	//List<PersoonsLijstWrapper> findByGeslachtsnaam (String geslachtsnaam);
	
	List<PersoonsLijstWrapper> findByGeslachtsnaamStartingWith(String geslachtsnaam);
}
