package reader;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a path in a GFA file, which consists of a sequence of segments with orientations.
 * Provides methods to access path attributes, segment names, and their positions within the path.
 */
public class SimplePath {
	// name of the path
	private final String name;
	// save the names of segment with their orientation
	private final String[] segmentNamesWithOrientation;

	/**
	 * Constructs a SimplePath object from a line in a GFA file.
	 *
	 * @param line the GFA file line containing path information
	 */
	public SimplePath(String line) {
		String[] fields = line.split("\t");
		this.name = fields[1];
		this.segmentNamesWithOrientation = fields[2].split(",");
	}

	/**
	 * Constructs a SimplePath object with a given name and segment array.
	 *
	 * @param name                       the name of the path
	 * @param segmentNamesWithOrientation an array of segment names with orientations
	 */
	public SimplePath(String name, String[] segmentNamesWithOrientation) {
		this.name = name;
		this.segmentNamesWithOrientation = segmentNamesWithOrientation;
	}

	/**
	 * Checks if a given segment has reverse orientation.
	 *
	 * @param segment the segment string (e.g., "1+")
	 * @return true if the segment is reverse-oriented (ends with '-'), false otherwise
	 */
	public static boolean checkIfSegmentIsReverse(String segment) {
		return segment.charAt(segment.length() - 1) == '-';
	}
	/**
	 * Checks if the segment in the i-th step is revers.
	 *
	 * @param index the index of the segment on the path
	 * @return true if the segment is reverse-oriented (ends with '-'), false otherwise
	 */
	public  boolean checkIfSegmentAtIsReverse(int index) {
		return checkIfSegmentIsReverse(getSegmentWithOrientationAt(index));
	}


	/**
	 * Extracts the name of a segment without its orientation.
	 *
	 * @param segment the segment string (e.g., "A+")
	 * @return the segment name without orientation (e.g., "A")
	 */
	public static String getSegmentName(String segment) {
		return segment.substring(0, segment.length() - 1);
	}

	/**
	 * Gets the name of the path.
	 *
	 * @return the name of the path
	 */
	public String getPathName() {
		return this.name;
	}

	/**
	 * Gets the number of segments in the path.
	 *
	 * @return the number of segments in the path
	 */
	public int getLength() {
		return this.segmentNamesWithOrientation.length;
	}

	/**
	 * Maps each segment name (with orientation) to a list of positions where it appears in the path.
	 *
	 * @return a map of segment names to their positions
	 */
	public HashMap<String, ArrayList<Integer>> getSegmentToPositions() {
		HashMap<String, ArrayList<Integer>> segmentPositions = new HashMap<>();

		for (int index = 0; index < this.segmentNamesWithOrientation.length; index++) {
			String segment = this.getSegmentWithOrientationAt(index);
			if (!segmentPositions.containsKey(segment)) {
				segmentPositions.put(segment, new ArrayList<>());
			}
			segmentPositions.get(segment).add(index);
		}
		return segmentPositions;
	}

	/**
	 * Retrieves the segment name (with orientation) at a specific position in the path.
	 *
	 * @param index the index in the path
	 * @return the segment name with orientation at the specified index
	 */
	public String getSegmentWithOrientationAt(int index) {
		return this.segmentNamesWithOrientation[index];
	}


}
