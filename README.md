<div align="center">  
  
  
# Genus-Compositicium
  
</div>

## Description 

This is the home page of the graph database of Latin nominal compounds, developed at the University of Udine, Italy.

The database aims to report the data contained in some Excel files ([also present in this repository](https://github.com/AI4CH-UniUD/Genus-Compositicium/tree/main/Dataset)) about nominal compounds of works of different Latin authors and to be a tool for investigating which types of compounds are used depending on the authors and the works they have composed, in a much more effective and efficient way compared to the use of the original Excel files.

<p align="center">
<img src="https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/ER_CompostiNominali.png" alt="Overall Entity-Relationship diagram" />
</p>

The database schema, which is depicted in the figure above, is composed as follows:
* **Opera**: it stores information regarding a single Work of an Author and has a Title (Titolo) and an acronym (abbreviazione);
* **Autore**: it stores information regarding an Author which are his Name (Nome), his century of birth (Secolo nascita) and his century of death (Secolo morte)
*  **Composto nominale**: it stores informaton regarding a Nominal Compound like its Lemma, its Greek form (Originale greco) and its Lexical category (Categoria morfologica)
* **Membro**: it stores informaton regarding the Members of a Nominal Compound and contains its Lemma and its Lexical category (Categoria morfologica)

In the following picture we have an example of the graph database.

<p align="center">
<img src="https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/DB_Grafo_Composti_Nominali.png" alt="Subgraph of the Nominal Compounds graph" />
</p>

The current repository includes:
* the code to create the graph database starting from the Excel files for Nominal Compounds and Works: [link](https://github.com/AI4CH-UniUD/Genus-Compositicium/tree/main/Database/src/main/java/it/gt/tesi/compostinominali)
* the code of some queries that show how to use the database: [link](https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/example_queries.rtf)


### Usage of the online implementation of the system

The system can be accessed at the address [http://158.110.146.222:7475/](http://158.110.146.222:7475/). Upon connection, users will find a web server interface, asking for the login data. 
A read-only user, that has the privileges to perform `MATCH` operations over the database has been provided, with the following credentials: 
```
username = 
password = 
```
The database comes already populated with information originating from the Excel files [here](https://github.com/AI4CH-UniUD/Genus-Compositicium/tree/main/Dataset).
Examples of queries on the database are reported [here](https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/example_queries.rtf).

### Entity-Relationship diagram notation

The following [link](https://github.com/dslab-uniud/Database-indoor/blob/main/README.md) gives an overview of the Entity-Relationship diagram notation.
