# Cypher queries for the graph database of Latin compounds

The file `Index.xlsx` contains all the information that the queries presented in this document can refer to.

## Occurences of a specific compound

These queries examine which authors and works attest a specific compound, providing the number of occurrences as well as its type and subtype.

### All authors and works I

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  c.lemma='aliger' // Insert here the compound
RETURN
  c.lemma AS Compound,
  w.acronym AS Author_Title,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  tolower(w.acronym)
```

Results are sorted alphabetically by acronym.

### All authors and works II

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  c.lemma='aliger' // Insert here the compound
RETURN
  c.lemma AS Compound,
  a.name AS Author,
  w.title AS Title,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  tolower(a.name),
  tolower(w.title)
```

The result is the same as the previous one, but with more detailed information about author and title.

### All authors and works III (graphic result)

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  c.lemma='aliger' // Insert here the compound
RETURN
  c, w, a, r, wr
```

The result is the same, but Neo4j generates a graph of the nodes representing the compound, the works in which it occurs, and their authors, as shown in the following picture.

<p align="center">
<img src="https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/IMG/IMG_1_aliger.jpg" alt="aliger" />
</p>

### Specific author I

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  c.lemma='aliger' // Insert here the compound
  AND a.name='P. Papinius Statius' // Insert here the name of the author
RETURN
  c.lemma AS Compound,
  w.acronym AS Author_Title,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  tolower(w.acronym)
```

This query examines how often a given compound appears in the *corpus* of a specific author.

### Specific author II

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  c.lemma='aliger' // Insert here the compound
  AND a.name='P. Papinius Statius' // Insert here the name of the author
RETURN
  c.lemma AS Compound,
  a.name AS Author,
  w.title AS Title,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  tolower(w.title)
```

The result is the same as the previous one, but the definitions of author and title are more detailed.

### Specific work I

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  c.lemma='aliger' // Insert here the compound
  AND a.name='P. Papinius Statius' // Insert here the name of the author
  AND w.title='Thebais' // Insert here the title of the work
RETURN
  c.lemma AS Compound,
  a.name AS Author,
  w.title AS Title,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
```

This query examines how frequently a given compound appears in a specific work.

### Specific work II

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  c.lemma='aliger' // Insert here the compound
  AND w.acronym='STAT. Theb.' // Insert here the acronym of the work
RETURN
  c.lemma AS Compound,
  w.acronym AS Author_Title,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
```

The result is the same as before, but the syntax is less verbose.

## All compounds sharing one of the members

These queries investigate which compounds have a member in common.
Results are sorted alphabetically.
The two members of the compound, along with its type and subtype, are included.

### All compounds sharing the first member I

```cypher
MATCH
  (c:NominalCompound)-[r1:FORMED_BY]->(m1:Member),
  (c)-[r2:FORMED_BY]->(m2:Member)
WHERE
  r1.position=1
  AND m1.lemma='aequus' // Insert here the first member
  AND r2.position=2
RETURN
  c.lemma AS Compound,
  m1.lemma AS First_Member,
  m2.lemma AS Second_Member,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  toLower(c.lemma)
```

This query identifies all compounds sharing the same first member.

### All compounds sharing the first member II (graphic result)

```cypher
MATCH
  (c:NominalCompound)-[r1:FORMED_BY]->(m1:Member),
  (c)-[r2:FORMED_BY]->(m2:Member)
WHERE
  r1.position=1
  AND m1.lemma='aequus' // Insert here the first member
  AND r2.position=2
RETURN
  c, m1, m2, r1, r2
```

The result is the same, but Neo4j generates a graph of the nodes representing the first member, the compounds in which it occurs, and the various second members it combines with, as shown in the following picture.

<p align="center">
<img src="https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/IMG/IMG_2_aequus.jpg" alt="aequus" />
</p>

### All compounds sharing the second member I

```cypher
MATCH
  (c:NominalCompound)-[r1:FORMED_BY]->(m1:Member),
  (c)-[r2:FORMED_BY]->(m2:Member)
WHERE
  r2.position=2
  AND m2.lemma='facio' // Insert here the second member
  AND r1.position=1
RETURN
  c.lemma AS Compound,
  m1.lemma AS First_Member,
  m2.lemma AS Second_Member,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  toLower(c.subtype)
```

This query identifies all compounds sharing the same second member.

### All compounds sharing the second member II (graphic result)

```cypher
MATCH
  (c:NominalCompound)-[r1:FORMED_BY]->(m1:Member),
  (c)-[r2:FORMED_BY]->(m2:Member)
WHERE
  r2.position=2
  AND m2.lemma='volo (them.)' // Insert here the second member
  AND r1.position=1
RETURN
  c, m1, m2, r1, r2
```

The result is the same, but Neo4j generates a graph of the nodes representing the second member, the compounds in which it occurs, and the various first members it combines with, as shown in the following picture.

<p align="center">
<img src="https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/IMG/IMG_3_volo.jpg" alt="volo" />
</p>

## All compound beginning or ending in a certain manner

Since the rules governing the formation of nominal compounds can significantly alter the structure of their members, it is often more useful to insert the 'sequence of letters' at the beginning or end of the compound, corresponding to the form that the members assume in composition.

For example:

*	*caro* \> *carni-* and *carnu-* respectively in *carnifex* and *carnufex*;
*	*facio* \> *-fex* and *-ficus* respectively in *artifex* and *beneficus*.

In both cases, the same member manifests as two distinct allomorphs, each of which may realize different functions or semantic values within the literary works in which they are attested.

### All compounds beginning in the same way I

```cypher
MATCH
  (c:NominalCompound)-[r1:FORMED_BY]->(m1:Member),
  (c)-[r2:FORMED_BY]->(m2:Member)
WHERE
  c.lemma STARTS WITH 'carni' // Insert here the sequence of letters
  AND r1.position=1
  AND r2.position=2
RETURN
  c.lemma AS Compound,
  m1.lemma AS First_Member,
  m2.lemma AS Second_Member,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  toLower(c.lemma)
```

This query identifies all compounds beginning with a specific allomorph of the first member, corresponding to its form in composition.

### All compounds beginning in the same way II (case-insensitive)

```cypher
MATCH
  (c:NominalCompound)-[r1:FORMED_BY]->(m1:Member),
  (c)-[r2:FORMED_BY]->(m2:Member)
WHERE
  c.lemma STARTS WITH toLower('CARNI') // Insert here the sequence of letters
  AND r1.position=1
  AND r2.position=2
RETURN
  c.lemma AS Compound,
  m1.lemma AS First_Member,
  m2.lemma AS Second_Member,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  toLower(c.lemma)
```

The result is the same as the preceding query, but this one is not case-sensitive, which can be useful for proper nouns that coincide with common names.

### All compounds beginning in the same way III (graphic result)

```cypher
MATCH
  (c:NominalCompound)-[r1:FORMED_BY]->(m1:Member),
  (c)-[r2:FORMED_BY]->(m2:Member)
WHERE
  c.lemma STARTS WITH 'carni' // Insert here the sequence of letters
  AND r1.position=1
  AND r2.position=2
RETURN
  c, m1, m2, r1, r2
```

The result is the same, but Neo4j generates a graph of the nodes representing the first member, the compounds in which it occurs, and the various second members it combines with, as shown in the following picture.

<p align="center">
<img src="https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/IMG/IMG_4_carni.jpg" alt="carni" />
</p>

### All compounds ending in the same way I

```cypher
MATCH
  (c:NominalCompound)-[r1:FORMED_BY]->(m1:Member),
  (c)-[r2:FORMED_BY]->(m2:Member)
WHERE
  c.lemma ENDS WITH 'fex' // Insert here the sequence of letters
  AND r1.position=1
  AND r2.position=2
RETURN
  c.lemma AS Compound,
  m1.lemma AS First_Member,
  m2.lemma AS Second_Member,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  toLower(c.lemma)
```

This query identifies all compounds ending with a specific allomorph of the last member, corresponding to its form in composition.

### All compounds ending in the same way II (case-insensitive)

```cypher
MATCH
  (c:NominalCompound)-[r1:FORMED_BY]->(m1:Member),
  (c)-[r2:FORMED_BY]->(m2:Member)
WHERE
  c.lemma ENDS WITH tolower('FEX') // Insert here the sequence of letters
  AND r1.position=1
  AND r2.position=2
RETURN
  c.lemma AS Compound,
  m1.lemma AS First_Member,
  m2.lemma AS Second_Member,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  toLower(c.lemma)
```

The result is the same as the preceding query, but this one is not case-sensitive.

### All compounds ending in the same way III (graphic result)

```cypher
MATCH
  (c:NominalCompound)-[r1:FORMED_BY]->(m1:Member),
  (c)-[r2:FORMED_BY]->(m2:Member)
WHERE
  c.lemma ENDS WITH 'fex' // Insert here the sequence of letters
  AND r1.position=1
  AND r2.position=2
RETURN
  c, m1, m2, r1, r2
```

The result is the same, but Neo4j generates a graph of the nodes representing the second member, the compounds in which it occurs, and the various first members it combines with, as shown in the following picture.

<p align="center">
<img src="https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/IMG/IMG_5_fex.jpg" alt="fex" />
</p>

## All nominal compounds in a specific work

These queries extract the list of all the nominal compounds an author uses in a specific work.
Results are alphabetically sorted.
Number of occurrences, type and subtype are added.

### Title of the work

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[:WRITTEN_BY]->(a:Author)
WHERE
  w.title='Florida' // Insert here the title of the work
RETURN
  a.name AS Author,
  w.title AS Title,
  c.lemma AS Compound,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  tolower(c.lemma),
  tolower(a.name)
```

Note that some titles can refer to multiple works written by different authors.
For instance, this is the case with "Saturae", which can refer to both "A. Persius Flaccus" and "D. Iunius Iuvenalis".
These results are separated by author and title.
To avoid this issue, it is better to use the following queries.

### Acronym of the work I

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work)
WHERE
  w.acronym='APUL. flor.' // Insert here the acronym of the work
RETURN
  w.acronym AS Author_Title,
  c.lemma AS Compound,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  tolower(c.lemma)
```

Unlike the previous one, this query is unambiguous because the acronym is unique.

### Name of the author and title of the work

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  a.name='Apuleius Madaurensis Afer' // Insert here the name of the author
  AND w.title='Florida' // Insert here the title of the work
RETURN
  a.name AS Author,
  w.title AS Title,
  c.lemma AS Compound,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  tolower(c.lemma)
```

The result is identical to the previous one, but the syntax is more verbose.

### Acronym of the work II (graphic result)

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work)
WHERE
  w.acronym='CALP. ecl.' // Insert here the acronym of the work
RETURN
  w, c, r
```

In this case, Neo4j generates a graph depicting the work and the compound it contains, as shown in the following picture.

<p align="center">
<img src="https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/IMG/IMG_6_CALP_ecl.jpg" alt="CALP_ecl" />
</p>

## All nominal compounds in a specific author

These queries extract the list of all nominal compounds used by a specific author in his *opera omnia*.
The results are sorted alphabetically, and the number of occurrences, type, and subtype are included.

### Name of the author

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  a.name='Apuleius Madaurensis Afer' // Insert here the name of the author
RETURN
  a.name AS Author,
  c.lemma AS Compound,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  tolower(c.lemma)
```

The number of occurrences reflects the overall total in the complete work, disregarding the fact that a specific compound may occur in multiple works.

### Name of the author, distinguishing the single works I

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  a.name='Apuleius Madaurensis Afer' // Insert here the name of the author
RETURN
  w.acronym AS Author_Title,
  c.lemma AS Compound,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  tolower(c.lemma),
  tolower(w.acronym)
```

Unlike the previous query, this one specifies the works where each compound occurs, by including the acronym that uniquely identifies each work.

### Name of the author, distinguishing the single works II

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  a.name='Apuleius Madaurensis Afer' // Insert here the name of the author
RETURN
  a.name AS Author,
  w.title AS Title,
  c.lemma AS Compound,
  r.occurrences AS Occurrences,
  c.type AS Type,
  c.subtype AS Subtype
ORDER BY
  tolower(c.lemma),
  tolower(w.title)
```

The output is identical to the previous one, though the author and title are defined in a more verbose manner.

### Name of the author, distinguishing the single works III (graphic result)

```cypher
MATCH
  (c:NominalCompound)<-[r:CONTAINS]-(w:Work),
  (w)-[wr:WRITTEN_BY]->(a:Author)
WHERE
  a.name='Flavius Merobaudes ex Hispania' // Insert here the name of the author
RETURN
  a, w, c, r, wr
```

In this case, Neo4j generates a graph depicting the works of a specific author and the compounds they contain, as shown in the following picture.

<p align="center">
<img src="https://github.com/AI4CH-UniUD/Genus-Compositicium/blob/main/IMG/IMG_7_Merobaudes.jpg" alt="Merobaudes" />
</p>

## Types of compounds recurring in a specific work

These queries investigate which types and subtypes of compounds recur in a given work.
Results are sorted from the most to the least frequent subtype.

### Title of the work

```cypher
MATCH
  (w:Work)-[:CONTAINS]->(n:NominalCompound)
WHERE
  w.title='Saturae' // Insert here the title of the work
RETURN
  n.type AS Type,
  n.subtype AS Subtype,
  COUNT(*) AS Occurrences
ORDER BY
  Occurrences DESC,
  n.subtype
```

Note this query is totally deprecated because some titles can refer to multiple works written by different authors: so results are mixed together.
For instance, this is the case with "Saturae", which can refer to both "A. Persius Flaccus" and "D. Iunius Iuvenalis".
To avoid this issue, it is better to use the following queries.

### Acronym of the work I

```cypher
MATCH
  (w:Work)-[r:CONTAINS]->(n:NominalCompound)
WHERE
  w.acronym='STAT. Theb.' // Insert here the acronym of the work
RETURN
  n.type AS Type,
  n.subtype AS Subtype,
  COUNT(*) AS Occurrences
ORDER BY
  Occurrences DESC,
  n.subtype
```

This query, unlike the previous one, is unambiguous due to the uniqueness of the acronym.

### Acronym of the work II, with percentage

```cypher
// Step 1: calculate the total occurrences and pass it as a variable
MATCH
  (w:Work)-[:CONTAINS]->(n:NominalCompound)
WHERE
  w.acronym='STAT. Theb.' // Insert here the acronym of the work
WITH COUNT(*) AS TotalOccurrences

// Step 2: subquery that receives TotalOccurrences as input variable
CALL (TotalOccurrences) {
  MATCH
    (w:Work)-[:CONTAINS]->(n:NominalCompound)
  WHERE
    w.acronym='STAT. Theb.' // Insert here the acronym of the work
  RETURN
    n.type AS Type,
    n.subtype AS Subtype,
    COUNT(*) AS Occurrences
}

RETURN
  Type,
  Subtype,
  Occurrences,
  ROUND(100.0 * Occurrences / TotalOccurrences, 2) AS Percentage
ORDER BY
  Occurrences DESC,
  Subtype
```

This query produces the same result as the previous one.
It is slightly more complex because it includes a column showing the percentage of occurrences relative to the total number of compounds.

### Name of the author and title of the work I

```cypher
MATCH
  (w:Work)-[r:CONTAINS]->(n:NominalCompound),
  (w)-[r2:WRITTEN_BY]->(a:Author)
WHERE
  a.name='P. Papinius Statius' // Insert here the name of the author
  AND w.title='Thebais' // Insert here the title of the work
RETURN
  n.type AS Type,
  n.subtype AS Subtype,
  COUNT(*) AS Occurrences
ORDER BY
  Occurrences DESC,
  n.subtype
```

The result is identical to the penultimate one, but the syntax is more verbose.

### Name of the author and title of the work II, with percentage

```cypher
// Step 1: calculate the total occurrences and pass it as a variable
MATCH
  (w:Work)-[:CONTAINS]->(n:NominalCompound),
  (w)-[:WRITTEN_BY]->(a:Author)
WHERE
  a.name='P. Papinius Statius' // Insert here the name of the author
  AND w.title='Thebais' // Insert here the title of the work
WITH COUNT(*) AS TotalOccurrences

// Step 2: subquery that receives TotalOccurrences as input variable
CALL (TotalOccurrences) {
  MATCH
    (w:Work)-[:CONTAINS]->(n:NominalCompound),
    (w)-[:WRITTEN_BY]->(a:Author)
  WHERE
    a.name='P. Papinius Statius' // Insert here the name of the author
    AND w.title='Thebais' // Insert here the title of the work
  RETURN
    n.type AS Type,
    n.subtype AS Subtype,
    COUNT(*) AS Occurrences
}

RETURN
  Type,
  Subtype,
  Occurrences,
  ROUND(100.0 * Occurrences / TotalOccurrences, 2) AS Percentage
ORDER BY
  Occurrences DESC,
  Subtype
```

This query yields the same result as the previous one, but is slightly more complex as it includes a column displaying the percentage of occurrences relative to the total number of compounds.

## Types of compounds recurring in a specific author

These queries investigate which types and subtypes of compounds recur in the works of a given author.
Results are sorted from the most to the least frequent subtype.

### Name of the author I

```cypher
MATCH
  (w:Work)-[r:CONTAINS]->(n:NominalCompound),
  (w)-[r2:WRITTEN_BY]->(a:Author)
WHERE
  a.name='P. Vergilius Maro' // Insert here the name of the author
RETURN
  n.type AS Type,
  n.subtype AS Subtype,
  COUNT(*) AS Occurrences
ORDER BY
  Occurrences DESC,
  n.subtype
```

This query is similar to the previous ones, with the only difference that it refers to a specific author's *opera omnia*, which may include more than one work.
If the author has only one work, the results are identical to those of the preceding set of queries.

### Name of the author II, with percentage

```cypher
// Step 1: calculate the total occurrences and pass it as a variable
MATCH
  (w:Work)-[:CONTAINS]->(n:NominalCompound),
  (w)-[:WRITTEN_BY]->(a:Author)
WHERE
  a.name='P. Vergilius Maro' // Insert here the name of the author
WITH COUNT(*) AS TotalOccurrences

// Step 2: subquery that receives TotalOccurrences as input variable
CALL (TotalOccurrences) {
  MATCH
    (w:Work)-[:CONTAINS]->(n:NominalCompound),
    (w)-[:WRITTEN_BY]->(a:Author)
  WHERE
    a.name='P. Vergilius Maro' // Insert here the name of the author
  RETURN
    n.type AS Type,
    n.subtype AS Subtype,
    COUNT(*) AS Occurrences
}

RETURN
  Type,
  Subtype,
  Occurrences,
  ROUND(100.0 * Occurrences / TotalOccurrences, 2) AS Percentage
ORDER BY
  Occurrences DESC,
  Subtype
```

This query yields the same result as the previous one, but is slightly more complex, as it includes a column showing the percentage of occurrences relative to the total number of compounds.

## Types of compounds recurring in a literary subgenre

These queries investigate which types and subtypes of compounds recur within a specific literary subgenre.
Results are sorted from the most to the least frequent subtype.

### Literary subgenre I

```cypher
MATCH
  (w:Work)-[r:CONTAINS]->(n:NominalCompound)
WHERE
  w.subgenre='Historiography' // Insert here the subgenre
RETURN
  n.type AS Type,
  n.subtype AS Subtype,
  COUNT(*) AS Occurrences
ORDER BY
  Occurrences DESC,
  n.subtype
```

This query is similar to the previous ones, with the only difference that it refers to a specific literary subgenre.

### Literary subgenre II, with percentage

```cypher
// Step 1: calculate the total occurrences and pass it as a variable
MATCH
  (w:Work)-[:CONTAINS]->(n:NominalCompound)
WHERE
  w.subgenre='Historiography' // Insert here the subgenre
WITH COUNT(*) AS TotalOccurrences

// Step 2: subquery that receives TotalOccurrences as input variable
CALL (TotalOccurrences) {
  MATCH
    (w:Work)-[:CONTAINS]->(n:NominalCompound)
  WHERE
    w.subgenre='Historiography' // Insert here the subgenre
  RETURN
    n.type AS Type,
    n.subtype AS Subtype,
    COUNT(*) AS Occurrences
}

RETURN
  Type,
  Subtype,
  Occurrences,
  ROUND(100.0 * Occurrences / TotalOccurrences, 2) AS Percentage
ORDER BY
  Occurrences DESC,
  Subtype
```

This query returns the same output as the previous one, with added complexity due to an additional column reporting the percentage of occurrences relative to the total number of compounds.

## Types of compounds recurring in a literary genre

These queries investigate which types and subtypes of compounds recur within a specific literary genre.
As usual, results are sorted from the most to the least frequent subtype.
In general, these queries are less significative because they contain works that are inconsistent, deriving from different subgenres.

### Literary genre I

```cypher
MATCH
  (w:Work)-[r:CONTAINS]->(n:NominalCompound)
WHERE
  w.genre='Poetry' // Insert here the genre
RETURN
  n.type AS Type,
  n.subtype AS Subtype,
  COUNT(*) AS Occurrences
ORDER BY
  Occurrences DESC,
  n.subtype
```

This query is similar to the previous ones, with the only difference that it refers to a specific literary genre (Poetry or Prose).

### Literary genre II, with percentage

```cypher
// Step 1: calculate the total occurrences and pass it as a variable
MATCH
  (w:Work)-[:CONTAINS]->(n:NominalCompound)
WHERE
  w.genre='Poetry' // Insert here the genre
WITH COUNT(*) AS TotalOccurrences

// Step 2: subquery that receives TotalOccurrences as input variable
CALL (TotalOccurrences) {
  MATCH
    (w:Work)-[:CONTAINS]->(n:NominalCompound)
  WHERE
    w.genre='Poetry' // Insert here the genre
  RETURN
    n.type AS Type,
    n.subtype AS Subtype,
    COUNT(*) AS Occurrences
}

RETURN
  Type,
  Subtype,
  Occurrences,
  ROUND(100.0 * Occurrences / TotalOccurrences, 2) AS Percentage
ORDER BY
  Occurrences DESC,
  Subtype
```

This query returns the same output as the previous one, with added complexity due to an additional column reporting the percentage of occurrences relative to the total number of compounds.

## All types of compounds with zero values

The results of the following queries are identical to that of the previous ones, but with one key difference: the list of subtypes does not follow frequency order, but rather a fixed sequence in which *nomina agentis* (1A, 1B, 1C, 1D, 1E, 1F, 1G, 1H, 1Z) are followed by *nomina actionis* (2A, 2B, 2C), *nominal abstracts* (3A, 3B, 3C), *bahuvrīhi* (4A, 4B, 4C, 4D, 5), *determinative compounds* (6A, 6B, 6C), *copulative compounds* (7), *juxtapositions* (Ju), and *grecisms* (Gr).
In addition, zero values are included.

### Title of the work (A)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

OPTIONAL MATCH
  (w:Work)-[:CONTAINS]->(n:NominalCompound {subtype: Subtype}),
  (w)-[:WRITTEN_BY]->(a:Author)
WHERE
  w.acronym = 'STAT. Theb.' // Insert here the acronym of the work

WITH
  Subtype,
  SUM(CASE WHEN n IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences
ORDER BY
  Subtype_Index
```

### Name of the author (B)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

OPTIONAL MATCH
  (w:Work)-[:CONTAINS]->(n:NominalCompound {subtype: Subtype}),
  (w)-[:WRITTEN_BY]->(a:Author)
WHERE
  a.name = 'P. Vergilius Maro' // Insert here the name of the author

WITH
  Subtype,
  SUM(CASE WHEN n IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences
ORDER BY
  Subtype_Index
```

### Literary subgenre (C)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

OPTIONAL MATCH
  (w:Work)-[:CONTAINS]->(n:NominalCompound {subtype: Subtype}),
  (w)-[:WRITTEN_BY]->(a:Author)
WHERE
  w.subgenre='Historiography' // Insert here the subgenre

WITH
  Subtype,
  SUM(CASE WHEN n IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences
ORDER BY
  Subtype_Index
```

### Literary genre (D)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

OPTIONAL MATCH
  (w:Work)-[:CONTAINS]->(n:NominalCompound {subtype: Subtype}),
  (w)-[:WRITTEN_BY]->(a:Author)
WHERE
  w.genre='Poetry' // Insert here the genre

WITH
  Subtype,
  SUM(CASE WHEN n IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences
ORDER BY
  Subtype_Index
```

## Cross queries

The following cross-queries are thus presented according to the possibilities described in the table below.

|       | **A** | **B** | **C** | **D** |
|:-----:|:-----:|:-----:|:-----:|:-----:|
| **A** |  A/A  |  A/B  |  A/C  |  A/D  |
| **B** |  B/A  |  B/B  |  B/C  |  B/D  |
| **C** |  C/A  |  C/B  |  C/C  |  C/D  |
| **D** |  D/A  |  D/B  |  D/C  |  D/D  |

### Work/Work (A/A)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

// Counting per work 1
OPTIONAL MATCH 
  (w1:Work)-[:CONTAINS]->(n1:NominalCompound {subtype: Subtype})
WHERE
  w1.acronym = 'STAT. Theb.' // Insert here the acronym of work 1
WITH
  Subtype,
  SUM(CASE WHEN n1 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Work1

// Counting per work 2
OPTIONAL MATCH 
  (w2:Work)-[:CONTAINS]->(n2:NominalCompound {subtype: Subtype})
WHERE
  w2.acronym = 'VERG. Aen.' // Insert here the acronym of work 2
WITH
  Subtype,
  Occurrences_Work1,
  SUM(CASE WHEN n2 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Work2

WITH
  Subtype,
  Occurrences_Work1,
  Occurrences_Work2,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences_Work1,
  Occurrences_Work2
ORDER BY
  Subtype_Index
```

### Work/Author (A/B)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

// Counting per work
OPTIONAL MATCH
  (w:Work)-[:CONTAINS]->(n:NominalCompound {subtype: Subtype})
WHERE
  w.acronym = 'STAT. Theb.' // Insert here the acronym of the work
WITH Subtype, 
     SUM(CASE WHEN n IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Work

// Counting per author
OPTIONAL MATCH
  (w2:Work)-[:CONTAINS]->(n2:NominalCompound {subtype: Subtype}),
  (w2)-[:WRITTEN_BY]->(a:Author)
WHERE
  a.name = 'P. Vergilius Maro' // Insert here the name of the author
WITH Subtype,
     Occurrences_Work,
     SUM(CASE WHEN n2 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Author

WITH
  Subtype,
  Occurrences_Work,
  Occurrences_Author,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences_Work,
  Occurrences_Author
ORDER BY
  Subtype_Index
```

### Work/Subgenre (A/C)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

// Counting per work
OPTIONAL MATCH
  (w1:Work)-[:CONTAINS]->(n1:NominalCompound {subtype: Subtype})
WHERE
  w1.acronym = 'STAT. Theb.' // Insert here the acronym of the work
WITH Subtype,
     SUM(CASE WHEN n1 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Work

// Counting per subgenre
OPTIONAL MATCH
  (w2:Work)-[:CONTAINS]->(n2:NominalCompound {subtype: Subtype})
WHERE
  w2.subgenre = 'Historiography' // Insert here the subgenre
WITH Subtype,
     Occurrences_Work,
     SUM(CASE WHEN n2 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Subgenre

WITH
  Subtype,
  Occurrences_Work,
  Occurrences_Subgenre,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences_Work,
  Occurrences_Subgenre
ORDER BY
  Subtype_Index
```

### Work/Genre (A/D)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

// Counting per work
OPTIONAL MATCH 
  (w1:Work)-[:CONTAINS]->(n1:NominalCompound {subtype: Subtype})
WHERE
  w1.acronym = 'STAT. Theb.' // Insert here the acronym of the work
WITH
  Subtype,
  SUM(CASE WHEN n1 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Work

// Counting per genre
OPTIONAL MATCH 
  (w2:Work)-[:CONTAINS]->(n2:NominalCompound {subtype: Subtype})
WHERE
  w2.genre = 'Poetry' // Insert here the genre
WITH
  Subtype,
  Occurrences_Work,
  SUM(CASE WHEN n2 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Genre

WITH
  Subtype,
  Occurrences_Work,
  Occurrences_Genre,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences_Work,
  Occurrences_Genre
ORDER BY
  Subtype_Index
```

### Author/Author (B/B)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

// Counting per author 1
OPTIONAL MATCH 
  (w1:Work)-[:CONTAINS]->(n1:NominalCompound {subtype: Subtype}),
  (w1)-[:WRITTEN_BY]->(a1:Author)
WHERE
  a1.name = 'P. Vergilius Maro' // Insert here the name of author 1
WITH
  Subtype,
  SUM(CASE WHEN n1 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Author1

// Counting per author 2
OPTIONAL MATCH 
  (w2:Work)-[:CONTAINS]->(n2:NominalCompound {subtype: Subtype}),
  (w2)-[:WRITTEN_BY]->(a2:Author)
WHERE
  a2.name = 'P. Papinius Statius' // Insert here the name of author 2
WITH
  Subtype,
  Occurrences_Author1,
  SUM(CASE WHEN n2 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Author2

WITH
  Subtype,
  Occurrences_Author1,
  Occurrences_Author2,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences_Author1,
  Occurrences_Author2
ORDER BY
  Subtype_Index
```

### Author/Subgenre (B/C)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

// Counting per author
OPTIONAL MATCH 
  (w1:Work)-[:CONTAINS]->(n1:NominalCompound {subtype: Subtype}),
  (w1)-[:WRITTEN_BY]->(a1:Author)
WHERE
  a1.name = 'P. Vergilius Maro' // Insert here the name of the author
WITH
  Subtype,
  SUM(CASE WHEN n1 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Author

// Counting per subgenre
OPTIONAL MATCH 
  (w2:Work)-[:CONTAINS]->(n2:NominalCompound {subtype: Subtype})
WHERE
  w2.subgenre = 'Historiography' // Insert here the subgenre
WITH
  Subtype,
  Occurrences_Author,
  SUM(CASE WHEN n2 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Subgenre

WITH
  Subtype,
  Occurrences_Author,
  Occurrences_Subgenre,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences_Author,
  Occurrences_Subgenre
ORDER BY
  Subtype_Index
```

### Author/Genre (B/D)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

// Counting per author
OPTIONAL MATCH 
  (w1:Work)-[:CONTAINS]->(n1:NominalCompound {subtype: Subtype}),
  (w1)-[:WRITTEN_BY]->(a1:Author)
WHERE
  a1.name = 'P. Vergilius Maro' // Insert here the name of the author
WITH
  Subtype,
  SUM(CASE WHEN n1 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Author

// Counting per genre
OPTIONAL MATCH 
  (w2:Work)-[:CONTAINS]->(n2:NominalCompound {subtype: Subtype})
WHERE
  w2.genre = 'Poetry' // Insert here the genre
WITH
  Subtype,
  Occurrences_Author,
  SUM(CASE WHEN n2 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Genre

WITH
  Subtype,
  Occurrences_Author,
  Occurrences_Genre,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences_Author,
  Occurrences_Genre
ORDER BY
  Subtype_Index
```

### Subgenre/Subgenre (C/C)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

// Counting per subgenre 1
OPTIONAL MATCH 
  (w1:Work)-[:CONTAINS]->(n1:NominalCompound {subtype: Subtype})
WHERE
  w1.subgenre = 'Historiography' // Insert here subgenre 1
WITH
  Subtype,
  SUM(CASE WHEN n1 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Subgenre1

// Counting per subgenre 2
OPTIONAL MATCH 
  (w2:Work)-[:CONTAINS]->(n2:NominalCompound {subtype: Subtype})
WHERE
  w2.subgenre = 'Novel' // Insert here subgenre 2
WITH
  Subtype,
  Occurrences_Subgenre1,
  SUM(CASE WHEN n2 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Subgenre2

WITH
  Subtype,
  Occurrences_Subgenre1,
  Occurrences_Subgenre2,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences_Subgenre1,
  Occurrences_Subgenre2
ORDER BY
  Subtype_Index
```

### Subgenre/Genre (C/D)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

// Counting per subgenre
OPTIONAL MATCH 
  (w1:Work)-[:CONTAINS]->(n1:NominalCompound {subtype: Subtype})
WHERE
  w1.subgenre = 'High poetry' // Insert here the subgenre
WITH
  Subtype,
  SUM(CASE WHEN n1 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Subgenre

// Counting per genre
OPTIONAL MATCH 
  (w2:Work)-[:CONTAINS]->(n2:NominalCompound {subtype: Subtype})
WHERE
  w2.genre = 'Poetry' // Insert here the genre
WITH
  Subtype,
  Occurrences_Subgenre,
  SUM(CASE WHEN n2 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Genre

WITH
  Subtype,
  Occurrences_Subgenre,
  Occurrences_Genre,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences_Subgenre,
  Occurrences_Genre
ORDER BY
  Subtype_Index
```

### Genre/Genre (D/D)

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D','5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

// Counting per genre 1
OPTIONAL MATCH 
  (w1:Work)-[:CONTAINS]->(n1:NominalCompound {subtype: Subtype})
WHERE
  w1.genre = 'Poetry' // Insert here genre 1
WITH
  Subtype,
  SUM(CASE WHEN n1 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Genre1

// Counting per genre 2
OPTIONAL MATCH 
  (w2:Work)-[:CONTAINS]->(n2:NominalCompound {subtype: Subtype})
WHERE
  w2.genre = 'Prose' // Insert here genre 2
WITH
  Subtype,
  Occurrences_Genre1,
  SUM(CASE WHEN n2 IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences_Genre2

WITH
  Subtype,
  Occurrences_Genre1,
  Occurrences_Genre2,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index

RETURN
  Subtype,
  Occurrences_Genre1,
  Occurrences_Genre2
ORDER BY
  Subtype_Index
```

## Multiple cross queries

```cypher
UNWIND [
  '1A','1B','1C','1D','1E','1F','1G','1H','1Z',
  '2A','2B','2C',
  '3A','3B','3C',
  '4A','4B','4C','4D', '5',
  '6A','6B','6C',
  '7','Ju','Gr'
] AS Subtype

// Insert here between [ ] all the acronyms
UNWIND [
  'VERG. Aen.', 'LUCAN.', 'VAL. FL.', 'SIL.', 'STAT. Ach.', 'STAT. silv.', 'STAT. Theb.'
] AS Acronym

OPTIONAL MATCH
  (w:Work {acronym: Acronym})-[:CONTAINS]->(n:NominalCompound)
WHERE
  n.subtype=Subtype
WITH
  Subtype,
  Acronym,
  SUM(CASE WHEN n IS NOT NULL THEN 1 ELSE 0 END) AS Occurrences,
  CASE Subtype
    WHEN '1A' THEN 1
    WHEN '1B' THEN 2
    WHEN '1C' THEN 3
    WHEN '1D' THEN 4
    WHEN '1E' THEN 5
    WHEN '1F' THEN 6
    WHEN '1G' THEN 7
    WHEN '1H' THEN 8
    WHEN '1Z' THEN 9
    WHEN '2A' THEN 10
    WHEN '2B' THEN 11
    WHEN '2C' THEN 12
    WHEN '3A' THEN 13
    WHEN '3B' THEN 14
    WHEN '3C' THEN 15
    WHEN '4A' THEN 16
    WHEN '4B' THEN 17
    WHEN '4C' THEN 18
    WHEN '4D' THEN 19
    WHEN '5'  THEN 20
    WHEN '6A' THEN 21
    WHEN '6B' THEN 22
    WHEN '6C' THEN 23
    WHEN '7'  THEN 24
    WHEN 'Ju' THEN 25
    WHEN 'Gr' THEN 26
  END AS Subtype_Index
RETURN
  Subtype,
  Acronym,
  Occurrences
ORDER BY
  Subtype_Index,
  Acronym
```

This query analyzes and compares the distribution of compound subtypes across two or more specific works.

In this query, the results are ordered in a different way: each row displays the subtype (column 1), the acronym of the corresponding work (column 2), and the number of occurrences (column 3).

In order to obtain a table comparable to the one produced by the previous query, you need to run one of the following Python scripts: `pivot_csv.py` and `pivot_xlsx.py`, which respectively export the files `pivoted.csv` and `pivoted.xlsx`.

```python
import pandas as pd

# Upload CSV
df = pd.read_csv("file.csv") # Change here the file name

# Table pivot
pivot = df.pivot(index='Subtype', columns='Acronym', values='Occurrences').fillna(0).astype(int)

# Export to pivoted CSV
pivot.to_csv("pivoted.csv") # Change here the file name
```

```python
import pandas as pd

# Upload CSV
df = pd.read_csv("file.csv") # Change here the file name

# Table pivot
pivot = df.pivot(index='Subtype', columns='Acronym', values='Occurrences').fillna(0).astype(int)

# Export to pivoted Excel
pivot.to_excel("pivoted.xlsx", sheet_name="Pivot") # Change here the file name
```

These tables can be used to perform chi-square tests or apply other statistical algorithms.
