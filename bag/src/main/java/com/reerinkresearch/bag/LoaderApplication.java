package com.reerinkresearch.bag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reerinkresearch.bag.model.Adres;
import com.reerinkresearch.bag.model.OpenbareRuimte;
import com.reerinkresearch.bag.model.Summary;
import com.reerinkresearch.bag.parser.GemeenteWoonplaatsRelationHandler;
import com.reerinkresearch.bag.parser.NummerAanduidingHandler;
import com.reerinkresearch.bag.parser.OpenbareRuimteHandler;
import com.reerinkresearch.bag.parser.WoonplaatsHandler;
import com.reerinkresearch.bag.service.GemeenteWoonplaatsService;
import com.reerinkresearch.bag.service.NummerAanduidingService;
import com.reerinkresearch.bag.service.OpenbareRuimteService;

@SpringBootApplication
@RestController
public class LoaderApplication implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(LoaderApplication.class);

	@Autowired
	GemeenteWoonplaatsService gemeenteWoonplaatsService;

	@Autowired
	OpenbareRuimteService openbareRuimteService;

	@Autowired
	NummerAanduidingService nummerAanduidingService;

	@Value("${gemeenteWoonplaatsRelationsFile}")
	private String gemeenteWoonplaatsRelationsFile;

	@Value("${woonplaatsFile}")
	private String woonplaatsFile;

	@Value("${openbareRuimteFolder}")
	private String openbareRuimteFolder;

	@Value("${nummerAanduidingFolder}")
	private String nummerAanduidingFolder;

	@GetMapping("/summary")
	public Summary getSummary() {
		Summary s = new Summary();

		s.setNumGemeenten(this.gemeenteWoonplaatsService.getGemeenteCount());
		s.setNumWoonplaatsen(this.gemeenteWoonplaatsService.getWoonplaatsCount());
		s.setNumStraten(this.openbareRuimteService.getCount());
		s.setNumAdressen(this.nummerAanduidingService.getCount());

		return s;
	}

	private int getRandomNumber(int min, int max) {
		double rand = Math.random();
		return (int) ((rand * (max + 1 - min)) + min);
	}

	@GetMapping("/address")
	public Adres getRandomAddress() {
		Adres a = new Adres();

		int num = this.nummerAanduidingService.getCount();
		int rand = getRandomNumber(0, num - 1);

		var n = this.nummerAanduidingService.get(rand);
		OpenbareRuimte o = this.openbareRuimteService.getOpenbareRuimte(n.getOpenbareRuimteCode());
		var w = this.gemeenteWoonplaatsService.getWoonplaats(o.getWoonplaatsCode());

		a.setStraat(o.getNaam());
		a.setHuisNummer(n.getHuisNummer());
		a.setHuisLetter(n.getHuisLetter());
		a.setPostCode(n.getPostCode());
		a.setWoonplaats(w.getNaam());
		a.setGemeenteCode(w.getGemeenteCode());

		return a;
	}

	@GetMapping("/openbareruimte")
	public OpenbareRuimte getStreet(@RequestParam(required = true) long id) {
		return this.openbareRuimteService.getOpenbareRuimte(id);
	}

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

		// Read gemeente - woonplaats relations into the store
		GemeenteWoonplaatsRelationHandler gemeenteWoonplaatsHandler = new GemeenteWoonplaatsRelationHandler(
				this.gemeenteWoonplaatsService);
		saxParser.parse(this.gemeenteWoonplaatsRelationsFile, gemeenteWoonplaatsHandler);

		// Read woonplaats namen and update the store
		WoonplaatsHandler woonplaatsHandler = new WoonplaatsHandler(this.gemeenteWoonplaatsService);
		saxParser.parse(this.woonplaatsFile, woonplaatsHandler);

		// Read Openbare Ruimten from all files in the folder
		Set<String> obrFiles = getFiles(this.openbareRuimteFolder);
		for (String file : obrFiles) {
			LOG.debug("Openbare Ruimte file: " + file);
			OpenbareRuimteHandler oprHandler = new OpenbareRuimteHandler(this.gemeenteWoonplaatsService,
					this.openbareRuimteService);
			saxParser.parse(this.openbareRuimteFolder + "/" + file, oprHandler);
		}

		// Read Nummer Aanduidingen from all files in the folder
		Set<String> numFiles = getFiles(this.nummerAanduidingFolder);
		for (String file : numFiles) {
			LOG.info("Nummer Aanduiding file: " + file);
			NummerAanduidingHandler numHandler = new NummerAanduidingHandler(this.nummerAanduidingService);
			saxParser.parse(this.nummerAanduidingFolder + "/" + file, numHandler);
		}

		// Show a summary of what has been read
		LOG.info("Aantal gemeenten: " + this.gemeenteWoonplaatsService.getGemeenteCount());
		LOG.info("Aantal woonplaatsen: " + this.gemeenteWoonplaatsService.getWoonplaatsCount());
		int woonplaatsCode = 1024;
		LOG.info("Naam woonplaats " + woonplaatsCode + ": "
				+ this.gemeenteWoonplaatsService.getWoonplaats(woonplaatsCode).getNaam());
		woonplaatsCode = 3630;
		LOG.info("Naam woonplaats " + woonplaatsCode + ": "
				+ this.gemeenteWoonplaatsService.getWoonplaats(woonplaatsCode).getNaam());
		LOG.info("Aantal straten: " + this.openbareRuimteService.getCount());
		LOG.info("Aantal addressen: " + this.nummerAanduidingService.getCount());
		long oId = 855300000002659L;
		LOG.info("Naam OPR: " + oId + "=" + this.openbareRuimteService.getOpenbareRuimte(oId).getNaam());
	}

	Set<String> getFiles(String dir) throws IOException {
		try (Stream<Path> stream = Files.list(Paths.get(dir))) {
			return stream.filter(file -> !Files.isDirectory(file)).map(Path::getFileName).map(Path::toString)
					.collect(Collectors.toSet());
		}
	}

}
