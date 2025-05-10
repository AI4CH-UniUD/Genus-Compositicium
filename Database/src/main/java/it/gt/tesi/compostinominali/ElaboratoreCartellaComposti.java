package it.gt.tesi.compostinominali;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.neo4j.driver.Driver;
import org.neo4j.driver.QueryConfig;

/**
 * La classe che permette di creare in DB i composti nominali e i membri del 
 * foglio di calcolo. Chiamare il metodo elabora().
 */
public class ElaboratoreCartellaComposti {
	
	private static final int NUMERO_MAX_MEMBRI = 4;
	private final Driver dbDriver;
	private final XSSFSheet sheetComposti;
	private final String dbName;
	
	
	/**
	 * Costruisce un oggetto ElaboratoreCartellaComposti a partire dal driver del DB, il foglio di
	 * calcolo dei composti nominali e il mome del database.
	 * 
	 * @param dbDriver il driver del DB
	 * @param sheetOpera il foglio di calcolo dei composti nominali
	 * @param dbName il nome del DB
	 * 
	 * @throw IllegaArgumentException se dbDriver è null, oppure se sheetComposti è null,
	 * 			oppure se dbName è vuoto
	 */
	public ElaboratoreCartellaComposti(Driver dbDriver, 
			XSSFSheet sheetComposti, String dbName) {
		if (dbDriver == null) 
			throw new IllegalArgumentException("Il driver DB non può essere null");
		if (sheetComposti == null) 
			throw new IllegalArgumentException("Il foglio dei composti non può essere null");
		if (StringUtils.isEmpty(dbName)) 
			throw new IllegalArgumentException("Il nome del database non può essere vuoto");
		this.dbDriver = dbDriver;
		this.sheetComposti = sheetComposti;
		this.dbName = dbName;
	}
	
	/**
	 * Costruisce in DB i composti nominali, i membri e le relazioni FORMED_BY tra questi
	 * a partire dal foglio di calcolo dei composti nominali.
	 */
	public void elabora() {
		int compostiCreati = 0;
		int relazioniCreate = 0;
		int membriElaborati = 0;
		int compostiVuoti = 0;
		int grecismiTrovati = 0;
		int rowNum = 0;
		int errors = 0;
		Iterator<Row> rowIterator = sheetComposti.iterator();
		
		//salta la prima riga: i composti nominali partono dalla seconda
		rowIterator.next();
		rowNum++;
		
		while (rowIterator.hasNext()) {
		  Row row = rowIterator.next();
		  rowNum++;
		  try {
			  Composto composto = getComposto(row);
			  if (composto.isEmpty()) {
				  compostiVuoti++;
				  continue;
			  }
			  if (composto.getLemma().contains(" (")) {
				  if (!composto.isGrecismo()) {
					  throw new IllegalStateException("Il composto dovrebbe essere un grecismo");
				  }
				  //i lemmi dei grecismi sono del tipo "latino (greco)" e vanno separati
				  separaLemmaGrecismo(composto);
				  grecismiTrovati++;
			  } else if (composto.isGrecismo()) {
				  throw new IllegalStateException("Grecismo a cui manca l'originale greco");
			  }
			  creaCompostoInDB(composto);
			  compostiCreati++;
			  
			  for (int i = 1; i <= NUMERO_MAX_MEMBRI; i++) {
				  Membro membro = getMembro(row, 2 + i * 2);
				  membriElaborati += creaMembroInDB(membro);		  
				  relazioniCreate += creaRelazioneCompostoMembroInDB(composto, membro, i);
			  }
			  
			  if (compostiCreati % 100 == 0) System.out.println("Righe elaborate: " + compostiCreati);
		  } catch (Exception e) {
			  System.out.println("Errore alla riga " + rowNum + ": " + e.getMessage());
			  errors++;
		  }
		}
		
		System.out.println("\nComposti creati: " + compostiCreati);
		System.out.println("Membri elaborati: " + membriElaborati);
		System.out.println("Relazioni create: " + relazioniCreate);
		System.out.println("Grecismi trovati: " + grecismiTrovati);
		System.out.println("Composti vuoti: " + compostiVuoti);
		System.out.println("Errorti trovati: " + errors);
	}

	/**
	 * Separa il lemma del grecismo nelle sue due componenti, latino e greco
	 * e le assegna al lemma e all'orignaleGreco rispettivamente.
	 * 
	 * @param composto il composto in cui separare il lemma
	 */
	private void separaLemmaGrecismo(Composto composto) {
		String lemma = composto.getLemma();
		int posLeftParentesi = lemma.indexOf(" (", 0);
		composto.setLemma(lemma.substring(0, posLeftParentesi));
		int posRightParentesi = lemma.indexOf(')', posLeftParentesi);
		composto.setOriginaleGreco(lemma.substring(posLeftParentesi + 2, posRightParentesi));
	}

	/**
	 * Crea, se non esiste già, la relazione (NomimalCompound)-[FORMED_BY {position}]->(Member).
	 * 
	 * @param composto il composto nominale per cui creare la relazione FORMED_BY
	 * @param membro il membro da legare nella relazione FORMED_BY
	 * @param posizione la posizione del membro nel composto nominale
	 * @return 0 se membro è vuoto, 1 altrimenti
	 */
	private int creaRelazioneCompostoMembroInDB(Composto composto, Membro membro, int posizione) {
		if (membro.isEmpty()) return 0;
		dbDriver.executableQuery(
				"MATCH (cm:NominalCompound {lemma : $lemmaComp, lexicalCatergory : $catMorfComp, "
				+ "type: $tipologia, subtype: $sottotipologia}), " + 
				"(m:Member {lemma : $lemmaMembro, lexicalCatergory : $catMorfMembro}) " + 
				"MERGE (cm)-[r:FORMED_BY {position : $posizione}]->(m) RETURN cm")
				.withParameters(Map.of("lemmaComp", composto.getLemma(), 
						"catMorfComp", composto.getCategoriaMorfologica(),
						"tipologia", composto.getTipologia(),
						"sottotipologia", composto.getSottotipologia(), 
						"lemmaMembro", membro.getLemma(), 
						"catMorfMembro", membro.getCategoriaMorfologica(), 
						"posizione", posizione))
			    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
				.execute();
		return 1;
	}

	/**
	 * Crea in DB, se non esiste già, il NominalCompound con lemma dato, la
	 * lexicalCategory data, type e subtye. Se si tratta di un grecismo aggiunge
	 * anche la proprietà greekForm.
	 * 
	 * @param composto l'oggetto contenente i dati da inserire
	 */
	private void creaCompostoInDB(Composto composto) {
		if (composto.isGrecismo()) {
			dbDriver.executableQuery(
					"MERGE (cm:NominalCompound {lemma : $lemma, "
					+ "type: $tipologia, subtype: $sottotipologia, "
					+ "lexicalCatergory : $catMorf, greekForm: $originaleGreco}) RETURN cm")
					.withParameters(Map.of("lemma", composto.getLemma(),
							"tipologia", composto.getTipologia(),
							"sottotipologia", composto.getSottotipologia(), 
							"catMorf", composto.getCategoriaMorfologica(), 
							"originaleGreco", composto.getOriginaleGreco()))
				    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
					.execute();
		} else {
			dbDriver.executableQuery(
					"MERGE (cm:NominalCompound {lemma : $lemma, "
					+ "type: $tipologia, subtype: $sottotipologia, "
					+ "lexicalCatergory : $catMorf}) RETURN cm")
					.withParameters(Map.of("lemma", composto.getLemma(), 
							"tipologia", composto.getTipologia(),
							"sottotipologia", composto.getSottotipologia(), 
							"catMorf", composto.getCategoriaMorfologica()))
				    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
					.execute();
		}
	}

	/**
	 * Crea in DB, se non esiste già e se membro non è vuoto, il Member con dato lemma e
	 * data lexycalCategory.
	 * 
	 * @param membro l'oggetto contenente lemma e categoria morfologica da inserire in DB
	 * @return 0 se membro è vuoto, 1 altrimenti
	 */
	private int creaMembroInDB(Membro membro) {
		if (membro.isEmpty()) return 0;
		dbDriver.executableQuery(
			"MERGE (m:Member {lemma : $lemma, lexicalCatergory : $catMorf}) RETURN m")
			.withParameters(Map.of("lemma", membro.getLemma(), 
					"catMorf", membro.getCategoriaMorfologica()))
		    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
			.execute();
		return 1;
	}

	/**
	 * Recupera il Composto (oggetto che contiene lemma, categoria morfologica, tipologia e 
	 * sottotipologia) che si trova nella riga row.
	 * 
	 * @param row la riga contenente lemma, categoria morfologica, tipologia e sottotipologia
	 * @return l'oggetto Composto della riga row 
	 */
	private Composto getComposto(Row row) {
		  String lemma = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : null;
		  String catLemma = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;
		  String tipologia = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null;
		  String sottotipologia = null;
		  if (row.getCell(3) != null && row.getCell(3).getCellType() == CellType.STRING) {
				sottotipologia = row.getCell(3).getStringCellValue();
		  } else if (row.getCell(3) != null && row.getCell(3).getCellType() == CellType.NUMERIC) {
				sottotipologia = String.valueOf(Double.valueOf(row.getCell(3).getNumericCellValue()).intValue());
		  }
		  if (lemma == null && (catLemma != null || tipologia != null || sottotipologia != null)) {
			  throw new IllegalArgumentException("Manca il lemma");
		  }
		  if (catLemma == null && (lemma != null || tipologia != null || sottotipologia != null)) {
			  throw new IllegalArgumentException("Manca la catergoria morforlogica");
		  }
		  Composto composto = new Composto();
		  composto.setLemma(lemma);
		  composto.setCategoriaMorfologica(catLemma);
		  if (tipologia == null) {
			  if (!composto.isEmpty()) {
				  throw new IllegalArgumentException("Manca la tipologia");
			  }
		  }
		  composto.setTipologia(tipologia);
		  if (sottotipologia == null) {
			  if (!composto.isEmpty()) {
				  throw new IllegalArgumentException("Manca la sottotipologia");
			  }
		  }
		  composto.setSottotipologia(sottotipologia);
		  return composto;
	}

	/**
	 * Recupera il Membro (oggetto che contiene lemma e categoria morfologica) che si 
	 * trova alla colonna posCompostoMembro. Nota: il Membro può essere vuoto se manca
	 * il lemma e la categoria morfologica.
	 * 
	 * @param row la riga contenente lemma e categoria morfologica
	 * @param posMembro la posizione della colonna in cui si trovano i dati
	 * @return l'oggetto Membro della riga row alla poszione posMembro
	 * e posMembro + 1
	 */
	private Membro getMembro(Row row, int posMembro) {
		  String lemma = row.getCell(posMembro) != null ?
				  row.getCell(posMembro).getStringCellValue() : null;
		  String catLemma = row.getCell(posMembro + 1) != null ?
				  row.getCell(posMembro + 1).getStringCellValue() : null;
		  if (lemma == null && catLemma != null) {
			  throw new IllegalArgumentException("Mamca il lemma del membro");
		  }
		  if (lemma != null && catLemma == null) {
			  throw new IllegalArgumentException("Mamca la catergoria morfologica del membro");
		  }
		  return new Membro(lemma, catLemma);
	}

}
