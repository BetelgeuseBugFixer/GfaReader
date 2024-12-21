package utils;
/**
 * Represents a reference to an entry in a file.
 * This record contains the starting byte position and the length of the segment.
 *
 * <p>Used for referencing portions of a file and managing byte offsets efficiently.
 *  @param startByte the byte in the file where an entry starts
 *  @param length the length of the entry
 */
public record ByteReference(long startByte, int length) {
}
