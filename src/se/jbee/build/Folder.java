package se.jbee.build;

/**
 * A relative path segment like <code>src/main</code>.
 *
 * @author jan
 */
public final class Folder implements Comparable<Folder> {

	public static final Folder ALL_SOURCES = new Folder("*");
	public static final Folder TRASH = new Folder("?");
	public static final Folder HOME = new Folder(".");

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
		if (!folder.matches("[a-zA-Z0-9][-.+a-zA-Z0-9_/]+"))
			throw new WrongFormat("Invalid folder", folder);
		return new Folder(folder);
	}

	public final String folder;

	private Folder(String folder) {
		super();
		this.folder = folder;
	}

	@Override
	public String toString() {
		return folder;
	}

	@Override
	public int hashCode() {
		return folder.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof Folder && equalTo((Folder) obj);
	}

	public boolean equalTo(Folder other) {
		return this == other || folder.equals(other.folder);
	}

	@Override
	public int compareTo(Folder other) {
		return folder.compareTo(other.folder);
	}
}
