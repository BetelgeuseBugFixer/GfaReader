package reader;

import utils.ByteReference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;

/**
 * SimplePathSaver is responsible for managing and retrieving path data from a GFA file.
 * It stores the start byte and length of each path line, allowing efficient random access
 * to path information using a ByteReference.
 */
public class SimplePathSaver {
	private final HashMap<String, ByteReference> pathStartBytes;
	private final RandomAccessFile byteReader;

	/**
	 * Constructs a SimplePathSaver for the specified GFA file.
	 *
	 * @param filePath the file path to the GFA file
	 * @throws FileNotFoundException if the file cannot be found
	 */
	protected SimplePathSaver(String filePath) throws FileNotFoundException {
		this.pathStartBytes = new HashMap<>();
		this.byteReader = new RandomAccessFile(filePath, "r");
	}

	/**
	 * Retrieves a SimplePath object corresponding to the specified path name (haplotype).
	 *
	 * @param haplotype the name of the path
	 * @return a SimplePath object representing the path
	 * @throws IOException if an error occurs while reading the file
	 */
	protected SimplePath getPath(String haplotype) throws IOException {
		ByteReference byteReference = pathStartBytes.get(haplotype);
		// Read the corresponding bytes for the path
		byte[] line = new byte[byteReference.length()];
		byteReader.seek(byteReference.startByte());
		byteReader.read(line);
		// Convert the byte array to a string and create a SimplePath object
		return new SimplePath(new String(line));
	}

	/**
	 * Parses a GFA line that represents a path ('P' line) and stores its byte reference.
	 *
	 * @param line      the line from the GFA file
	 * @param startByte the starting byte position of the line in the file
	 */
	protected void parseGfaLine(String line, long startByte) {
		String name = line.substring(2, line.indexOf('\t', 2));
		ByteReference byteReference = new ByteReference(startByte, line.length());
		this.pathStartBytes.put(name, byteReference);
	}

	/**
	 * Retrieves a set of all path names stored in the GFA file.
	 *
	 * @return a HashSet containing all path names
	 */
	protected HashSet<String> getPathNames() {
		return new HashSet<>(this.pathStartBytes.keySet());
	}

	/**
	 * Retrieves the total number of paths stored in the GFA file.
	 *
	 * @return the number of paths
	 */
	public int getNumberOfPaths() {
		return this.pathStartBytes.size();
	}
}
