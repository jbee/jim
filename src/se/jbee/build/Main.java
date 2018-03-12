package se.jbee.build;

/**
 * The main class of a jar.
 */
public final class Main implements Comparable<Main> {

	public static final Main NONE = new Main("");

	public static Main main(String cls) {
		if (!cls.matches("(?:[a-z][a-z_A-Z0-9]+\\.)*[$A-Z][a-zA-Z_$0-9]+"))
			throw new WrongFormat("Invalid main", cls);
		return new Main(cls);
	}

	public final String cls;
	public final boolean qualified;

	private Main(String cls) {
		this.cls = cls.intern();
		this.qualified = cls.indexOf('.') > 0;
	}

	public boolean isNone() {
		return this.cls.isEmpty();
	}

	@Override
	public String toString() {
		return cls;
	}

	@Override
	public int hashCode() {
		return cls.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj instanceof Main && equalTo((Main) obj);
	}

	public boolean equalTo(Main other) {
		return this == other || cls == other.cls; // interned names
	}

	@Override
	public int compareTo(Main other) {
		return cls.compareTo(other.cls);
	}

}
