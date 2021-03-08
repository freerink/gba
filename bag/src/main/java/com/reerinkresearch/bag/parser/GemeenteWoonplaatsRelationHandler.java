package com.reerinkresearch.bag.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.reerinkresearch.bag.service.GemeenteWoonplaatsService;


public class GemeenteWoonplaatsRelationHandler extends DefaultHandler {

	private static Logger LOG = LoggerFactory.getLogger(GemeenteWoonplaatsRelationHandler.class);

	GemeenteWoonplaatsService service;

	boolean hasEindDatum = false;
	boolean isWoonplaats = false;
	boolean isGemeente = false;
	boolean isId = false;

	int woonplaats, gemeente;

	public GemeenteWoonplaatsRelationHandler(GemeenteWoonplaatsService service) {
		this.service = service;
	}
	
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
				LOG.debug("Store relation: gemeente=" + gemeente + ", woonplaats=" + woonplaats);
				if( !this.service.store(gemeente, woonplaats)) {
					LOG.error("Error storing relation: gemeente=" + gemeente + ", woonplaats=" + woonplaats);
				}
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

}
