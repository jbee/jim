package se.jbee.build;

/**
 * Most often a simple package name, like <code>jbee</code> in the full package
 * name <code>se.jbee.build</code>. In {@link Structure#base} this might be a
 * nested package like <code>se.jbee.build</code>.
 */
public final class Package implements Comparable<Package> {

	/**
	 * This is not Java's default package but the base package common to all
	 * packages with classes within a project, like <code>se.jbee.build</code>.
	 */
	public static final Package ROOT = new Package(".", false);

	/**
	 * This is a special {@link Package} representing any other package there is.
	 * It is also parent to any other package.
	 */
	static final Package ANY = new Package("*", false);

	public static Package pkg(String name) {
		if (".".equals(name)) return ROOT;
		if ("*".equals(name)) return ANY;
		boolean hub = name.endsWith("+");
		if (hub)
			name = name.substring(0, name.length()-1);
		if (!name.matches("[.a-zA-Z0-9_]+"))
			throw new WrongFormat("Invalid package name", name);
		return new Package(name, hub);
	}

	public final String name;
	public final String path;
	/**
	 * This package is the one of a group that may depend upon others in that group.
	 */
	public final boolean hub;
	/**
	 * This package represents a single level within a package hierarchy.
	 */
	public final boolean module;

	private Package(String name, boolean hub) {
		this.name = name.intern();
		this.path = name+".";
		this.hub = hub;
		this.module = name.indexOf('.') < 0 || ".".endsWith(name);
	}

	@Override
	public String toString() {
		return name + (hub ? "+" : "");
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

	public boolean parentOf(Package other) {
		return isAny() || other.path.length() > path.length() && other.path.startsWith(path);
	}

	@Override
	public int compareTo(Package other) {
		return -name.compareTo(other.name);
	}

	public boolean isAny() {
		return this == ANY;
	}

	public boolean isEqualToOrParentOf(Package other) {
		return isAny() || other.path.startsWith(path);
	}
}
