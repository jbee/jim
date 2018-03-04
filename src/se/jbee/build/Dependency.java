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

	public static Dependency parseDependency(String expr, Folder toDefault) {
		int inAt = expr.indexOf(" in ");
		int toAt = expr.indexOf(" to ", max(0, inAt));
		if (inAt < 0 && toAt < 0)
			return new Dependency(url(expr), Packages.ALL, toDefault);
		Url source = url(expr.substring(0, expr.indexOf(' ')));
		Packages in = inAt < 0
				? Packages.ALL
				: Packages.parsePackages(expr.substring(expr.indexOf('[', inAt) + 1, expr.indexOf(']', inAt)));
		Folder to = toAt < 0 ? toDefault : folder(expr.substring(toAt+4).trim());
		return new Dependency(source, in, to);
	}

	public final Url resource;
	public final Packages in;
	public final Folder to;

	public Dependency(Url resource, Packages in, Folder to) {
		this.resource = resource;
		this.in = in;
		this.to = to;
	}

	@Override
	public String toString() {
		String res = resource.url;
		if (!in.effectiveAnywhere())
			res += " in "+in;
		if (!to.name.isEmpty())
			res += " to "+to;
		return res;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Dependency && equalTo((Dependency) obj);
	}

	@Override
	public int hashCode() {
		return resource.hashCode(); // good enough
	}

	public boolean equalTo(Dependency other) {
		return resource.equalTo(other.resource) && in.equalTo(other.in) && to.equalTo(other.to);
	}
}
