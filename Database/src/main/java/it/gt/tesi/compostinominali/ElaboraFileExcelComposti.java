package it.gt.tesi.compostinominali;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

/**
 * La classe che esegue l'eleaborazione dei fogli di calcolo dei composti nominali e 
 * delle opere aggiungendo i dati in DB.
 */
public class ElaboraFileExcelComposti {

	/**
	 * Il metodo che esegue l'elaborazione dei fogli di calcolo che si trovano nella
	 * directory di input e inserisce composti nominali, membri, opere e autori in DB.
	 * 
	 * @param args gli argomenti a riga di comando, se si passano i parametri per fare
	 * funzionare l'applicazione a riga di comando occore passare tutti e 6 i parametri
	 */
	public static void main(String[] args) {
		if (args.length != 0 && args.length != 6) {
			stampaUsoApplicazione();
		}
		
		Properties prop;
		if (args.length == 0) {
			prop = creaPropertiesDaConfig();
		} else {
			prop = creaPropertiesDaArgument(args);
		}
		checkAllProperties(prop);
		
		checkDirsAndFiles(prop);
		
		long start = System.currentTimeMillis();
		
		Driver dbDriver = getDatabaseDriver(prop.getProperty("dbURI"), 
					prop.getProperty("dbUser"), prop.getProperty("dbPassword"));
			
		elaboraFileComposti(dbDriver, prop);
		
		elaboraFileOpere(dbDriver, prop);
		
		dbDriver.close();	
		
		long end = System.currentTimeMillis();
		
		System.out.println("\nFine elaborazione");
		System.out.println("Tempo di elaborazione in secondi: " + Math.round(((double)(end-start)) / 1000));
	}
	
	/**
	 * Elabora tutte le opere che si trovano nella directory di input.
	 * 
	 * @param dbDriver il driver del DB neo4j
	 * @param prop le proprietà dell'applicazione
	 */
	private static void elaboraFileOpere(Driver dbDriver, Properties prop) {
		File dirInput = new File(prop.getProperty("dir.input"));
		File fileComposti = new File(prop.getProperty("file.composti.nominali"));
		int numFileElaborati = 0;
		
		File[] files = dirInput.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".xlsx") || 
		        		name.toLowerCase().endsWith(".xls");
		    }
		});
	
		//per ogni file elabora l'opera
		for (File fileOpera : files) {
			if (fileOpera.isFile() && !fileOpera.isHidden() && !fileOpera.equals(fileComposti)) {
				try {
					System.out.println("\nElaboro il file " + fileOpera.getName());
					numFileElaborati++;
					
					FileInputStream streamOpera = new FileInputStream(fileOpera);
					XSSFWorkbook workbookOpera = new XSSFWorkbook(streamOpera); 
					
					new ElaboratoreCartellaOpera(dbDriver, 
							workbookOpera.getSheetAt(0), prop.getProperty("dbName")).elabora();
					
					workbookOpera.close();
					streamOpera.close();
				} catch (Exception e) {
					System.err.println("Qualcosa è andato storto nell'elaborazione del file " + fileOpera.getName());
					e.printStackTrace();
				}
			}
		}
		System.out.println("\nNumero file opera elaborati: " + numFileElaborati);
	}

	/**
	 * Verifica che il file dei composti esista e sia un file e che la directory di
	 * input esista e sia una directory. Segnala errore se queste condizioni non 
	 * sono soddisfatte e termina l'applicazione.
	 * 
	 * @param prop le proprietà dell'applicazione
	 */
	private static void checkDirsAndFiles(Properties prop) {
		boolean errors = false;
		
		File fileComposti = new File(prop.getProperty("file.composti.nominali"));
		if (!fileComposti.exists() || !fileComposti.isFile()) {
			System.err.println("Non esiste il file composti");
			errors = true;
		}
		
		File dirInput = new File(prop.getProperty("dir.input"));
		if (!dirInput.exists() || !dirInput.isDirectory()) {
			System.err.println("Non esiste la directory di input");
			errors = true;
		}
	
		if (errors) {
			System.exit(-1);
		}
	}

	/**
	 * Elabora il file dei composti, cioè la cartella dei composti e la cartella
	 * dei doppioni.
	 * 
	 * @param dbDriver il driver del DB neo4j
	 * @param prop le proprietà dell'applicazione
	 */
	private static void elaboraFileComposti(Driver dbDriver, Properties prop) {
		try {
			File fileComposti = new File(prop.getProperty("file.composti.nominali"));
			System.out.println("Elaboro il file dei composti " + fileComposti.getName());
			
			FileInputStream streamComposti = new FileInputStream(fileComposti);	
			XSSFWorkbook workbookComposti = new XSSFWorkbook(streamComposti);
			
			//inserisci i composti
			System.out.println("\nElaboro la cartella dei composti");
			new ElaboratoreCartellaComposti(dbDriver, 
					workbookComposti.getSheetAt(0), 
					prop.getProperty("dbName")).elabora();
			
			//inserisce le relazioni dei doppioni
			System.out.println("\nElaboro la cartella dei doppioni");
			new ElaboratoreCartellaDoppioni(dbDriver,
					workbookComposti.getSheetAt(1),
					prop.getProperty("dbName")).elabora();
			
			workbookComposti.close();
			streamComposti.close();			
		} catch (Exception e) {
			System.err.println("Qualcosa è andato storto nell'elaborazione del file dei composti");
			e.printStackTrace();
		}
	}
	
	/**
	 * Verifica che siano presenti tutte le proprietà per eseguire l'applicazione.
	 * In caso contrario segnala l'errore della proprietà mancante e termina l'applicazione.
	 * 
	 * @param prop le proprietà da controllare
	 */
	private static void checkAllProperties(Properties prop) {
		boolean errors = false;
		if (StringUtils.isEmpty(prop.getProperty("file.composti.nominali"))) {
			System.err.println("Manca la proprietà file.composti.nominali");
			errors = true;
		}
		if (StringUtils.isEmpty(prop.getProperty("dir.input"))) {
			System.err.println("Manca la proprietà dir.input");
			errors = true;
		}
		if (StringUtils.isEmpty(prop.getProperty("dbURI"))) {
			System.err.println("Manca la proprietà dbURI");
			errors = true;
		}
		if (StringUtils.isEmpty(prop.getProperty("dbUser"))) {
			System.err.println("Manca la proprietà dbUser");
			errors = true;
		}
		if (StringUtils.isEmpty(prop.getProperty("dbPassword"))) {
			System.err.println("Manca la proprietà dbPassword");
			errors = true;
		}
		if (StringUtils.isEmpty(prop.getProperty("dbName"))) {
			System.err.println("Manca la proprietà dbName");
			errors = true;
		}
		if (errors) {
			System.exit(-1);
		}
	}

	/**
	 * Crea l'oggetto Properties a partire dagli argomenti a riga di comando in args.
	 * 
	 * @param args gli argomenti a riga di comando
	 * @return l'oggetto Properties con le proprietà contenute nella riga di comando
	 */
	private static Properties creaPropertiesDaArgument(String[] args) {
		Properties prop = new Properties();
		String allArgs = "";
		for (int i = 0; i < args.length; i++) {
			allArgs += args[i] + "\n";
		}
		StringReader stringReader = new StringReader(allArgs);
		try {
			prop.load(stringReader);
		} catch (IOException e) {
		}
		return prop;
	}

	/**
	 * Stampa le informazioni su come usare l'applicazione e termina l'applicazione
	 * 
	 */
	private static void stampaUsoApplicazione() {
		System.out.println("L'applicazione va lanciata senza argomenti oppure con i seguenti sei argomenti:");
		System.out.println("\n\tfile.composti.nominali=<nome file Excel composti nominali con eventuale percorso>");
		System.out.println("\n\tdir.input=<directory in cui si trovano i file delle opere>");
		System.out.println("\n\tdbURI=<URI del DB neo4j a cui collegarsi>");
		System.out.println("\n\tdbUser=<utente col quale collegarsi al DB>");
		System.out.println("\n\tdbPassword=<password dell'utente col quale ci si collega al DB>");
		System.out.println("\n\tdbName=<nome del DB nel quale importare i composti nominali>");
		System.out.println("\nEsempio: file.composti.nominali=src/main/resources/file-da-elaborare/Compounds.xlsx "
				+ "dir.input=src/main/resources/file-da-elaborare dbURI=bolt://localhost:7687/ "
				+ "dbUser=neo4j dbPassword=password dbName=compostinominali");
		System.exit(0);
	}

	/**
	 * Restituisce il driver del database di neo4j in cui inserire i dati.
	 * 
	 * @param dbURI l'URI della connessione DB
	 * @param dbUser lo user per accedere al DB
	 * @param dbPassword la password per accedere al DB
	 * @return l'oggetto Driver corrispondente al database
	 */
	private static Driver getDatabaseDriver(String dbURI, String dbUser, String dbPassword) {
		var driver = GraphDatabase.driver(dbURI, AuthTokens.basic(dbUser, dbPassword));
	    driver.verifyConnectivity();
	    return driver;	
	}
	
	/**
	 * Restituisce un oggetto Properties caricato a partire da config.properties
	 * o un oggetto vuoto se non trova config.properties.
	 * 
	 * @return l'oggetto Properties caricato da config.properties o un oggetto vuoto
	 * se non trova config.properties
	 */
	private static Properties creaPropertiesDaConfig() {
        Properties prop = new Properties();
		try (InputStream input = ElaboraFileExcelComposti.class
				.getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                System.out.println("Non riesco a trovare config.properties");
            } else {
            	prop.load(input);
            }
            
            return prop;            
        } catch (IOException ex) {
        	System.out.println("Non riesco a trovare config.properties");
            return prop;
        }
	}

}
