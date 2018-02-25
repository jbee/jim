package se.jbee.build;

import static java.util.Arrays.asList;
import static se.jbee.build.Arr.filter;
import static se.jbee.build.Arr.map;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public final class Dependencies implements Iterable<Dependency> {

	public static final Dependencies NONE = new Dependencies(new Dependency[0]);

	public static Dependencies dependsOn(Dependency...deps) {
		return deps == null || deps.length == 0 ? NONE : new Dependencies(deps);
	}

	public static Dependencies dependsOn(Collection<Dependency> dependencies) {
		return dependsOn(dependencies.toArray(new Dependency[0]));
	}

	public static Dependencies dependsOn(Url... deps) {
		if (deps.length == 0)
			return NONE;
		return dependsOn(map(deps, url -> new Dependency(url, Packages.ALL, Run.to)));
	}

	private final Dependency[] deps;

	private Dependencies(Dependency[] deps) {
		super();
		this.deps = deps;
	}

	public int count() {
		return deps.length;
	}

	public boolean hasNone() {
		return deps.length == 0;
	}

	public Dependencies in(Package pkg) {
		return wrap(filter(deps, dep -> dep.in.includes(pkg)));
	}

	@Override
	public Iterator<Dependency> iterator() {
		return asList(deps).iterator();
	}

	private Dependencies wrap(Dependency[] deps) {
		return deps == this.deps ? this : new Dependencies(deps);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Dependencies && equalTo((Dependencies) obj);
	}

	public boolean equalTo(Dependencies other) {
		return Arrays.equals(deps, other.deps);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(deps);
	}

	@Override
	public String toString() {
		return Arr.toString(deps, "\n");
	}
}
