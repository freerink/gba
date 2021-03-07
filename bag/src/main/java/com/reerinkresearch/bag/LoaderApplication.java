package com.reerinkresearch.bag;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.reerinkresearch.bag.parser.GemeenteWoonplaatsRelationHandler;
import com.reerinkresearch.bag.service.GemeenteWoonplaatsService;

@SpringBootApplication
public class LoaderApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(LoaderApplication.class);

	@Autowired
	GemeenteWoonplaatsService gemeenteWoonplaatsService;
	
	@Value("${gemeenteWoonplaatsRelationsFile}")
	private String gemeenteWoonplaatsRelationsFile;

	public static void main(String[] args) {
		LOG.info("Starting the LoaderApplication");
		SpringApplication.run(LoaderApplication.class, args);
		LOG.info("LoaderApplication finished");
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("Parse and store the BAG data");
		for (int i = 0; i < args.length; ++i) {
			LOG.info("args[{}]: {}", i, args[i]);
		}

		// Read an XML file
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		GemeenteWoonplaatsRelationHandler handler = new GemeenteWoonplaatsRelationHandler(this.gemeenteWoonplaatsService);
		saxParser.parse(this.gemeenteWoonplaatsRelationsFile, handler);

		
		LOG.info("Aantal gemeenten: " + this.gemeenteWoonplaatsService.getGemeenteCount());
		LOG.info("Aantal woonplaatsen: " + this.gemeenteWoonplaatsService.getWoonplaatsCount());
	}

}
