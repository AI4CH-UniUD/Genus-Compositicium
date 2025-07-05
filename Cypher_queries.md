<div align="center">

# Cypher queries for the graph database of Latin compounds

</div>

The file `Index.xlsx` contains all the information that the queries presented in this document can refer to.

## Occurences of a single compound

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

The result is the same as the previous one, but with more detailed author and title information.

### Single author I

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

This query examines how often a given compound appears in the *corpus* of a single author.

### Single author II

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

The result is the same as the previous one, but the definitions of the author and title are more detailed.

### Single work I

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

### Single work II

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

### All compounds sharing the first member

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

This query identifies compounds sharing the same first member.

### All compounds sharing the second member

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

This query identifies compounds sharing the same second member.

## All nominal compounds in a single work

These queries extract the list of all the nominal compounds an author uses in a single work.
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

### Acronym of the work

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

## All nominal compounds in a single author

These queries extract the list of all nominal compounds used by an author in his *opera omnia*.
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

The number of occurrences reflects the overall total in the complete work, disregarding the fact that a single compound may occur in multiple works.

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

## Types of compounds recurring in a single work

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

## Types of compounds recurring in a single author

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

This query is similar to the previous ones, with the only difference that it refers to a single author's *opera omnia*, which may include more than one work.
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

These queries investigate which types and subtypes of compounds recur within a literary subgenre.
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

These queries investigate which types and subtypes of compounds recur within a literary genre.
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

This query is similar to the previous ones, with the only difference that it refers to a literary genre (Poetry or Prose).

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
