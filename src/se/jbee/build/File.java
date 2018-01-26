package se.jbee.build;

/**
 * A simple filename, like <code>x.jar</code>.
 *
 * @author jan
 */
public final class File implements Comparable<File> {

	public static final File NO_SPECIFIC = new File("");

	public static File file(String filename) {
		if (!filename.matches("[-_.+~a-zA-Z0-9]+\\.[a-z]+"))
			throw new WrongFormat("Invalid filename", filename);
		return new File(filename);
	}

	public final String filename;

	private File(String name) {
		super();
		this.filename = name;
	}

	@Override
	public String toString() {
		return filename;
	}

	@Override
	public int hashCode() {
		return filename.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj instanceof File && equalTo((File) obj);
	}

	public boolean equalTo(File other) {
		return this == other || filename.equals(other.filename);
	}

	@Override
	public int compareTo(File other) {
		return filename.compareTo(other.filename);
	}
}
