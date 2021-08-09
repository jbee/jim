package se.jbee.build;

import static java.util.Arrays.asList;
import static se.jbee.build.Arr.filter;
import static se.jbee.build.Arr.map;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * An immutable list of dependencies.
 */
public final class Dependencies implements Iterable<Dependency> {

	public static final Dependencies NONE = new Dependencies(new Dependency[0]);

	public static Dependencies dependsOn(Dependency...list) {
		return list == null || list.length == 0 ? NONE : new Dependencies(list);
	}

	public static Dependencies dependsOn(Collection<Dependency> list) {
		return dependsOn(list.toArray(new Dependency[0]));
	}

	public static Dependencies dependsOn(Url... deps) {
		if (deps.length == 0)
			return NONE;
		return dependsOn(map(deps, url -> new Dependency(url, Packages.ALL, Run.to)));
	}

	private final Dependency[] list;

	private Dependencies(Dependency[] list) {
		this.list = list;
	}

	public int count() {
		return list.length;
	}

	public boolean hasNone() {
		return list.length == 0;
	}

	public Dependencies effectiveIn(Package pkg) {
		return wrap(filter(list, dep -> dep.in.effectiveIn(pkg)));
	}

	public SortedMap<Package, Dependencies> asPackageTreeFor(Package root) {
		SortedMap<Package, Dependencies> res = new TreeMap<>();
		res.put(root, Dependencies.NONE);
		for (Dependency dep : list)
			for (Package p : dep.in)
				if (root.isEqualToOrParentOf(p))
					res.compute(p, (k,v) -> v == null ? dependsOn(dep) : v.append(dep));
		for (Dependency dep : list)
			if (dep.in.effectiveIn(root))
				res.replaceAll((k, v) -> v.append(dep));
		return res;
	}

	@Override
	public Iterator<Dependency> iterator() {
		return asList(list).iterator();
	}

	public Dependencies append(Dependency dep) {
		return wrap(Arr.add(list, dep, Dependency::equalTo));
	}

	private Dependencies wrap(Dependency[] deps) {
		return deps == this.list ? this : new Dependencies(deps);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Dependencies && equalTo((Dependencies) obj);
	}

	public boolean equalTo(Dependencies other) {
		return Arrays.equals(list, other.list);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(list);
	}

	@Override
	public String toString() {
		return Arr.toString(list, "\n");
	}
}
