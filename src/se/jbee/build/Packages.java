package se.jbee.build;

import static java.util.Arrays.asList;
import static se.jbee.build.Arr.equalSets;
import static se.jbee.build.Arr.map;
import static se.jbee.build.Package.pkg;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Predicate;

public final class Packages implements Iterable<Package> {

	public static final Packages ALL = new Packages(Package.ANY);
	public static final Packages NONE = new Packages();

	public static Packages parsePackages(String packages) {
		packages = packages.trim();
		if (packages.isEmpty() || "[*]".equals(packages))
			return ALL;
		if ("[]".equals(packages))
			return NONE;
		return new Packages(map(packages.replaceAll("[\\[\\]]+", "").split("[ ,]\\s*"), m -> pkg(m)));
	}

	private final Package[] set;

	public Packages(Package... set) {
		this.set = set;
	}

	@Override
	public String toString() {
		if (isEmpty()) return "[]";
		StringBuilder b = new StringBuilder();
		b.append('[');
		for (Package p :set)
			b.append(p).append(' ');
		b.setLength(b.length()-1);
		b.append(']');
		return b.toString();
	}

	/**
	 * OBS! This will not check for sub-packages.
	 *
	 * @see #effectiveIn(Package)
	 *
	 * @return Is the given {@link Package} a set member or not
	 */
	public boolean contains(Package pkg) {
		return effectiveAnywhere() || any(p -> p.equalTo(pkg));
	}

	public boolean effectiveIn(Package pkg) {
		return effectiveAnywhere() || any(p -> p.isEqualToOrParentOf(pkg));
	}

	public boolean any(Predicate<Package> test) {
		return Arr.any(set, test);
	}

	@Override
	public Iterator<Package> iterator() {
		return asList(set).iterator();
	}

	public boolean effectiveAnywhere() {
		return set.length == 1 && set[0] == Package.ANY;
	}

	public boolean isEmpty() {
		return set.length == 0;
	}

	public Packages union(Packages others) {
		if (isEmpty() || others.effectiveAnywhere()) return others;
		if (others.isEmpty() || effectiveAnywhere()) return this;
		return wrap(Arr.union(set, others.set, Package::equalTo));
	}

	public Packages subtract(Packages others) {
		if (others.effectiveAnywhere() || isEmpty()) return NONE;
		if (others.isEmpty() || effectiveAnywhere()) return this;
		return wrap(Arr.subtract(set, others.set, Package::equalTo));
	}

	private Packages wrap(Package[] set) {
		return set == this.set ? this : new Packages(set);
	}

	public boolean equalTo(Packages other) {
		return equalSets(set, other.set, Package::equalTo);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Packages && equalTo((Packages) obj);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(set);
	}

}
