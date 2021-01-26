package com.reerinkresearch.anummers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@SpringBootApplication
@RestController
public class AnummersApplication {

	private final AtomicLong counter = new AtomicLong();
	
	@GetMapping("/anummers")
	public Anummer getAnummer(
			@RequestParam(value = "startFrom", required=false) Long startFrom,
			@RequestParam(value = "maxIterations", required=false) Long maxIiterations) {
		if( startFrom == null ) {
			startFrom = 1010101025L;
		}
		if( maxIiterations == null ) {
			maxIiterations = 1L;
		}
		Anummer a = new Anummer(counter.incrementAndGet(), startFrom);
		long iter = 0L;
		while( !a.isValid() && iter < maxIiterations ) {
			long skipTo = a.getSkipTo();
			a = new Anummer(counter.incrementAndGet(), skipTo);
			iter++;
		}
		return a;
	}

	public static void main(String[] args) {
		SpringApplication.run(AnummersApplication.class, args);
	}

}
