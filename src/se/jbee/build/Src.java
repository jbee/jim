package se.jbee.build;

import static se.jbee.build.Filter.filter;
import static se.jbee.build.Folder.folder;

public final class Src {

	public static Src[] split(String sources) {
		String[] sn = sources.split("[ ,]\\s*");
		Src[] res = new Src[sn.length];
		for (int i = 0; i < sn.length; i++)
			res[i] = Src.parse(sn[i]);
		return res;
	}

	public static Src parse(String source) {
		boolean depsInJar = source.endsWith("++");
		if (depsInJar)
			source = source.substring(0, source.length()-2);
		boolean depsOnCP = source.endsWith("+");
		if (depsOnCP)
			source = source.substring(0, source.length()-1);
		int colon = source.indexOf(':');
		return colon > 0
				? new Src(folder(source.substring(0, colon)), filter(source.substring(colon + 1)), depsOnCP, depsInJar)
				: new Src(folder(source), depsOnCP, depsInJar);
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

	public Src(Folder dir, boolean depsOnCP, boolean depsInJar) {
		this(dir, Filter.UNFILTERED, depsOnCP, depsInJar);
	}

	public Src(Folder dir, Filter filter, boolean depsOnCP, boolean depsInJar) {
		this.dir = dir;
		this.pattern = filter;
		this.depsOnCP = depsOnCP;
		this.depsInJar = depsInJar;
	}

	@Override
	public String toString() {
		return dir+(pattern.isFiltered()? ":"+pattern.toString() : "")+(depsOnCP ? "+" : "");
	}

	public boolean isSuperset(Src other) {
		return isSuperset(this, other);
	}

	public boolean isSubset(Src other) {
		return isSuperset(other, this);
	}

	public static boolean isSuperset(Src a, Src b) {
		return b.dir.equalTo(a.dir)
				&& (b.pattern.equalTo(a.pattern) || !b.pattern.isFiltered() && a.pattern.isFiltered());
	}
}
