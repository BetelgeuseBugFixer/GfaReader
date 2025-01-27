package utils;

import java.util.HashMap;

/**
 * A utility class for biological sequence manipulations
 */
public class BioUtils {

	/**
	 * A HashMap that defines the complement of DNA bases.
	 * This map is used to get the complementary base for each DNA nucleotide.
	 */
	public static final HashMap<Character, Character> complement = new HashMap<>() {{
		put('A', 'T'); // Adenine pairs with Thymine
		put('T', 'A'); // Thymine pairs with Adenine
		put('C', 'G'); // Cytosine pairs with Guanine
		put('G', 'C'); // Guanine pairs with Cytosine
	}};

	/**
	 * Generates the reverse complement of a given DNA sequence.
	 * <p>
	 * This method takes a DNA sequence, reverses it, and then replaces each base
	 * with its complementary base (A ↔ T, C ↔ G). For example, the reverse complement
	 * of the sequence "ATGC" would be "GCAT".
	 * </p>
	 *
	 * @param seq The DNA sequence for which the reverse complement is generated.
	 * @return The reverse complement of the input DNA sequence.
	 */
	public static String reverseComplement(String seq) {
		// Initialize a StringBuilder to construct the reverse complement sequence
		StringBuilder sb = new StringBuilder();

		// Iterate over the sequence in reverse order and append the complement of each base
		for (int i = seq.length() - 1; i >= 0; i--) {
			// Get the complement of the base and append it to the StringBuilder
			sb.append(complement.get(seq.charAt(i)));
		}

		// Return the reverse complement sequence as a string
		return sb.toString();
	}
}
