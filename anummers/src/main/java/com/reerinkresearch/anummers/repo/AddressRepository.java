package com.reerinkresearch.anummers.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.reerinkresearch.pl.Adres;

@Repository
public class AddressRepository {

	@Value("${addressService.url}")
	private String addressServiceUrl;

	@Autowired
	private RestTemplate restTemplate;
	
	public Adres getRandomAddress() {

		Adres a = this.restTemplate.getForObject(addressServiceUrl, Adres.class);
		
		return a;
	}
	
}
