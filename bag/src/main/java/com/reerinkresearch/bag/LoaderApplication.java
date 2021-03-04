package com.reerinkresearch.bag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoaderApplication implements CommandLineRunner {

	@Value("${sleepMillis}")
	private long sleepMillis;
	
	private static Logger LOG = LoggerFactory.getLogger(LoaderApplication.class);

	public static void main(String[] args) {
		LOG.info("Starting the LoaderApplication");
		SpringApplication.run(LoaderApplication.class, args);
		LOG.info("LoaderApplication finished");
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("The runner");
		for (int i = 0; i < args.length; ++i) {
			LOG.info("args[{}]: {}", i, args[i]);
		}
		LOG.info("Sleeping for " + this.sleepMillis + " millis");
		Thread.sleep(this.sleepMillis);
	}

}
