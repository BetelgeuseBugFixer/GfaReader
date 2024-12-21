package reader;

import utils.ByteReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

/**
 * SimpleSegmentSaver is responsible for managing and retrieving segment data from a GFA file.
 * It stores the start byte and length of each segment line, allowing efficient random access
 * to segment sequences using a ByteReference.
 */
public class SimpleSegmentSaver {
	private final HashMap<String, ByteReference> segmentToByteRef; // Maps segment names to their byte references in the file
	private final RandomAccessFile byteReader;                    // Provides random access to the file for reading segments

	/**
	 * Constructs a SimpleSegmentSaver for the specified GFA file.
	 *
	 * @param filePath the file path to the GFA file
	 * @throws FileNotFoundException if the file cannot be found
	 */
	public SimpleSegmentSaver(String filePath) throws FileNotFoundException {
		this.segmentToByteRef = new HashMap<>();
		this.byteReader = new RandomAccessFile(filePath, "r");
	}

	/**
	 * Retrieves the DNA sequence of a segment by its name.
	 *
	 * @param segmentName the name of the segment
	 * @return the DNA sequence as a string
	 * @throws IOException if an error occurs while reading the file
	 */
	protected String getSegmentSeq(String segmentName) throws IOException {
		// Retrieve the ByteReference for the segment
		ByteReference byteReference = segmentToByteRef.get(segmentName);

		// Create a byte array to store the sequence
		byte[] seq = new byte[byteReference.length()];

		// Seek to the start byte and read the sequence
		byteReader.seek(byteReference.startByte());
		byteReader.read(seq);

		// Convert the byte array to a string and return it
		return new String(seq);
	}

	/**
	 * Retrieves the length of a segment by its name.
	 *
	 * @param segmentName the name of the segment
	 * @return the length of the segment
	 */
	protected int getSegmentLength(String segmentName) {
		// Retrieve the length from the ByteReference
		return segmentToByteRef.get(segmentName).length();
	}

	/**
	 * Parses a GFA line that represents a segment ('S' line) and stores its byte reference.
	 *
	 * @param line      the line from the GFA file
	 * @param startByte the starting byte position of the line in the file
	 */
	protected void parseGfaLine(String line, long startByte) {
		// Split the line into fields separated by tabs
		String[] fields = line.split("\t");

		// Extract the segment name (field[1])
		String name = fields[1];

		// Compute the starting byte of the sequence and its length
		ByteReference byteReference = new ByteReference(
				startByte + fields[0].length() + fields[1].length() + 2, // Start byte: after "S<tab><name><tab>"
				fields[2].length()                                       // Length of the sequence (field[2])
		);

		// Store the ByteReference in the map
		this.segmentToByteRef.put(name, byteReference);
	}
}
