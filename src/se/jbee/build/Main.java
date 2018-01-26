package se.jbee.build;

/**
 * The main class of a jar.
 *
 * @author jan
 */
public final class Main implements Comparable<Main> {

	public static final Main NONE = new Main("");

	public static Main main(String clazz) {
		if (!clazz.matches("(?:[a-z][a-z_A-Z0-9]+\\.)*[$A-Z][a-zA-Z_$0-9]+"))
			throw new WrongFormat("Invalid main", clazz);
		return new Main(clazz);
	}

	public final String clazz;
	public final boolean qulified;

	private Main(String clazz) {
		this.clazz = clazz.intern();
		this.qulified = clazz.indexOf('.') > 0;
	}

	@Override
	public String toString() {
		return clazz;
	}

	@Override
	public int hashCode() {
		return clazz.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj instanceof Main && equalTo((Main) obj);
	}

	public boolean equalTo(Main other) {
		return this == other || clazz == other.clazz; // interned names
	}

	@Override
	public int compareTo(Main other) {
		return clazz.compareTo(other.clazz);
	}

}
