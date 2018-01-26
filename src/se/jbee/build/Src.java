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
		boolean inclusive = source.endsWith("+");
		if (inclusive)
			source = source.substring(0, source.length()-1);
		int colon = source.indexOf(':');
		return colon > 0
				? new Src(folder(source.substring(0, colon)), filter(source.substring(colon + 1)), inclusive)
				: new Src(folder(source), inclusive);
	}

	public final Folder dir;
	public final Filter pattern;
	/**
	 * Should the dependencies of the source be included.
	 * What "included" means depends on the goal type.
	 */
	public final boolean includive;

	public Src(Folder dir, boolean includive) {
		this(dir, Filter.UNFILTERED, includive);
	}

	public Src(Folder dir, Filter filter, boolean includive) {
		this.dir = dir;
		this.pattern = filter;
		this.includive = includive;
	}

	@Override
	public String toString() {
		return dir+(pattern.isFiltered()? pattern.toString() : "")+(includive ? "+" : "");
	}

	public boolean isSuperset(Src other) {
		return other.dir.equalTo(dir)
				&& (other.pattern.equalTo(pattern) || !other.pattern.isFiltered() && pattern.isFiltered());
	}

	public boolean isSubset(Src other) {
		return other.dir.equalTo(dir)
				&& (other.pattern.equalTo(pattern) || !pattern.isFiltered() && other.pattern.isFiltered());
	}
}
