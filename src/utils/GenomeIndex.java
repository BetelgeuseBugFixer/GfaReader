package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * A class to parse and store an index of genome data.
 * This class reads an index file that maps chromosomes to their start byte,
 * line lengths, and newline-character-adjusted line lengths. The class allows
 * efficient retrieval of this information for any given chromosome.
 */
class GenomeIndex {
	// A HashMap to store the chromosome data, mapping chromosome names to an array of three long values:
	// [start byte, characters per line, characters per line including newline characters]
	HashMap<String, long[]> chromToData;

	/**
	 * Constructor that initializes the index by reading a file and parsing its contents.
	 * The file is expected to contain tab-separated entries with chromosome names and their associated data:
	 * <chromosome name> <other fields> <start byte> <line length> <line length with newline>.
	 *
	 * @param file The path to the index file containing chromosome data.
	 * @throws IOException If an error occurs while reading the file.
	 */
	GenomeIndex(String file) throws IOException {
		this.chromToData = new HashMap<>();

		// Read the file line by line
		try (BufferedReader br = new BufferedReader(new java.io.FileReader(file))) {
			String entry;
			while ((entry = br.readLine()) != null) {
				// Skip empty lines
				if (entry.isEmpty()) {
					continue;
				}

				// Split the line by tab characters to extract the data
				String[] data = entry.split("\t");

				// Parse the relevant fields into long values
				long startByte = Long.parseLong(data[2]);
				long charsInLine = Long.parseLong(data[3]);
				long charsWithNewLine = Long.parseLong(data[4]);

				// Store the data in the map for easy lookup by chromosome name
				chromToData.put(data[0], new long[]{startByte, charsInLine, charsWithNewLine});
			}
		}
	}

	/**
	 * Retrieves the genome data for a given chromosome. This data includes the start byte,
	 * the number of characters per line, and the number of characters per line including newline characters.
	 *
	 * @param chrom The name of the chromosome whose data is to be retrieved.
	 * @return A long array containing [start byte, characters per line, characters per line with newline],
	 * or null if the chromosome is not found.
	 */
	long[] getData(String chrom) {
		return this.chromToData.get(chrom);
	}
}
