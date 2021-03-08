package com.reerinkresearch.bag.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.reerinkresearch.bag.service.GemeenteWoonplaatsService;

public class WoonplaatsHandler extends DefaultHandler {

	private static final String OBJECTEN_WOONPLAATS = "Objecten:Woonplaats";

	private static final String HISTORIE_EIND_GELDIGHEID = "Historie:eindGeldigheid";

	private static final String HISTORIE_VOORKOMEN = "Historie:Voorkomen";

	private static final String OBJECTEN_NAAM = "Objecten:naam";

	private static final String OBJECTEN_IDENTIFICATIE = "Objecten:identificatie";

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
		if (qName.equals(OBJECTEN_IDENTIFICATIE)) {
			isId = true;
		}
		if (qName.equals(OBJECTEN_NAAM)) {
			isWoonplaatsnaam = true;
		}
		if (qName.equals(HISTORIE_VOORKOMEN)) {
			hasEindDatum = false;
		}
		if (qName.equals(HISTORIE_EIND_GELDIGHEID)) {
			hasEindDatum = true;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (qName.equals(OBJECTEN_IDENTIFICATIE)) {
			isId = false;
		}
		if (qName.equals(OBJECTEN_NAAM)) {
			isWoonplaatsnaam = false;
			woonplaatsNaamCount = 0;
		}
		if (qName.equals(OBJECTEN_WOONPLAATS) && !hasEindDatum) {
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
