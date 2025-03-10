
This is the project CommpostiNominali to import Excel files into the Neo4j database. The project is Maven project of which it is reported the pom.xml file. You can create the project under an environment like Eclipse.

To run the project you can run the main class ElaboraFileExcelComposti.java passing zero arguments and using the file config.properties unnder src/main/resources or you can pass it six parameters as described in the example below. You can also create a jar file named Composti-Nominali.jar running the manven command "clean package". After which you can run the command:

java -jar Composti-Nominali.jar file.composti.nominali=file-composti-nominali/Compounds.xlsx dir.input=file-composti-nominali dbURI=bolt://localhost:7687/ dbUser=utente dbPassword=password dbName=nominalcompounds

in which:
- file-composti-nominali/Compounds.xlsx is the name of the file of the nominal compounds with the directory where it is contained, both the file name and the directory without spaces;
- file-composti-nominali is the imput directory without spaces of the files of Works and this directory should contain only work files and possibly the file with nominal compounds and duplicates;
- bolt://localhost:7687/ is the address of the local machine that runs and instance of Neo4j Desktop, but you can substitute this address with neo4j://<ip-address>:port/ for the address of a remote machine that runs a Neo4j database server;
- utente is the user name to access Neo4j database;
- password is the password of the user to access Neo4j database;
- nominalcompounds is the name of the Neo4j database you are accessing.


