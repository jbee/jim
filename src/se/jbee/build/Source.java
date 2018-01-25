package se.jbee.build;

import static se.jbee.build.Filter.filter;
import static se.jbee.build.Folder.folder;

public final class Source {

	public static Source[] split(String sources) {
		String[] sn = sources.split("[ ,]\\s*");
		Source[] res = new Source[sn.length];
		for (int i = 0; i < sn.length; i++)
			res[i] = Source.parse(sn[i]);
		return res;
	}

	public static Source parse(String source) {
		int colon = source.indexOf(':');
		return colon > 0
				? new Source(folder(source.substring(0, colon)), filter(source.substring(colon + 1)))
				: new Source(folder(source));
	}

	public final Folder dir;
	public final Filter pattern;

	public Source(Folder dir) {
		this(dir, Filter.UNFILTERED);
	}

	public Source(Folder dir, Filter filter) {
		this.dir = dir;
		this.pattern = filter;
	}

	@Override
	public String toString() {
		return dir+(pattern.isFiltered()? pattern.toString() : "");
	}

	public boolean isSuperset(Source other) {
		return other.dir.equalTo(dir)
				&& (other.pattern.equalTo(pattern) || !other.pattern.isFiltered() && pattern.isFiltered());
	}

	public boolean isSubset(Source other) {
		return other.dir.equalTo(dir)
				&& (other.pattern.equalTo(pattern) || !pattern.isFiltered() && other.pattern.isFiltered());
	}
}
