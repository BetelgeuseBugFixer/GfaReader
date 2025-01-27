package utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A class to extract DNA sequence data from a FASTA file based on an index.
 * This class allows for the extraction of a subsequence from a specific chromosome
 * within a specified range (start and end positions).
 */
class GenomeSequenceExtractor {
	// The FASTA file containing the genome data
	File fasta;

	// The index used to navigate the FASTA file
	GenomeIndex index;

	// RandomAccessFile to allow efficient reading of the FASTA file
	RandomAccessFile raf;

	/**
	 * Constructor to initialize the genome sequence extractor with a FASTA file and an index file.
	 * The FASTA file is opened for random access, and the index is used to navigate the file.
	 *
	 * @param fasta Path to the FASTA file containing genome sequences.
	 * @param idx   Path to the index file used to locate sequences within the FASTA file.
	 * @throws IOException If an error occurs while accessing the FASTA file or index.
	 */
	 GenomeSequenceExtractor(String fasta, String idx) throws IOException {
		this.fasta = new File(fasta);
		this.raf = new RandomAccessFile(this.fasta, "r");
		this.index = new GenomeIndex(idx);
	}

	/**
	 * Calculates the number of bytes needed to extract a sequence from the FASTA file.
	 * This takes into account the start and end positions, as well as the line length and newline character length in the file.
	 *
	 * @param start        The starting position of the sequence (1-based).
	 * @param end          The ending position of the sequence (1-based).
	 * @param lineLength   The number of bases per line in the FASTA file.
	 * @param lineLengthNL The number of characters per line, including newline characters.
	 * @return The total number of bytes to be extracted from the file.
	 */
	 private static long calculateBytesToExtract(long start, long end, long lineLength, long lineLengthNL) {
		start--; // Convert to 0-based indexing
		end--;   // Convert to 0-based indexing

		// Calculate the number of newlines between the start and end positions
		long newLineChars = (end / lineLength) - (start / lineLength);

		// Calculate the difference between the line length with and without newlines
		long newLineCharLength = lineLengthNL - lineLength;

		// Calculate the total bytes to extract, including newlines
		return (end - start) + (newLineChars * newLineCharLength) + 1;
	}

	/**
	 * Calculates the start byte position for a sequence in the FASTA file based on the start position.
	 * This accounts for the chromosomal start position, line length, and the presence of newline characters.
	 *
	 * @param start        The starting position of the sequence (1-based).
	 * @param chromStart   The starting byte of the chromosome in the FASTA file.
	 * @param lineLength   The number of bases per line in the FASTA file.
	 * @param lineLengthNL The number of characters per line, including newline characters.
	 * @return The byte offset for the start position of the sequence in the FASTA file.
	 */
	private long calculateStartByte(long start, long chromStart, long lineLength, long lineLengthNL) {
		start--; // Convert to 0-based indexing
		long newLineCharLength = lineLengthNL - lineLength;

		// Calculate the number of lines before the start position
		long lines = start / lineLength;

		// Calculate the starting byte position for the sequence
		return chromStart + (newLineCharLength * lines) + start;
	}

	/**
	 * Extracts a specific DNA sequence from the FASTA file based on chromosome and position range.
	 *
	 * @param chr   The name of the chromosome from which the sequence should be extracted.
	 * @param start The start position of the sequence (1-based).
	 * @param end   The end position of the sequence (1-based).
	 * @return A byte array containing the extracted DNA sequence.
	 * @throws IOException If an error occurs while reading the FASTA file.
	 */
	private byte[] extractSingleSequence(String chr, long start, long end) throws IOException {
		// Retrieve the data for the specified chromosome from the index
		long[] data = this.index.getData(chr);

		// Unpack the index data for chromosome start byte, line length, and newline length
		long chromStartByte = data[0];
		long lineLength = data[1];
		long lineLengthNL = data[2];

		// Calculate the byte position where the sequence starts
		long startByte = calculateStartByte(start, chromStartByte, lineLength, lineLengthNL);

		// Move the file pointer to the start position
		raf.seek(startByte);

		// Read the bytes of the sequence into an array
		byte[] extractedBytes = new byte[(int) calculateBytesToExtract(start, end, lineLength, lineLengthNL)];

		// Read the sequence bytes into the byte array
		raf.readFully(extractedBytes);

		return extractedBytes;
	}

	/**
	 * Returns the DNA sequence as a string from the specified chromosome and position range.
	 * The sequence is returned as a string without newline characters.
	 *
	 * @param chr   The name of the chromosome from which the sequence should be extracted.
	 * @param start The start position of the sequence (1-based).
	 * @param end   The end position of the sequence (1-based).
	 * @return A string representing the extracted DNA sequence.
	 * @throws IOException If an error occurs while reading the FASTA file.
	 */
	 String getSequence(String chr, long start, long end) throws IOException {
		// Convert the extracted byte array into a string and remove newline characters
		return new String(extractSingleSequence(chr, start, end)).replace("\n", "");
	}
}
