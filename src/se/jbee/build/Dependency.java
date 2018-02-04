package se.jbee.build;

import static java.lang.Math.max;
import static se.jbee.build.Folder.folder;
import static se.jbee.build.Url.url;

/**
 * A binary dependency on a "3rd partly" artefact given as the {@link Url} it
 * can be received from, the {@link Packages} it is accessible and the
 * {@link Folder} it should be stored at.
 */
public final class Dependency {

	public static Dependency parse(String dep, Folder toDefault) {
		int inAt = dep.indexOf(" in ");
		int toAt = dep.indexOf(" to ", max(0, inAt));
		if (inAt < 0 && toAt < 0)
			return new Dependency(url(dep), Packages.EMPTY, toDefault);
		Url source = url(dep.substring(0, dep.indexOf(' ')));
		Packages ins = inAt < 0
				? Packages.EMPTY
				: Packages.parse(dep.substring(dep.indexOf('[', inAt) + 1, dep.indexOf(']', inAt)));
		Folder to = toAt < 0 ? toDefault : folder(dep.substring(toAt+4).trim());
		return new Dependency(source, ins, to);
	}

	public final Url source;
	public final Packages ins;
	public final Folder to;

	public Dependency(Url source, Packages ins, Folder to) {
		this.source = source;
		this.ins = ins;
		this.to = to;
	}

	@Override
	public String toString() {
		String res = source.url;
		if (ins.count() > 0) {
			res += " in "+ins;
		}
		if (!to.name.isEmpty()) {
			res += " to "+to;
		}
		return res;
	}
}
