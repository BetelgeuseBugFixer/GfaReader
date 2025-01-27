# GfaReader

The `GfaReader` class provides functionality to read and parse [GFA (Graphical Fragment Assembly)](https://github.com/GFA-spec/GFA-spec) files, extracting segments and paths for further processing. It is designed to handle large files efficiently by indexing relevant information with byte references.

**important**: Since GFA specification is... weird... this reader is designed for GFA files from [the original pangenome paper](https://www.nature.com/articles/s41586-023-05896-x#Abs1).
These can be found [here](https://s3-us-west-2.amazonaws.com/human-pangenomics/index.html?prefix=pangenomes/freeze/freeze1/pggb/chroms/).

---
## GFA and Pangenomes


GFA and Pangenomes
A GFA (Graphical Fragment Assembly) file is a graph-based format for representing genomic data. It stores genomic segments (nodes) and their relationships (edges), allowing for the representation of different genomic variations.

### mini-GFA

A mini-GFA is a cut-out version of a GFA, that contains only one gene.

---

In a pangenome, the GFA file aligns multiple individual genomes against reference paths. These reference paths serve as a baseline, while the individual genomes are represented as alternative paths, capturing genetic variations across different individuals.
## Features

- Read GFA file:
  - Provides methods to:
      - Retrieve the sequence of a specific segment
      - Get the length of a segment
      - Access all paths in the file using an iterator
  - Efficiently handles file reading using byte-level indexing for optimal performance
- Rewrite GTF files for mini-GFA
  - This method adopts a Genome-Wide GTF file for a specific mini-GFA
- BioUtils methods
  - Get the reverse complement of a DNA String
---

## GfaReader Usage

### 1. **Instantiating the GfaReader**

To create an instance of `GfaReader`, provide the file path to a GFA file:

```java
GfaReader gfaReader = new GfaReader("path/to/file.gfa");
```
### 3. **Working with Paths**

- Get the number of paths:
```java
int numberOfPaths = gfaReader.getNumberOfPaths();
System.out.println("Number of Paths: " + numberOfPaths);
```
- Iterate over paths:
```java
for (SimplePath path : gfaReader) {
    System.out.println("Path Name: " + path.getPathName());
    System.out.println("Path Length: " + path.getLength());
}
```
### 4. **Path Structure**
Paths in GFA files represent sequences of segments with orientations (```+``` or ```-```). 
The ```SimplePath``` class provides tools to analyze these paths:

- Check if a segment at a certain index in the path is reverse-oriented:
```java
  boolean isReverse = SimplePath.checkIfSegmentAtIsReverse(0);
  System.out.println("Is Reverse: " + isReverse);
```

- Get segment names with orientation:

```java
String segmentName = SimplePath.getSegmentWithOrientationAt(0);
System.out.println("Segment Name: " + segmentName);
```
- Map segment positions within a path. 
  - This method returns a Hashmap, where each segment with orientation is mapped to all it occurrences in the path 
```java
HashMap<String, ArrayList<Integer>> segmentPositions = path.getSegmentToPositions();
```

### 5. **Working with Segments**

- Get a segments sequence:
```java
String segmentSeq = gfaReader.getSegmentSeq("segmentName");
System.out.println("Sequence: " + segmentSeq);
```
- Get a segments length:

```java
int segmentLength = gfaReader.getSegmentLength("segmentName");
System.out.println("Length: " + segmentLength);

```

---

## Other Methods Usage

### 1. rewriteGtfForMiniGfa

- This method generates a new GTF file based on a mini GFA (pangenome cut out for a single gene). It retrieves the gene sequence, compares it with the GFA sequence to determine the offset, and writes the updated GTF file.
- Parameters:
  - `geneId` (String): The gene ID (e.g., "ENSG00000214216"). 
  - `gfaRefPath` (String): The reference path for the GFA (e.g., "grch38#0#3"). 
  - `pathToMiniGfa` (String): The file path to the mini GFA. 
  - `pathToGtf` (String): The file path to the original GTF file. 
  - `pathToGenome` (String): The file path to the genome FASTA file. 
  - `pathToGenomeIndex` (String): The file path to the genome index file (.fai). 
  - `outputFilePath` (String, optional): The output file path for the new GTF. If not specified, the output file will be saved in the same directory as the input GTF with the name `<GTF file name>_<gene id>_mini.gtf`.

```java
rewriteGtfForMiniGfa(geneId, gfaRefPath, miniGfaPath, pathToGtf, pathToGenome, pathToGenomeIndex, out);
```

### 2. reverseComplement
- Generates the reverse complement of a DNA sequence by reversing the input and replacing each base with its complement
- Parameters:
  - `seq` (String): The DNA sequence to process
```java
String reverseComp = BioUtils.reverseComplement("ATGC"); // Output: "GCAT"
```
