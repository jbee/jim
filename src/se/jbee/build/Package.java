package se.jbee.build;

/**
 * Most often a simple package name, like <code>jbee</code> in the full package
 * name <code>se.jbee.build</code>. In {@link Structure#base} this might be a
 * nested package like <code>se.jbee.build</code>.
 *
 * @author jan
 */
public final class Package implements Comparable<Package> {

	public static final Package SELF = new Package(".", false);

	public static Package pkg(String name) {
		if (name.equals("."))
			return SELF;
		boolean plus = name.endsWith("+");
		if (plus)
				name = name.substring(0, name.length()-1);
		if (!name.matches("[.a-zA-Z0-9_]+"))
			throw new WrongFormat("Invalid package name", name);
		return new Package(name, plus);
	}

	public final String name;
	/**
	 * This package is the one of a group that may depend upon others in that group.
	 */
	public final boolean plus;

	private Package(String name, boolean plus) {
		this.name = name.intern();
		this.plus = plus;
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
		return this == other || name == other.name; // interned names
	}

	@Override
	public int compareTo(Package other) {
		return name.compareTo(other.name);
	}
}
