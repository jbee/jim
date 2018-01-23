package se.jbee.build;

/**
 * A simple package name, like <code>jbee</code> in the full package name
 * <code>se.jbee.build</code>.
 *
 * @author jan
 */
public final class Package implements Comparable<Package> {

	public static final Package SELF = new Package(".");

	public static Package pkg(String name) {
		if (name.equals("."))
			return SELF;
		if (!name.matches("[a-z][a-zA-Z0-9_]+"))
			throw new WrongFormat("Invalid package name: "+name);
		return new Package(name);
	}

	public final String name; // for now...

	private Package(String name) {
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
		return obj == this || obj instanceof Package && equalTo((Package) obj);
	}

	public boolean equalTo(Package other) {
		return this == other || name.equals(other.name);
	}

	@Override
	public int compareTo(Package other) {
		return name.compareTo(other.name);
	}
}
