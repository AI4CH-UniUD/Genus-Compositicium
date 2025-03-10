package it.gt.tesi.compostinominali;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.neo4j.driver.Driver;
import org.neo4j.driver.QueryConfig;

import io.netty.util.internal.StringUtil;

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
		Iterator<Row> rowIterator = sheetComposti.iterator();
		
		//salta la prima riga: i composti nominali partono dalla seconda
		rowIterator.next();
		
		while (rowIterator.hasNext()) {
		  Row row = rowIterator.next();
		  CompostoMembro composto = getCompostoMembro(row, 0);
		  if (composto.isEmpty()) {
			  compostiVuoti++;
			  continue;
		  }
		  if (composto.getLemma().contains(" (")) {
			  //i lemmi dei grecismi sono del tipo "latino (greco)" e vanno separati
			  separaLemmaGrecismo(composto);
			  grecismiTrovati++;
		  }
		  creaCompostoInDB(composto);
		  compostiCreati++;
		  
		  for (int i = 1; i <= NUMERO_MAX_MEMBRI; i++) {
			  CompostoMembro membro = getCompostoMembro(row, i * 2);
			  membriElaborati += creaMembroInDB(membro);		  
			  relazioniCreate += creaRelazioneCompostoMembroInDB(composto, membro, i);
		  }
		  
		  if (compostiCreati % 100 == 0) System.out.println("Righe elaborate: " + compostiCreati);
		}
		
		System.out.println("\nComposti creati: " + compostiCreati);
		System.out.println("Membri elaborati: " + membriElaborati);
		System.out.println("Relazioni create: " + relazioniCreate);
		System.out.println("Grecismi trovati: " + grecismiTrovati);
		System.out.println("Composti vuoti: " + compostiVuoti);
	}

	/**
	 * Separa il lemma del grecismo nelle sue due componenti, latino e greco
	 * e le assegna al lemma e all'orignaleGreco rispettivamente.
	 * 
	 * @param composto il composto in cui separare il lemma
	 */
	private void separaLemmaGrecismo(CompostoMembro composto) {
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
	private int creaRelazioneCompostoMembroInDB(CompostoMembro composto, CompostoMembro membro, int posizione) {
		if (membro.isEmpty()) return 0;
		dbDriver.executableQuery(
				"MATCH (cm:NominalCompound {lemma : $lemmaComp, lexicalCatergory : $catMorfComp}), " + 
				"(m:Member {lemma : $lemmaMembro, lexicalCatergory : $catMorfMembro}) " + 
				"MERGE (cm)-[r:FORMED_BY {position : $posizione}]->(m) RETURN cm")
				.withParameters(Map.of("lemmaComp", composto.getLemma(), 
						"catMorfComp", composto.getCategoriaMorfologica(), 
						"lemmaMembro", membro.getLemma(), 
						"catMorfMembro", membro.getCategoriaMorfologica(), 
						"posizione", posizione))
			    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
				.execute();
		return 1;
	}

	/**
	 * Crea in DB, se non esiste già, il NominalCompound con lemma dato e 
	 * lexicalCategory data. Se si tratta di un grecismo aggiunge anche la 
	 * proprietà greekForm.
	 * 
	 * @param composto l'oggetto contenente i dati da inserire
	 */
	private void creaCompostoInDB(CompostoMembro composto) {
		if (StringUtil.isNullOrEmpty(composto.getOriginaleGreco())) {
			dbDriver.executableQuery(
					"MERGE (cm:NominalCompound {lemma : $lemma, lexicalCatergory : $catMorf}) RETURN cm")
					.withParameters(Map.of("lemma", composto.getLemma(), 
							"catMorf", composto.getCategoriaMorfologica()))
				    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
					.execute();
		} else {
			//caso dei grecismi
			dbDriver.executableQuery(
					"MERGE (cm:NominalCompound {lemma : $lemma, "
					+ "lexicalCatergory : $catMorf, greekForm: $originaleGreco}) RETURN cm")
					.withParameters(Map.of("lemma", composto.getLemma(), 
							"catMorf", composto.getCategoriaMorfologica(), 
							"originaleGreco", composto.getOriginaleGreco()))
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
	private int creaMembroInDB(CompostoMembro membro) {
		if (membro.isEmpty()) return 0;
		dbDriver.executableQuery(
			"MERGE (m:Member {lemma : $lemma, lexicalCatergory : $catMorf}) RETURN m")
			.withParameters(Map.of("lemma", membro.getLemma(), "catMorf", membro.getCategoriaMorfologica()))
		    .withConfig(QueryConfig.builder().withDatabase(dbName).build())
			.execute();
		return 1;
	}

	/**
	 * Recupera il CompostoMembro (oggetto che contiene lemma e categoria morfologica) che si 
	 * trova alla colonna posCompostoMembro. Nota: il CompostoMembro può essere vuoto se manca
	 * il lemma e la categoria morfologica.
	 * 
	 * @param row la riga contenente lemma e categoria morfologica
	 * @param posCompostoMembro la posizione della colonna in cui si trovano i dati
	 * @return l'oggetto CompostoMembro della riga row alla poszione posCompostoMembro
	 * e posCompostoMembro + 1
	 */
	private CompostoMembro getCompostoMembro(Row row, int posCompostoMembro) {
		  String lemma = row.getCell(posCompostoMembro) != null ?
				  row.getCell(posCompostoMembro).getStringCellValue() : null;
		  String catLemma = row.getCell(posCompostoMembro + 1) != null ?
				  row.getCell(posCompostoMembro + 1).getStringCellValue() : null;
		return new CompostoMembro(lemma, catLemma);
	}

}
