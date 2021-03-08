package com.reerinkresearch.bag.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.reerinkresearch.bag.service.GemeenteWoonplaatsService;

public class WoonplaatsHandler extends DefaultHandler {

	private static Logger LOG = LoggerFactory.getLogger(WoonplaatsHandler.class);

	GemeenteWoonplaatsService service;

	boolean hasEindDatum = false;
	boolean isWoonplaatsnaam = false;
	int woonplaatsNaamCount = 0;
	boolean isId = false;

	int woonplaatsCode;
	String woonplaatsNaam;

	public WoonplaatsHandler(GemeenteWoonplaatsService service) {
		this.service = service;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		LOG.debug("startElement: " + qName);
		if (qName.endsWith("Objecten:identificatie")) {
			isId = true;
		}
		if (qName.endsWith("Objecten:naam")) {
			isWoonplaatsnaam = true;
		}
		if (qName.endsWith("Historie:Voorkomen")) {
			hasEindDatum = false;
		}
		if (qName.endsWith("Historie:eindGeldigheid")) {
			hasEindDatum = true;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (qName.endsWith("Objecten:identificatie")) {
			isId = false;
		}
		if (qName.endsWith("Objecten:naam")) {
			isWoonplaatsnaam = false;
			woonplaatsNaamCount = 0;
		}
		if (qName.endsWith("Objecten:Woonplaats") && !hasEindDatum) {
			// We have all data, find the woonplaats an update it
			LOG.info("Update woonplaats: " + woonplaatsCode + " with name: " + woonplaatsNaam);
			if (!this.service.updateWoonplaats(woonplaatsCode, woonplaatsNaam)) {
				LOG.error("Error updating woonplaats: " + woonplaatsCode + " with name: " + woonplaatsNaam);
			}
		}
	}

	public void characters(char ch[], int start, int length) {
		if (isId) {
			String id = new String(ch, start, length);
			woonplaatsCode = Integer.parseInt(id);
			LOG.debug("isId: " + id + ", " + woonplaatsCode);
		}
		if (isWoonplaatsnaam) {
			if (woonplaatsNaamCount == 0) {
				woonplaatsNaam = new String(ch, start, length);
			} else {
				woonplaatsNaam += new String(ch, start, length);
				LOG.info("characters for woonplaatsCode: " + woonplaatsCode + ", ch[0]:" + (int) ch[start] + ", start: "
						+ start + ", len: " + length);
			}
			woonplaatsNaamCount++;
		}
	}
}
