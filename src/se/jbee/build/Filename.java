package se.jbee.build;

/**
 * A simple filename, like <code>x.jar</code>.
 *
 * @author jan
 */
public final class Filename implements Comparable<Filename> {

	public static final Filename NO_SPECIFIC = new Filename("");

	public static Filename file(String filename) {
		if (!filename.matches("[-_.+~a-zA-Z0-9]+\\.[a-z]+"))
			throw new WrongFormat("Invalid filename", filename);
		return new Filename(filename);
	}

	public final String name;

	private Filename(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj instanceof Filename && equalTo((Filename) obj);
	}

	public boolean equalTo(Filename other) {
		return this == other || name.equals(other.name);
	}

	@Override
	public int compareTo(Filename other) {
		return name.compareTo(other.name);
	}
}
