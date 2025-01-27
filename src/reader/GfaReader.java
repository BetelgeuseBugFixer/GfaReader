package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static utils.BioUtils.reverseComplement;

/**
 * GfaReader is a utility class for reading and parsing GFA (Graphical Fragment Assembly) files.
 * It processes segment and path data from the file and provides methods to access this information.
 * The class also implements Iterable, allowing iteration over paths in the GFA.
 */
public class GfaReader implements Iterable<SimplePath> {
	private SimpleSegmentSaver segmentSaver; // Helper for managing segment-related data
	private SimplePathSaver pathSaver; // Helper for managing path-related data

	/**
	 * Constructs a GfaReader and parses the specified GFA file.
	 *
	 * @param pathToGfa the file path to the GFA file
	 * @throws IOException if an error occurs while reading the file (duh)
	 */
	public GfaReader(String pathToGfa) throws IOException {
		readGfa(pathToGfa);
	}

	/**
	 * reads a gfa
	 *
	 * @param pathToGfa file path to gfa
	 * @throws IOException when there is a problem reading the file (duh)
	 */
	private void readGfa(String pathToGfa) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(pathToGfa));

		// Initialize helpers for managing segments and paths
		this.segmentSaver = new SimpleSegmentSaver(pathToGfa);
		this.pathSaver = new SimplePathSaver(pathToGfa);
		String line;
		long currentByte = 0;
		while ((line = reader.readLine()) != null) {
			if (line.isEmpty()) {
				continue;
			}
			if (line.startsWith("S")) {
				this.segmentSaver.parseGfaLine(line, currentByte);
			} else if (line.startsWith("P")) {
				this.pathSaver.parseGfaLine(line, currentByte);
			}
			// add +1 for line ending
			currentByte += line.length() + 1;
		}
	}

	/**
	 * Retrieves the sequence of a specified segment.
	 *
	 * @param segmentName the name of the segment
	 * @return the sequence of the segment
	 * @throws IOException if an error occurs while accessing the segment data
	 */
	public String getSegmentSeq(String segmentName) throws IOException {
		return this.segmentSaver.getSegmentSeq(segmentName);
	}

	/**
	 * Retrieves the sequence of a specified segment.
	 *
	 * @param pathName the name of the path
	 * @return the sequence of the path
	 * @throws IOException if an error occurs while accessing the segment data
	 */
	public String getPathSeq(String pathName) throws IOException {
		SimplePath path = this.pathSaver.getPath(pathName);
		StringBuilder sb = new StringBuilder();

		for (int segmentIndex = 0; segmentIndex < path.getLength(); segmentIndex++) {
			// get the current segment id
			String segmentId = path.getSegmentWithoutOrientationAt(segmentIndex);

			// get the sequence, build reverse compliment if necessary
			String segmentSeq = this.segmentSaver.getSegmentSeq(segmentId);
			if (path.checkIfSegmentAtIsReverse(segmentIndex)) {
				segmentSeq = reverseComplement(segmentSeq);
			}
			sb.append(segmentSeq);
		}
		return sb.toString();
	}

	/**
	 * Retrieves the length of a specified segment.
	 *
	 * @param segmentName the name of the segment
	 * @return the length of the segment
	 */
	public int getSegmentLength(String segmentName) {
		return this.segmentSaver.getSegmentLength(segmentName);
	}

	/**
	 * Retrieves the total number of paths in the GFA file.
	 *
	 * @return the number of paths
	 */
	public int getNumberOfPaths() {
		return this.pathSaver.getNumberOfPaths();
	}

	/**
	 * Provides an iterator for iterating over paths in the GFA file.
	 *
	 * @return an instance of PathIterator
	 */
	@Override
	public Iterator<SimplePath> iterator() {
		return new PathIterator();
	}

	/**
	 * PathIterator is an inner class that implements an iterator over SimplePath objects.
	 * It fetches paths sequentially from the path data managed by SimplePathSaver.
	 */
	private class PathIterator implements Iterator<SimplePath> {
		private final ArrayList<String> pathNames; // List of path names
		private final int size; // Total number of paths
		private int index; // Current index in the list of path names

		/**
		 * Constructs a PathIterator and initializes its state.
		 */
		public PathIterator() {
			this.pathNames = new ArrayList<>(pathSaver.getPathNames());
			index = -1;
			this.size = pathNames.size();
		}

		/**
		 * Checks if there are more paths to iterate over.
		 *
		 * @return true if there are more paths, false otherwise
		 */
		@Override
		public boolean hasNext() {
			return index + 1 < this.size;
		}

		/**
		 * Retrieves the next path in the iteration.
		 *
		 * @return the next SimplePath object
		 * @throws RuntimeException if an IOException occurs while accessing path data
		 */
		@Override
		public SimplePath next() {
			try {
				index++;
				return pathSaver.getPath(pathNames.get(index));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
