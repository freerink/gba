package com.reerinkresearch.bag.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.reerinkresearch.bag.model.OpenbareRuimte;
import com.reerinkresearch.bag.model.Woonplaats;
import com.reerinkresearch.bag.service.GemeenteWoonplaatsService;
import com.reerinkresearch.bag.service.OpenbareRuimteService;

public class OpenbareRuimteHandler extends DefaultHandler {
	private static final String HISTORIE_EIND_GELDIGHEID = "Historie:eindGeldigheid";
	private static final String HISTORIE_VOORKOMEN = "Historie:Voorkomen";
	private static final String OBJECTEN_NAAM = "Objecten:naam";
	private static final String SL_STAND_BESTAND = "sl:standBestand";
	private static final String OBJECTEN_OPENBARE_RUIMTE = "Objecten:OpenbareRuimte";
	private static final String OBJECTEN_REF_WOONPLAATS_REF = "Objecten-ref:WoonplaatsRef";
	private static final String OBJECTEN_LIGT_IN = "Objecten:ligtIn";
	private static final String OBJECTEN_IDENTIFICATIE = "Objecten:identificatie";

	private static Logger LOG = LoggerFactory.getLogger(OpenbareRuimteHandler.class);

	boolean isId = false;
	boolean isNaam = false;
	boolean isLigtIn = false;
	boolean isWoonplaatsRef = false;
	boolean hasEndDate = false;
	int numWoonplaatsRefs = 0;
	int numOpenbareRuimten = 0;
	long openbareRuimteId;
	String openbareRuimteName;
	int woonplaatsRef;

	GemeenteWoonplaatsService gemeenteWoonplaatsService;
	OpenbareRuimteService openbareRuimteService;

	public OpenbareRuimteHandler(GemeenteWoonplaatsService gemeenteWoonplaatsService,
			OpenbareRuimteService openbareRuimteService) {
		this.gemeenteWoonplaatsService = gemeenteWoonplaatsService;
		this.openbareRuimteService = openbareRuimteService;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals(OBJECTEN_OPENBARE_RUIMTE)) {
			numOpenbareRuimten++;
		}
		if (qName.equals(OBJECTEN_IDENTIFICATIE)) {
			isId = true;
		}
		if (qName.equals(OBJECTEN_LIGT_IN)) {
			isLigtIn = true;
			numWoonplaatsRefs = 0;
		}
		if (qName.equals(OBJECTEN_REF_WOONPLAATS_REF)) {
			numWoonplaatsRefs++;
			isWoonplaatsRef = true;
		}
		if (qName.equals(OBJECTEN_NAAM)) {
			isNaam = true;
		}
		if (qName.equals(HISTORIE_VOORKOMEN)) {
			hasEndDate = false;
		}
		if (qName.equals(HISTORIE_EIND_GELDIGHEID)) {
			hasEndDate = true;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (qName.equals(OBJECTEN_IDENTIFICATIE)) {
			isId = false;
		}
		if (qName.equals(OBJECTEN_LIGT_IN)) {
			isLigtIn = false;
			if (numWoonplaatsRefs > 1) {
				LOG.info("Found OPR with multiple woonplaats refs: " + numWoonplaatsRefs);
			}
		}
		if (qName.equals(OBJECTEN_REF_WOONPLAATS_REF)) {
			isWoonplaatsRef = false;
		}
		if (qName.equals(OBJECTEN_NAAM)) {
			isNaam = false;
		}
		if (qName.equals(SL_STAND_BESTAND)) {
			LOG.info("Number of OPR: " + numOpenbareRuimten);
		}
		if (qName.equals(OBJECTEN_OPENBARE_RUIMTE) && !hasEndDate) {
			// We have all data
			// Get the woonplaats name
			Woonplaats w = this.gemeenteWoonplaatsService.getWoonplaats(woonplaatsRef);
			LOG.debug("Woonplaats: " + w.getNaam() + ", OPR id: " + openbareRuimteId + ", name: " + openbareRuimteName);
			OpenbareRuimte opr = new OpenbareRuimte(openbareRuimteId, woonplaatsRef, openbareRuimteName);
			if(!this.openbareRuimteService.store(opr)) {
				LOG.error("Error storing Woonplaats: " + w.getNaam() + ", OPR id: " + openbareRuimteId + ", name: " + openbareRuimteName);
			}
		}
	}

	public void characters(char ch[], int start, int length) {
		String raw = new String(ch, start, length);
		if (isId) {
			openbareRuimteId = Long.parseLong(raw);
		}
		if (isNaam) {
			openbareRuimteName = raw;
		}
		if (isWoonplaatsRef) {
			woonplaatsRef = Integer.parseInt(raw);
		}
	}
}
