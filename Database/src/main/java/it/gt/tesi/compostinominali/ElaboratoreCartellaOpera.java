package it.gt.tesi.compostinominali;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.neo4j.driver.Driver;
import org.neo4j.driver.QueryConfig;

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
		
		int compostiTrovati = 0;
		int compostiVuoti = 0;
		int rowNum = 5;
		int errors = 0;
		
		//i composti nominali iniziano due righe sotto l'opera
		rowIterator.next();
		rowIterator.next();
		
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			rowNum++;
			try {
				Composto composto = getComposto(row);
				if (composto.isEmpty()) {
					System.out.println("Composto vuoto: " + composto.getLemma() + " alla riga " + rowNum);
					compostiVuoti++;
					continue;
				} 
				compostiTrovati++;
				if (esisteComposto(composto)) {
					creaRelazioneOperaCompostoInDB(opera, composto);
				} else {
					System.out.println("IL COMPOSTO " + composto.getLemma() 
						+ " NON È STATO TROVATO TRA LA LISTA DEI COMPOSTI NOMINALI");
					errors++;
				}
			} catch (Exception e) {
				System.out.println("Errore alla riga " + rowNum + ": " + e.getMessage());
				errors++;
			}
		}
		
		System.out.println("Composti trovati: " + compostiTrovati);
		System.out.println("Composti vuoti: " + compostiVuoti);
		System.out.println("Errori trovati: " + errors);
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
				+ "(cn:NominalCompound {lemma: $lemma}) "
				+ "MERGE (op)-[r:CONTAINS {occurrences: $occorrenze}]->(cn) RETURN r")
				.withParameters(Map.of("titolo", opera.getTitolo(), 
						"genere", opera.getGenere(), 
						"sottogenere", opera.getSottogenere(),
						"abbreviazione", opera.getAbbreviazione(),
						"lemma", composto.getLemma(),
						"occorrenze", composto.getOccorrenze()))
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
	 * Recupera il Composto che ha il dato lemma e il dato numero di occorrenze.
	 * 
	 * @param row la riga dove si trova il composto
	 * @return il composto alla riga row
	 */
	private Composto getComposto(Row row) {
		Composto composto = new Composto();
		composto.setLemma(getStringCellValue(row.getCell(0)));
		composto.setOccorrenze(getIntCellValue(row.getCell(1)));
		if (composto.getOccorrenze() <= 0 && !composto.isEmpty()) {
			throw new IllegalArgumentException("Mancano le occorrenze");
		}
		return composto;
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
		opera.setTitolo(getStringCellValue(row.getCell(0)));
		opera.setAbbreviazione(getStringCellValue(row.getCell(1)));
		row = rowIterator.next();
		opera.setGenere(getStringCellValue(row.getCell(0)));
		opera.setSottogenere(getStringCellValue(row.getCell(1)));
		if (StringUtils.isEmpty(opera.getTitolo())) {
			throw new IllegalArgumentException("Manca il titolo dell'opera");
		}
		if (StringUtils.isEmpty(opera.getAbbreviazione())) {
			throw new IllegalArgumentException("Manca l'abbreviazione dell'autore");
		}
		if (StringUtils.isEmpty(opera.getGenere())) {
			throw new IllegalArgumentException("Manca il genere dell'opera");
		}
		if (StringUtils.isEmpty(opera.getSottogenere())) {
			throw new IllegalArgumentException("Manca il sottogenere dell'opera");
		}
		return opera;
	}
	
	/**
	 * Restituisce il valore di cell come stringa o null se cell è null.
	 * 
	 * @param cell la Cell da cui prendere il valore stringa
	 * @return il valore di cell come stringa o null se cell è null
	 */
	private String getStringCellValue(Cell cell) {
		return cell != null ? cell.getStringCellValue() : null;
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
		autore.setNome(getStringCellValue(row.getCell(0)));
		autore.setSecoloNascita(getIntCellValue(row.getCell(1)));
		autore.setSecoloMorte(getIntCellValue(row.getCell(2)));
		if (StringUtils.isEmpty(autore.getNome())) {
			throw new IllegalArgumentException("Manca il nome dell'autore");
		}
		if (autore.getSecoloNascita() == 0) {
			throw new IllegalArgumentException("Secolo nascita autore errato");
		}
		if (autore.getSecoloMorte() == 0) {
			throw new IllegalArgumentException("Secolo morte autore errato");
		}
		return autore;
	}
	
	/**
	 * Restituisce il valore della cella cell come intero. Se cell è null
	 * o ha CellType.BLANK restituisce 0. Solleva un'eccezione se CellType
	 * non è NUMERIC, STRING o BLANK.
	 * 
	 * @param cell la cella da cui recuperare i valore
	 * @return il valore della cella come intero
	 */
	private int getIntCellValue(Cell cell) {
		if (cell == null) {
			return 0;
		}
		if (cell.getCellType() == CellType.NUMERIC) {
			return Double.valueOf(cell.getNumericCellValue()).intValue();
		}
		if (cell.getCellType() == CellType.STRING) {
			return Integer.valueOf(cell.getStringCellValue());
		}
		if (cell.getCellType() == CellType.BLANK) {
			return 0;
		}
		throw new IllegalArgumentException("Tipo di cella non riconosciuta " + cell.getCellType());
	}

}
