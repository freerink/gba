package com.reerinkresearch.lo3pl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reerinkresearch.lo3pl.model.PersoonsLijst;

@SpringBootApplication
@RestController
public class LO3PLApplication {

	@GetMapping("/persoonslijsten")
	public PersoonsLijst getPL(@RequestParam(value = "anummer", required = true) Long anummer) {
		return null;
	}
	
	public static void main(String[] args) {
		SpringApplication.run(LO3PLApplication.class, args);
	}
}
