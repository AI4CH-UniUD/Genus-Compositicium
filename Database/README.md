
These instructions refer to the project 'CompostiNominali', whose code can be used to import Excel files into a Neo4j database.
The project is Maven-based, and it can be re-created (for instance, for customization purposes) under an IDE like Eclipse using the information reported in the pom.xml file.
You can also create a jar file named Composti-Nominali.jar running the maven command "clean package", which creates a jar file with all the code needed to run the database import script. 

## Running the code to create the Neo4j graph database and to import the Excel data in it

Otherwise, you can use the jar file already include in this repository [here](https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/Database/Composti-Nominali.jar).

You can run the main class ElaboraFileExcelComposti.java passing zero arguments and using the file config.properties under src/main/resources to pass the six parameters the script needs, or you can pass six parameters on the command line as described in the example below.

```
java -jar Composti-Nominali.jar file.composti.nominali=file-composti-nominali/Compounds.xlsx dir.input=file-composti-nominali dbURI=bolt://localhost:7687/ dbUser=utente dbPassword=password dbName=nominalcompounds
```

in which:
- _file-composti-nominali/Compounds.xlsx_ is the name of the file of the nominal compounds with the directory where it is contained; both the file name and the directory should be without spaces.
- _file-composti-nominali_ is the input directory (without spaces) of the Excel filesand this directory should contain only work files and possibly the file with nominal compounds and their duplicates.
- _bolt://localhost:7687/_ is the address of the local machine that runs an instance of Neo4j Desktop, but you can substitute this address with neo4j://ip-server-address:port/ for the address of a remote machine that runs a Neo4j database server.
- _utente_ is the user name to access Neo4j database.
- _password_ is the password of the user to access Neo4j database.
- _nominalcompounds_ is the name of the Neo4j database you are accessing.

Once run, the jar script merges into the Neo4J database all the information contained in the input Excel files which is not already contained in the database. Please note that the script does not modify (update/delete) data which has already been imported in the database, even when such data is different with respect to that contained in the Excel files. This means that possbile erroneous database entries should be corrected directly within the database. Another possiblity is correcting the Excel files related to those entries, truncating the entire database, and then running the whole jar script to re-populate the database from zero starting from the Excel files.
