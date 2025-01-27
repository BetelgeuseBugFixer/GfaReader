package utils;

import reader.GfaReader;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for rewriting GTF files for use with MiniGFA format.
 * The class provides methods to adjust GTF coordinates for a specific gene
 * by calculating offsets based on sequences in a reference genome and a MiniGFA file.
 */
public class RewriteGtfs {

	/**
	 * Rewrites a GTF file to a new file with coordinates adjusted for MiniGFA format.
	 * The method calculates an offset between the gene sequence in the reference genome
	 * and its corresponding sequence in the MiniGFA file. Then it updates the start and end
	 * coordinates for the gene in the GTF file.
	 *
	 * @param geneId            The ID of the gene to be adjusted.
	 * @param gfaRefPath        The reference path in the MiniGFA file.
	 * @param pathToMiniGfa     Path to the MiniGFA file.
	 * @param pathToGtf         Path to the input GTF file.
	 * @param pathToGenome      Path to the reference genome file.
	 * @param pathToGenomeIndex Path to the genome index file.
	 * @param outputFilePath    Path where the updated GTF file will be written.
	 * @throws IOException If an I/O error occurs while reading or writing files.
	 */
	public static void rewriteGtfForMiniGfa(String geneId, String gfaRefPath, String pathToMiniGfa, String pathToGtf, String pathToGenome,
											String pathToGenomeIndex, String outputFilePath) throws IOException {
		// Calculate offset based on the GTF and MiniGFA files
		int offset = getOffset(pathToGtf, pathToGenome, pathToGenomeIndex, geneId, pathToMiniGfa, gfaRefPath);
		BufferedReader reader = new BufferedReader(new FileReader(pathToGtf));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));

		String line;
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) {
				continue;
			} else if (line.startsWith("#!")) {
				writer.write(line);  // Write header lines unchanged
				writer.newLine();
			} else {
				String[] fields = line.split("\t");
				if (geneId.equals(extractGeneID(fields[8]))) {
					// Adjust the start and end positions based on the calculated offset
					fields[3] = String.valueOf(Integer.parseInt(fields[3]) - offset);
					fields[4] = String.valueOf(Integer.parseInt(fields[4]) - offset);
					writer.write(String.join("\t", fields));  // Write the updated GTF entry
					writer.newLine();
				}
			}
		}
		writer.flush();
		writer.close();
	}

	/**
	 * Overloaded method for rewriting a GTF file. It generates an output file path by adding
	 * a suffix based on the gene ID and then calls the main method to perform the actual rewrite.
	 *
	 * @param geneId            The ID of the gene to be adjusted.
	 * @param gfaRefPath        The reference path in the MiniGFA file.
	 * @param pathToMiniGfa     Path to the MiniGFA file.
	 * @param pathToGtf         Path to the input GTF file.
	 * @param pathToGenome      Path to the reference genome file.
	 * @param pathToGenomeIndex Path to the genome index file.
	 * @throws IOException If an I/O error occurs while reading or writing files.
	 */
	public static void rewriteGtfForMiniGfa(String geneId, String gfaRefPath,
											String pathToMiniGfa, String pathToGtf, String pathToGenome,
											String pathToGenomeIndex) throws IOException {
		File gtfFile = new File(pathToGtf);
		String outputFilePath = addSuffixToFileName(gtfFile, "_" + geneId + "_mini");

		rewriteGtfForMiniGfa(geneId, gfaRefPath, pathToMiniGfa, pathToGtf, pathToGenome,
				pathToGenomeIndex, outputFilePath);
	}

	/**
	 * Adds a suffix to a file's name before the file extension.
	 *
	 * @param file   The original file.
	 * @param suffix The suffix to append to the file name.
	 * @return A new file path with the suffix added.
	 */
	private static String addSuffixToFileName(File file, String suffix) {
		String directory = file.getParent();
		String fileNameAndType = file.getName();
		String[] fileNameSplit = fileNameAndType.split("\\.");
		String fileType = fileNameSplit[fileNameSplit.length - 1];

		StringBuilder newFileBuilder = new StringBuilder(directory);
		newFileBuilder.append("/");
		for (int i = 0; i < fileNameSplit.length - 1; i++) {
			newFileBuilder.append(fileNameSplit[i]).append(".");
		}
		newFileBuilder.deleteCharAt(newFileBuilder.length() - 1);
		newFileBuilder.append(suffix);
		newFileBuilder.append(".");
		newFileBuilder.append(fileType);

		return newFileBuilder.toString();
	}

	/**
	 * Calculates the offset between the gene's position in the reference genome and its position in the MiniGFA file.
	 *
	 * @param pathToGtf         Path to the GTF file.
	 * @param pathToGenome      Path to the reference genome file.
	 * @param pathToGenomeIndex Path to the genome index file.
	 * @param geneId            The gene's identifier.
	 * @param pathToMiniGfa     Path to the MiniGFA file.
	 * @param gfaRefPath        The reference path in the MiniGFA file.
	 * @return The calculated offset between the reference genome and MiniGFA sequence.
	 * @throws IOException If an I/O error occurs while reading files.
	 */
	private static int getOffset(String pathToGtf, String pathToGenome, String pathToGenomeIndex,
								 String geneId, String pathToMiniGfa, String gfaRefPath) throws IOException {
		String gtfSeq = getGeneSeqFromReferenceGenome(pathToGtf, pathToGenome, pathToGenomeIndex, geneId);
		int gtfStartingPoint = getGeneStartingPoint(geneId, pathToGtf);

		GfaReader gfaReader = new GfaReader(pathToMiniGfa);
		String gfaSeq = gfaReader.getPathSeq(gfaRefPath);

		return getOffsetForGtfCoordinates(gfaSeq, gtfSeq, gtfStartingPoint);
	}

	/**
	 * Extracts the gene sequence from the reference genome.
	 *
	 * @param pathToGtf    Path to the GTF file.
	 * @param pathToGenome Path to the reference genome file.
	 * @param pathToIndex  Path to the genome index file.
	 * @param geneId       The gene's identifier.
	 * @return The gene sequence as a string.
	 * @throws IOException If an I/O error occurs while reading files.
	 */
	private static String getGeneSeqFromReferenceGenome(String pathToGtf, String pathToGenome,
														String pathToIndex, String geneId) throws IOException {
		BufferedReader gtfReader = new BufferedReader(new FileReader((pathToGtf)));
		GenomeSequenceExtractor extractor = new GenomeSequenceExtractor(pathToGenome, pathToIndex);

		String line;
		while ((line = gtfReader.readLine()) != null) {
			if (line.startsWith("#") || line.isEmpty()) {
				continue;
			}
			String[] fields = line.split("\t");
			if (fields[2].equals("gene")) {
				if (geneId.equals(extractGeneID(fields[8]))) {
					int start = Integer.parseInt(fields[3]);
					int end = Integer.parseInt(fields[4]);

					if (fields[6].equals("-")) {
						return extractor.getSequence(fields[0], start, end);  // Reverse strand
					}
					return extractor.getSequence(fields[0], start, end);  // Forward strand
				}
			}
		}
		throw new IllegalArgumentException("Could not find gene id in gtf");
	}

	/**
	 * Retrieves the starting point of the gene from the GTF file.
	 *
	 * @param geneId    The gene's identifier.
	 * @param pathToGtf Path to the GTF file.
	 * @return The starting position of the gene in the GTF file.
	 * @throws IOException If an I/O error occurs while reading the GTF file.
	 */
	private static int getGeneStartingPoint(String geneId, String pathToGtf) throws IOException {
		BufferedReader gtfReader = new BufferedReader(new FileReader((pathToGtf)));
		String line;
		while ((line = gtfReader.readLine()) != null) {
			if (line.startsWith("#") || line.isEmpty()) {
				continue;
			}
			String[] fields = line.split("\t");
			if (fields[2].equals("gene")) {
				if (geneId.equals(extractGeneID(fields[8]))) {
					return Integer.parseInt(fields[3]);
				}
			}
		}
		return -1;
	}

	/**
	 * Extracts the gene ID from the attribute field in the GTF file.
	 *
	 * @param attributes The attribute field in the GTF line (last column).
	 * @return The gene ID as a string.
	 */
	private static String extractGeneID(String attributes) {
		Pattern p = Pattern.compile("gene_id \"([^\"]+)\"");
		Matcher m = p.matcher(attributes);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}

	/**
	 * Calculates the offset for adjusting GTF coordinates based on sequence comparison.
	 *
	 * @param gfaSeq        The sequence from the MiniGFA file.
	 * @param gtfSeq        The sequence from the reference genome.
	 * @param startingPoint The starting point of the gene in the GTF file.
	 * @return The calculated offset.
	 */
	private static int getOffsetForGtfCoordinates(String gfaSeq, String gtfSeq, int startingPoint) {
		// Find the position of the GTF sequence in the GFA sequence
		int offset = gfaSeq.indexOf(gtfSeq);
		if (offset < 0) {
			throw new IllegalArgumentException("Gene sequence not found in MiniGFA sequence.");
		}
		return startingPoint - offset - 1;
	}
}
