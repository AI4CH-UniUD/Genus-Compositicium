
This is the project CommpostiNominali to import Excel files into the Neo4j database. The project is Maven project of which it is reported the pom.xml file. You can create the project under an environment like Eclipse.

To run the project you can run the main class ElaboraFileExcelComposti.java passing zero arguments and using the file config.properties unnder src/main/resources or you can pass it six parameters as described in the example below. You can also create a jar file named Composti-Nominali.jar running the manven command "clean package". After which you can run the command:

java -jar Composti-Nominali.jar file.composti.nominali=file-composti-nominali/Compounds.xlsx dir.input=file-composti-nominali dbURI=bolt://localhost:7687/ dbUser=utente dbPassword=password dbName=nominalcompounds
