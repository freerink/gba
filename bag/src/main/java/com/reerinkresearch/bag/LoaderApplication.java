package com.reerinkresearch.bag;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

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

		// Read an XML file
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		DefaultHandler handler = new DefaultHandler() {

			boolean hasEindDatum = false;
			boolean isWoonplaats = false;
			boolean isGemeente = false;
			boolean isId = false;

			int woonplaats, gemeente;

			public void startElement(String uri, String localName, String qName, Attributes attributes) {
				if (qName.endsWith("begindatumTijdvakGeldigheid")) {
					hasEindDatum = false;
				}
				if (qName.endsWith("einddatumTijdvakGeldigheid")) {
					hasEindDatum = true;
				}
				if (qName.endsWith("gerelateerdeWoonplaats")) {
					isWoonplaats = true;
				}
				if (qName.endsWith("gerelateerdeGemeente")) {
					isGemeente = true;
				}
				if (qName.endsWith("identificatie")) {
					isId = true;
				}
			}

			public void endElement(String uri, String localName, String qName) {
				if (qName.endsWith("gerelateerdeWoonplaats")) {
					isWoonplaats = false;
				}
				if (qName.endsWith("gerelateerdeGemeente")) {
					isGemeente = false;
				}
				if (qName.endsWith("identificatie")) {
					isId = false;
				}
				if (qName.endsWith("GemeenteWoonplaatsRelatie")) {
					if (!hasEindDatum) {
						// We hebben alles, nu opslaan
						LOG.info("Store relation: gemeente=" + gemeente + ", woonplaats=" + woonplaats);
					}
				}
			}

			public void characters(char ch[], int start, int length) {
				if (isId) {
					if (isWoonplaats) {
						String id = new String(ch, start, length);
						woonplaats = Integer.parseInt(id);
					}
					if (isGemeente) {
						String id = new String(ch, start, length);
						gemeente = Integer.parseInt(id);
					}
				}
			}
		};

		saxParser.parse(args[0], handler);

		LOG.info("Sleeping for " + this.sleepMillis + " millis");
		Thread.sleep(this.sleepMillis);
	}

}
