package se.jbee.build;

import static se.jbee.build.Filter.filter;
import static se.jbee.build.Folder.folder;

public final class From implements Comparable<From> {

	public static From[] parseSources(String sources) {
		String[] sn = sources.split("[ ,]\\s*");
		From[] res = new From[sn.length];
		for (int i = 0; i < sn.length; i++)
			res[i] = From.parseSource(sn[i]);
		return res;
	}

	public static From parseSource(String source) {
		boolean depsInJar = source.endsWith("++");
		if (depsInJar)
			source = source.substring(0, source.length()-2);
		boolean depsOnCP = source.endsWith("+");
		if (depsOnCP)
			source = source.substring(0, source.length()-1);
		int colon = source.indexOf(':');
		return colon > 0
				? new From(folder(source.substring(0, colon)), filter(source.substring(colon + 1)), depsOnCP, depsInJar)
				: new From(folder(source), Filter.UNFILTERED, depsOnCP, depsInJar);
	}

	public final Folder dir;
	public final Filter pattern;
	/**
	 * Should the dependencies of the source be added to the class-path of created
	 * jar.
	 */
	public final boolean depsOnCP;
	/**
	 * Should the dependencies of the source be extracted from their jar and added
	 * into the created jar ("uber-jar").
	 */
	public final boolean depsInJar;

	public From(Folder dir, Filter filter) {
		this(dir, filter, false, false);
	}

	public From(Folder dir, Filter filter, boolean depsOnCP, boolean depsInJar) {
		this.dir = dir;
		this.pattern = filter;
		this.depsOnCP = depsOnCP;
		this.depsInJar = depsInJar;
	}

	@Override
	public String toString() {
		return dir+(pattern.isFiltered()? ":"+pattern.toString() : "")+(depsOnCP ? "+" : "");
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj instanceof From && equalTo((From) obj);
	}

	public boolean equalTo(From other) {
		return this == other || dir.equalTo(other.dir) && pattern.equalTo(other.pattern);
	}

	@Override
	public int hashCode() {
		return dir.hashCode() ^ pattern.hashCode();
	}

	public boolean isSuperset(From other) {
		return isSuperset(this, other);
	}

	public boolean isSubset(From other) {
		return isSuperset(other, this);
	}

	public static boolean isSuperset(From a, From b) {
		return b.dir.equalTo(a.dir)
				&& (b.pattern.equalTo(a.pattern) || !b.pattern.isFiltered() && a.pattern.isFiltered());
	}

	@Override
	public int compareTo(From other) {
		int res = dir.compareTo(other.dir);
		return res == 0 ? pattern.compareTo(pattern) : res;
	}
}
