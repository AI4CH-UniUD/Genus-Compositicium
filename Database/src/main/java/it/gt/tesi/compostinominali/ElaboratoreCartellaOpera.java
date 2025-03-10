package it.gt.tesi.compostinominali;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.neo4j.driver.Driver;
import org.neo4j.driver.QueryConfig;
import org.neo4j.driver.Value;
import org.neo4j.driver.util.Pair;

/**
 * La classe che permette di creare in DB l'opera di un dato foglio di calcolo.
 * Chiamare il metodo elabora().
 */
public class ElaboratoreCartellaOpera {
	
	private final Driver dbDriver;
	private final XSSFSheet sheetOpera;
	private final String dbName;
	
	/**
	 * Costruisce un oggetto ElaboratoreCartellaOpera a partire dal driver del DB, il foglio di
	 * calcolo dell'opera e il mome del database.
	 * 
	 * @param dbDriver il driver del DB
	 * @param sheetOpera il foglio di calcolo dell'opera
	 * @param dbName il nome del DB
	 * 
	 * @throw IllegaArgumentException se dbDriver è null, oppure se sheetOpera è null,
	 * 			oppure se dbName è vuoto
	 */
	public ElaboratoreCartellaOpera(Driver dbDriver, XSSFSheet sheetOpera, String dbName) {
		if (dbDriver == null) 
			throw new IllegalArgumentException("Il driver DB non può essere null");
		if (sheetOpera == null) 
			throw new IllegalArgumentException("Il foglio dell'opera non può essere null");
		if (StringUtils.isEmpty(dbName)) 
			throw new IllegalArgumentException("Il nome del database non può essere vuoto");
		this.dbDriver = dbDriver;
		this.sheetOpera = sheetOpera;
		this.dbName = dbName;
	}
	
	/**
	 * Costruisce la data opera in DB aggiungendo autore, opera e le occorrenze dei composti
	 * nominali presenti nel foglio di calcolo.
	 */
	public void elabora() {
		Iterator<Row> rowIterator = sheetOpera.iterator();
		
		Autore autore = getAutore(rowIterator);
		creaAutoreInDB(autore);
		
		Opera opera = getOpera(rowIterator);
		creaOperaInDB(opera);
		
		creaRelazioneOperaAutoreInDB(opera, autore);
		
		Row rowTipologia = rowIterator.next();
		Row rowSottotipologia = rowIterator.next();
		
		int compostiTrovati = 0;
		int grecismiTrovati = 0;
		int compostiVuoti = 0;
		int rowNum = 5;
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			rowNum++;
			Composto composto = getComposto(row, rowTipologia, rowSottotipologia);
			if (composto.isEmpty()) {
				System.out.println("Composto vuoto: " + composto.getLemma() + " alla riga " + rowNum);
				compostiVuoti++;
				continue;
			} else if (composto.isGrecismo()) {
				grecismiTrovati++;
			}
			compostiTrovati++;
			if (esisteComposto(composto)) {
				//se il composto ha la stessa tipologia e sottotipologia nel database
				//oppure non ha ancora le proprietà, lo aggiungo in DB, 
				//altrimenti il metodo segnala errore
				if (compostoHaStessaTipologiaInDB(composto)) {
					//il composto nominale è già presente del database, ma gli si devono
					//aggiungere le proprietà tipologia e sottotipologia
					aggiungiProprietaCompostoInDB(composto);
					creaRelazioneOperaCompostoInDB(opera, composto);
				}
			} else {
				System.out.println("IL COMPOSTO " + composto.getLemma() 
					+ " NON È STATO TROVATO TRA LA LISTA DEI COMPOSTI NOMINALI");
			}
		}
		
		System.out.println("Grecismi trovati: " + grecismiTrovati);
		System.out.println("Composti trovati: " + compostiTrovati);
		System.out.println("Composti vuoti: " + compostiVuoti);
	}

	/**
	 * Verifica che il composto nominale in DB abbia le stesse tipologia e 
	 * sottotipologia del composto passato come parametro. Se ha le stesse
	 * tipologia e sottotipologia oppure se non ha ancora valori per queste
	 * restituiesce true. Altrimenti segnala l'errore e restituisce false.
	 * Si suppone che il composto esista già nel database.
	 * 
	 * @param composto il composto nominale di cui controllare tipologia e sottotipologia
	 * @return true se composto ha le stesse tipologia e sottotipologia del
	 * 		NominalCompound in DB con lo stesso lemma oppure se il NominalCompound
	 * 		non ha ancora le proprietà impostate; false altrimenti 
	 */
	private boolean compostoHaStessaTipologiaInDB(Composto composto) {
		var result = dbDriver.executableQuery(
				"MATCH (c:NominalCompound {lemma: $lemma}) RETURN c")
				.withParameters(Map.of("lemma", composto.getLemma()))
			    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
				.execute();
		List<Pair<String,Value>> values = result.records().get(0).fields();
		for (Pair<String,Value> nameValue: values) {
	        Value value = nameValue.value();
	        //verifica che la tipologia e la sottotipologia sono le stesse del composto
	        String tipologia = value.get("type").asString();
	        String sottotipologia = value.get("subtype").asString();
	        if (tipologia == null || "null".equals(tipologia) || 
	        		sottotipologia == null || "null".equals(sottotipologia)) {
	        	return true;
	        }
	        if (tipologia.equals(composto.getTipologia()) && 
	        		sottotipologia.equals(composto.getSottotipologia())) {
	        	return true;
	        }
	        System.out.println("ATTENZIONE: il composto nominale " + composto.getLemma() +
	        		" ha tipologia " + composto.getTipologia() + " e sottotipologia " + 
	        		composto.getSottotipologia() + " ma nel database ha tipologia " +
	        		tipologia + " e sottotipologia " + sottotipologia + 
	        		". \nNon viene aggiunto al DB: correggere l'errore.");
	        return false;
		}
		return true;
	}

	/**
	 * Aggiunge al DB, se non è già presente, la relazione 
	 * (Work)-[CONTAINS [occurences}]->(NominalCompound)
	 * 
	 * @param opera a cui aggiungere la relazione CONTAINS
	 * @param composto a cui aggiungere la relazione CONTAINS
	 */
	private void creaRelazioneOperaCompostoInDB(Opera opera, Composto composto) {
		dbDriver.executableQuery(
				"MATCH (op:Work {title: $titolo, genre: $genere, "
				+ "subgenre: $sottogenere, acronym: $abbreviazione}), "
				+ "(cn:NominalCompound {lemma: $lemma, type: $tipologia, "
				+ "subtype: $sottotipologia}) "
				+ "MERGE (op)-[r:CONTAINS {occurrences: $occorrenze}]->(cn) RETURN r")
				.withParameters(Map.of("titolo", opera.getTitolo(), 
						"genere", opera.getGenere(), 
						"sottogenere", opera.getSottogenere(),
						"abbreviazione", opera.getAbbreviazione(),
						"lemma", composto.getLemma(),
						"tipologia", composto.getTipologia(),
						"sottotipologia", composto.getSottotipologia(), 
						"occorrenze", composto.getOccorrenze()))
			    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
				.execute();
	}

	/**
	 * Aggiunge le proprietà tipologia e sottotipologia cercando il composto per lemma.
	 * 
	 * @param composto il composto con le proprietà da aggiungere
	 */
	private void aggiungiProprietaCompostoInDB(Composto composto) {
		dbDriver.executableQuery(
				"MATCH (c:NominalCompound {lemma: $lemma}) "
				+ "SET c.type = $tipologia, c.subtype = $sottotipologia RETURN c")
				.withParameters(Map.of("lemma", composto.getLemma(), 
						"tipologia", composto.getTipologia(), 
						"sottotipologia", composto.getSottotipologia()))
			    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
				.execute();
	}

	/**
	 * Restituisce true se il composto col dato lemma è già presente in DB,
	 * false altrimenti.
	 * 
	 * @param composto con il lemma da verificare se è già presente nel DB
	 * @return true se il composto con lemma uguale a composto.getLemma() esiste in DB,
	 * false altrimenti
	 */
	private boolean esisteComposto(Composto composto) {
		var result = dbDriver.executableQuery(
				"MATCH (c:NominalCompound {lemma: $lemma}) RETURN c")
				.withParameters(Map.of("lemma", composto.getLemma()))
			    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
				.execute();
		return !result.records().isEmpty();
	}
	
	/**
	 * Recupera il Composto che ha il dato lemma, la data tipologia, la data sottotipologia
	 * e il dato numero di occorrenze.
	 * 
	 * @param row la riga dove si trova il composto
	 * @param rowTipologia la riga dove si trovano le varie tipologie
	 * @param rowSottotipologia la riga dove si trovano le varie sottotipologie
	 * @return il composto alla riga row
	 */
	private Composto getComposto(Row row, Row rowTipologia, Row rowSottotipologia) {
		Composto composto = new Composto();
		Iterator<Cell> cellIterator = row.cellIterator();
		
		Cell cell = cellIterator.next();
		composto.setLemma(cell.getStringCellValue());
		
		while (cellIterator.hasNext()) {
			cell = cellIterator.next();
			if (cell.getCellType() == CellType.NUMERIC) {
				composto.setOccorrenze(Double.valueOf(cell.getNumericCellValue()).intValue());
				composto.setTipologia(getTipologia(rowTipologia, cell.getColumnIndex()));
				composto.setSottotipologia(getSottotipologia(rowSottotipologia, cell.getColumnIndex()));
				break;
			}
		}
		return composto;
	}

	/**
	 * Restituisce la sottotipoloiga relativa all'indice columnIndex
	 * 
	 * @param rowSottotipologia la riga dove si trovano le varie sottotipologie
	 * @param columnIndex l'indice della colonna della sottotipologia
	 * @return la sottotipoloiga relativa all'indice columnIndex
	 * 
	 * @throw IllegalStateException se non riesce a trovare la sottotipologia
	 */
	private String getSottotipologia(Row rowSottotipologia, int columnIndex) {
		if (rowSottotipologia.getCell(columnIndex).getCellType() == CellType.STRING) {
			return rowSottotipologia.getCell(columnIndex).getStringCellValue();
		}
		if (rowSottotipologia.getCell(columnIndex).getCellType() == CellType.NUMERIC) {
			return String.valueOf(Double.valueOf(
						rowSottotipologia.getCell(columnIndex).getNumericCellValue()).intValue());
		}
		throw new IllegalStateException("Il foglio " + sheetOpera.getSheetName() +
				" ha la riga della sottotipologia errata");
	}

	/**
	 * Restituisce la tipologia corrispondente alla colonna columnIndex
	 * 
	 * @param rowTipologia la riga delle tipologie
	 * @param columnIndex l'indice della colonna della tipologia
	 * @return la tipologia corrispondente alla colonna columnIndex
	 * 
	 * @throw IllegalStateException se non riesce a trovare la tipologia
	 */
	private String getTipologia(Row rowTipologia, int columnIndex) {
		for (int pos = columnIndex; pos > 0; pos--) {
			if (rowTipologia.getCell(pos) != null && 
					rowTipologia.getCell(pos).getCellType() == CellType.STRING) {
				return rowTipologia.getCell(pos).getStringCellValue();
			}
		}
		throw new IllegalStateException("Il foglio " + sheetOpera.getSheetName() +
				" ha la riga della tipologia errata");
	}

	/**
	 * Crea, se non esiste già, la relazione (Work)-[WRITTEN_BY]->(Author)
	 * 
	 * @param opera l'opera per cui creare la relazione WRITTEN_BY
	 * @param autore l'autore per cui creare la relazione WRITTEN_BY
	 */
	private void creaRelazioneOperaAutoreInDB(Opera opera, Autore autore) {
		dbDriver.executableQuery(
				"MATCH (op:Work {title: $titolo, genre: $genere, "
				+ "subgenre: $sottogenere, acronym: $abbreviazione}), "
				+ "(au:Author {name: $nome, centuryOfBirth: $secoloNascita, centuryOfDeath: $secoloMorte}) "
				+ "MERGE (op)-[r:WRITTEN_BY]->(au) RETURN r")
				.withParameters(Map.of("titolo", opera.getTitolo(), 
						"genere", opera.getGenere(), 
						"sottogenere", opera.getSottogenere(),
						"abbreviazione", opera.getAbbreviazione(),
						"nome", autore.getNome(),
						"secoloNascita", autore.getSecoloNascita(),
						"secoloMorte", autore.getSecoloMorte()))
			    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
				.execute();
	}

	/**
	 * Crea in DB, se non esiste già, l'opera col dato titolo, il dato genere e il dato sottogenere
	 * 
	 * @param opera l'oggetto Opera da creare in DB
	 */
	private void creaOperaInDB(Opera opera) {
		dbDriver.executableQuery(
				"MERGE (o:Work {title: $titolo, genre: $genere, "
				+ "subgenre: $sottogenere, acronym: $abbreviazione}) RETURN o")
				.withParameters(Map.of("titolo", opera.getTitolo(), 
						"genere", opera.getGenere(), 
						"sottogenere", opera.getSottogenere(),
						"abbreviazione", opera.getAbbreviazione()))
			    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
				.execute();
	}

	/**
	 * Recupera l'oggetto Opera di questo foglio leggendo titolo, genere e sottogenere.
	 * Incrementa di due rowIterator
	 * 
	 * @param rowIterator l'iterator che contiene le righe coi dati dell'opera
	 * @return l'opera recuperata
	 */
	private Opera getOpera(Iterator<Row> rowIterator) {
		Opera opera = new Opera();
		Row row = rowIterator.next();
		opera.setTitolo(row.getCell(0).getStringCellValue());
		opera.setAbbreviazione(row.getCell(1).getStringCellValue());
		row = rowIterator.next();
		opera.setGenere(row.getCell(0).getStringCellValue());
		opera.setSottogenere(row.getCell(1).getStringCellValue());
		return opera;
	}

	/**
	 * Crea in DB, se non esiste già, l'Autore col dato nome, il dato secolo di nascita e 
	 * il dato secolo di morte.
	 * 
	 * @param autore da inserire in DB
	 */
	private void creaAutoreInDB(Autore autore) {
		dbDriver.executableQuery(
				"MERGE (a:Author {name: $nome, centuryOfBirth: $secoloNascita, centuryOfDeath: $secoloMorte}) RETURN a")
				.withParameters(Map.of("nome", autore.getNome(), 
						"secoloNascita", autore.getSecoloNascita(), 
						"secoloMorte", autore.getSecoloMorte()))
			    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
				.execute();
	}

	/**
	 * Recupera l'autore dal foglio di calcolo prendendo nome, secolo di nascita e secolo di morte.
	 * Manda avanti rowIterator di 1.
	 * 
	 * @param rowIterator l'iterator che contiene la riga dove si trovano i dati dell'autore
	 * @return l'autore del foglio di calcolo
	 */
	private Autore getAutore(Iterator<Row> rowIterator) {
		Row row = rowIterator.next();
		Autore autore = new Autore();
		autore.setNome(row.getCell(0).getStringCellValue());
		autore.setSecoloNascita(Double.valueOf(row.getCell(1).getNumericCellValue()).intValue());
		autore.setSecoloMorte(Double.valueOf(row.getCell(2).getNumericCellValue()).intValue());
		return autore;
	}

}
