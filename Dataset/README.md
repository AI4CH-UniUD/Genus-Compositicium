<div align="center">

# Nominal Compounds Dataset

</div>

This repository contains linguistic data structured for database import via a Java script.
The data focuses on **nominal compounds** attested in Latin texts and is conceived for lexicographic and philological research.

## File `Compounds.xlsx`

This Excel file includes a comprehensive list of all nominal compounds stored in the database.
It consists of two distinct sheets.

### Sheet `COMPOUNDS`

The first sheet provides detailed information for each compound.

| **Column**     | **Description**                       |
|----------------|---------------------------------------|
| A              | Lemma (alphabetical order)            |
| B              | Lexical category                      |
| C              | Type of the compound                  |
| D              | Sub-type of the compound              |
| E              | First member                          |
| F              | Lexical category of the first member  |
| G              | Second member                         |
| H              | Lexical category of the second member |
| I *(optional)* | Third member (if present)             |
| J *(optional)* | Lexical category of the third member  |
| K *(optional)* | Fourth member (if present)            |
| L *(optional)* | Lexical category of the fourth member |

### Sheet `DUPLICATES`

The second sheet lists duplicated lemmas for disambiguation purposes.

| **Column** | **Description**                                 |
|------------|-------------------------------------------------|
| A          | Base form of the duplicate (alphabetical order) |
| B          | Secondary form of the duplicate                 |

## Folder `Authors`

This folder contains individual files, each referring to a specific literary work.
These files document the nominal compounds found in that work.

### File Structure

| **Cell** | **Content**                                 |
|----------|---------------------------------------------|
| A1       | Latin name of the author                    |
| B1       | Century of birth                            |
| C1       | Century of death                            |
| A2       | Full title of the work                      |
| B2       | Standard citation (according to the [*Thesaurus linguae Latinae*](https://thesaurus.badw.de/en/tll-digital/index/a.html)) |
| A3       | Literary genre                              |
| B3       | Subgenre                                    |

From row 6 onward:

* Column A: Alphabetical list of nominal compounds attested in the work;
* Column B: Number of attestations for each compound.

## Purpose

This dataset supports:

* Historical and comparative linguistic analysis;
* Lexicographic projects on Latin compounds;
* Digital philology and digital humanities research.

## Contributing

Contributions, corrections, and extensions are warmly welcome: please write to <genus.compositicium@gmail.com>.
