package se.jbee.build;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public final class Dependencies implements Iterable<Dependency> {

	public static final Dependencies NONE = new Dependencies(new Dependency[0]);

	public static Dependencies dependencies(Dependency...dependencies) {
		return dependencies == null || dependencies.length == 0 ? NONE : new Dependencies(dependencies);
	}

	public static Dependencies dependencies(Collection<Dependency> dependencies) {
		return dependencies(dependencies.toArray(new Dependency[0]));
	}

	public static Dependencies dependencies(Url... dependencies) {
		if (dependencies.length == 0)
			return NONE;
		return dependencies(Arr.map(dependencies, url -> new Dependency(url, Packages.NONE, Run.to)));
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
		return wrap(Arr.filter(deps, dep -> dep.appliesTo(pkg)));
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
