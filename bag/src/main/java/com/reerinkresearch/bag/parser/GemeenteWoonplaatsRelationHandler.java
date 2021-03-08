package com.reerinkresearch.bag.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.reerinkresearch.bag.service.GemeenteWoonplaatsService;


public class GemeenteWoonplaatsRelationHandler extends DefaultHandler {

	private static final String GWR_PRODUCT_GEMEENTE_WOONPLAATS_RELATIE = "gwr-product:GemeenteWoonplaatsRelatie";

	private static final String GWR_PRODUCT_IDENTIFICATIE = "gwr-product:identificatie";

	private static final String GWR_PRODUCT_GERELATEERDE_GEMEENTE = "gwr-product:gerelateerdeGemeente";

	private static final String GWR_PRODUCT_GERELATEERDE_WOONPLAATS = "gwr-product:gerelateerdeWoonplaats";

	private static final String BAGTYPES_EINDDATUM_TIJDVAK_GELDIGHEID = "bagtypes:einddatumTijdvakGeldigheid";

	private static final String BAGTYPES_BEGINDATUM_TIJDVAK_GELDIGHEID = "bagtypes:begindatumTijdvakGeldigheid";

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
		if (qName.equals(BAGTYPES_BEGINDATUM_TIJDVAK_GELDIGHEID)) {
			hasEindDatum = false;
		}
		if (qName.equals(BAGTYPES_EINDDATUM_TIJDVAK_GELDIGHEID)) {
			hasEindDatum = true;
		}
		if (qName.equals(GWR_PRODUCT_GERELATEERDE_WOONPLAATS)) {
			isWoonplaats = true;
		}
		if (qName.equals(GWR_PRODUCT_GERELATEERDE_GEMEENTE)) {
			isGemeente = true;
		}
		if (qName.equals(GWR_PRODUCT_IDENTIFICATIE)) {
			isId = true;
		}
	}

	public void endElement(String uri, String localName, String qName) {
		if (qName.equals(GWR_PRODUCT_GERELATEERDE_WOONPLAATS)) {
			isWoonplaats = false;
		}
		if (qName.equals(GWR_PRODUCT_GERELATEERDE_GEMEENTE)) {
			isGemeente = false;
		}
		if (qName.equals(GWR_PRODUCT_IDENTIFICATIE)) {
			isId = false;
		}
		if (qName.equals(GWR_PRODUCT_GEMEENTE_WOONPLAATS_RELATIE)) {
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
