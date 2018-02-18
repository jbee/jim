package se.jbee.build;

import java.io.File;

/**
 * A relative path segment like <code>src/main</code>.
 *
 * @author jan
 */
public final class Folder implements Comparable<Folder>, Location {

	public static final Folder
		ALL_SOURCES = new Folder("*"),
		TRASH = new Folder("?"),
		HOME = new Folder("."),
		LIB = folder("lib"),
		OUTPUT = folder("target"),
		RUN = folder(".jim/run");

	/**
	 * <pre>
	 * main
	 * src/main
	 * src/main/java
	 * </pre>
	 */
	public static Folder folder(String folder) {
		if ("*".equals(folder))
			return ALL_SOURCES;
		if ("?".equals(folder))
			return TRASH;
		while (folder.length() > 0 && folder.endsWith("/"))
			folder = folder.substring(0, folder.length()-1);
		if (".".equals(folder) || folder.isEmpty())
			return HOME;
		if (!folder.matches("\\.?[a-zA-Z0-9][-.+a-zA-Z0-9_/]+"))
			throw new WrongFormat("Invalid folder", folder);
		return new Folder(folder);
	}

	public final String name;
	public final boolean virtual;

	private Folder(String folder) {
		super();
		this.name = folder;
		this.virtual = name.equals("*") || name.equals("?");
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
		return obj == this || obj instanceof Folder && equalTo((Folder) obj);
	}

	public boolean equalTo(Folder other) {
		return this == other || name.equals(other.name);
	}

	@Override
	public int compareTo(Folder other) {
		return name.compareTo(other.name);
	}

	@Override
	public File toFile(Home home) {
		return this == HOME ? home.dir : new File(home.dir, name);
	}
}
