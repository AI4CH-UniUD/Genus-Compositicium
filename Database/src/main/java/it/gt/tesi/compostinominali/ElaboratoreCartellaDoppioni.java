package it.gt.tesi.compostinominali;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.neo4j.driver.Driver;
import org.neo4j.driver.QueryConfig;

/**
 * La classe che permette di creare in DB le relazioni DUPLICATE_OF.
 * Chiamare il metodo elabora().
 */
public class ElaboratoreCartellaDoppioni {
	
	private final Driver dbDriver;
	private final XSSFSheet sheetDoppioni;
	private final String dbName;
	
	/**
	 * Costruisce un oggetto ElaboratoreCartellaDoppioni a partire dal driver 
	 * del database, dal foglio dei doppioni e dal nome del database.
	 * 
	 * @param dbDriver il driver del database
	 * @param sheetDoppioni il foglio dei doppioni
	 * @param dbName il nome del database
	 * 
	 * @throws IllegalArgumentException se il driver è null, se il foglio è null
	 * oppure se il nome del database è vuoto
	 */
	public ElaboratoreCartellaDoppioni(Driver dbDriver, 
			XSSFSheet sheetDoppioni, String dbName) {
		if (dbDriver == null) 
			throw new IllegalArgumentException("Il driver DB non può essere null");
		if (sheetDoppioni == null) 
			throw new IllegalArgumentException("Il foglio dei doppioni non può essere null");
		if (StringUtils.isEmpty(dbName)) 
			throw new IllegalArgumentException("Il nome del database non può essere vuoto");
		this.dbDriver = dbDriver;
		this.sheetDoppioni = sheetDoppioni;
		this.dbName = dbName;
	}
	
	/**
	 * Crea le relazioni DUPLICATE_OF per i composti che sono presenti nella
	 * cartella passata nel costruttore.
	 */
	public void elabora() {
		Iterator<Row> rowIterator = sheetDoppioni.iterator();
		int errors = 0;
		
		//i composti iniziano dalla seconda riga
		rowIterator.next();
		int rowNum = 1;
		
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			rowNum++;
			int emptyColIdx = getEmptyColIndex(row);
			if (emptyColIdx < 2) {
				System.out.println("ATTENZIONE riga " + rowNum + " errata");
				errors++;
				continue;
			}
			for (int i = 0; i < emptyColIdx - 1; i++) {
				Composto composto = getComposto(row, i);
				if (!esisteCompostoInDB(composto)) {
					System.out.println("ATTENZIONE: manca il composto " + composto.getLemma() + " in DB");
					errors++;
					continue;
				}
				for (int j = i + 1; j < emptyColIdx; j++) {
					Composto doppione = getComposto(row, j);
					if (!esisteCompostoInDB(doppione)) {
						System.out.println("ATTENZIONE: manca il composto " + doppione.getLemma() + " in DB");
						errors++;
						continue;
					}
					creaRelazioneDoppioneInDB(doppione, composto);
				}
			}
		}
		System.out.println("Righe elaborate: " + rowNum);
		System.out.println("Errori trovati: " + errors);
	}

	/**
	 * Crea la relazione DUPLICATE_OF tra il doppione e il composto.
	 * 
	 * @param doppione il composto doppione
	 * @param composto il composto di cui l'altro è doppione
	 */
	private void creaRelazioneDoppioneInDB(Composto doppione, Composto composto) {
		dbDriver.executableQuery(
				"MATCH (c:NominalCompound {lemma : $lemmaComp}), " + 
				"(d:NominalCompound {lemma : $lemmaDopp}) " + 
				"MERGE (d)-[r:DUPLICATE_OF]->(c) RETURN r")
				.withParameters(Map.of("lemmaComp", composto.getLemma(), 
						"lemmaDopp", doppione.getLemma()))
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
	private boolean esisteCompostoInDB(Composto composto) {
		var result = dbDriver.executableQuery(
				"MATCH (c:NominalCompound {lemma: $lemma}) RETURN c")
				.withParameters(Map.of("lemma", composto.getLemma()))
			    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
				.execute();
		return !result.records().isEmpty();
	}
	
	/**
	 * Recupera il composto che si trova alla colonna i-esima della riga row.
	 * 
	 * @param row la riga do recuperare il composto
	 * @param i l'indice della colonna dove recuperare il composto
	 * @return il composto alla riga row e alla i-esima colonna
	 */
	private Composto getComposto(Row row, int i) {
		Composto composto = new Composto();
		Cell cell = row.getCell(i);
		if (cell != null) {
			composto.setLemma(cell.getStringCellValue());
		}
		return composto;
	}

	/**
	 * Recupera l'indice su base zero della prima colonna vuota della riga
	 * row dei composti.
	 * 
	 * @param row la riga su cui ci sono i composti
	 * @return l'indice su base zero della prima colonna che è vuota
	 */
	private int getEmptyColIndex(Row row) {
		return row.getLastCellNum();
	}
	
}
