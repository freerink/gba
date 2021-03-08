package com.reerinkresearch.bag.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.reerinkresearch.bag.model.NummerAanduiding;
import com.reerinkresearch.bag.service.NummerAanduidingService;

public class NummerAanduidingHandler extends DefaultHandler {
	private static final String HISTORIE_EIND_GELDIGHEID = "Historie:eindGeldigheid";

	private static final String OBJECTEN_NUMMERAANDUIDING = "Objecten:Nummeraanduiding";

	private static final String OBJECTEN_POSTCODE = "Objecten:postcode";

	private static final String OBJECTEN_HUISLETTER = "Objecten:huisletter";

	private static final String OBJECTEN_HUISNUMMER = "Objecten:huisnummer";

	private static final String OBJECTEN_REF_OPENBARE_RUIMTE_REF = "Objecten-ref:OpenbareRuimteRef";

	private static final String OBJECTEN_IDENTIFICATIE = "Objecten:identificatie";

	private static final String SL_STAND_BESTAND = "sl:standBestand";

	private static Logger LOG = LoggerFactory.getLogger(NummerAanduidingHandler.class);

	boolean isId = false;
	boolean isRef = false;
	boolean isHuisNummer = false;
	boolean isHuisLetter = false;
	boolean isPostCode = false;
	boolean hasEndDate = false;

	long nummerAanduidingId;
	long openbareRuimteRef;
	int huisNummer;
	String huisLetter;
	String postCode;

	int numNummerAanduidingen = 0;

	NummerAanduidingService nummerAanduidingService;

	public NummerAanduidingHandler(NummerAanduidingService nummerAanduidingService) {
		this.nummerAanduidingService = nummerAanduidingService;
	}

	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals(OBJECTEN_NUMMERAANDUIDING)) {
			hasEndDate = false;
		}
		if (qName.equals(OBJECTEN_IDENTIFICATIE)) {
			numNummerAanduidingen++;
			isId = true;
		}
		if (qName.equals(OBJECTEN_HUISNUMMER)) {
			isHuisNummer = true;
		}
		if (qName.equals(OBJECTEN_HUISLETTER)) {
			isHuisLetter = true;
		}
		if (qName.equals(OBJECTEN_POSTCODE)) {
			isPostCode = true;
		}
		if (qName.equals(HISTORIE_EIND_GELDIGHEID)) {
			hasEndDate = true;
		}
		if (qName.equals(OBJECTEN_REF_OPENBARE_RUIMTE_REF)) {
			isRef = true;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (qName.equals(OBJECTEN_IDENTIFICATIE)) {
			isId = false;
		}
		if (qName.equals(OBJECTEN_HUISNUMMER)) {
			isHuisNummer = false;
		}
		if (qName.equals(OBJECTEN_HUISLETTER)) {
			isHuisLetter = false;
		}
		if (qName.equals(OBJECTEN_POSTCODE)) {
			isPostCode = false;
		}
		if (qName.equals(OBJECTEN_REF_OPENBARE_RUIMTE_REF)) {
			isRef = false;
		}
		// We're done with this nummer aanduiding object
		if (qName.equals(OBJECTEN_NUMMERAANDUIDING)) {
			if (!hasEndDate) {
				// Store it
				LOG.debug("Nummer aanduiding: " + nummerAanduidingId + "=" + postCode + " " + huisNummer
						+ (huisLetter != null ? huisLetter : ""));
				this.nummerAanduidingService.store(
						new NummerAanduiding(nummerAanduidingId, postCode, huisNummer, huisLetter, openbareRuimteRef));
			}
			// Reset this one as it is optional (the rest get overwritten for every object)
			huisLetter = null;
		}
		// We're done with the file
		if (qName.equals(SL_STAND_BESTAND)) {
			LOG.info("Number of Nummer Aanduidingen: " + numNummerAanduidingen);
		}
	}

	public void characters(char ch[], int start, int length) {
		String raw = new String(ch, start, length);
		if (isId) {
			nummerAanduidingId = Long.parseLong(raw);
		}
		if (isHuisNummer) {
			huisNummer = Integer.parseInt(raw);
		}
		if (isHuisLetter) {
			huisLetter = raw;
		}
		if (isPostCode) {
			postCode = raw;
		}
		if (isRef) {
			openbareRuimteRef = Long.parseLong(raw);
		}
	}

}
