package se.jbee.build;

import static java.util.Arrays.asList;
import static se.jbee.build.Arr.map;
import static se.jbee.build.Package.pkg;

import java.util.Iterator;
import java.util.function.Predicate;

public final class Packages implements Iterable<Package> {

	public static final Packages ALL = new Packages();
	public static final Packages NONE = new Packages(Package.$);

	public static Packages parse(String packages) {
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
		if (isNone()) return "[]";
		if (!isLimited()) return "[*]";
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
	 * @see #includes(Package)
	 *
	 * @return Is the given {@link Package} a set member or not
	 */
	public boolean contains(Package pkg) {
		return !isLimited() || any(p -> p.equalTo(pkg));
	}

	public boolean includes(Package pkg) {
		return !isLimited() || any(p -> p.includes(pkg));
	}

	public boolean any(Predicate<Package> test) {
		return Arr.any(set, test);
	}

	@Override
	public Iterator<Package> iterator() {
		return asList(set).iterator();
	}

	public boolean isLimited() {
		return set.length > 0;
	}

	public boolean isNone() {
		return set.length == 1 && set[0] == Package.$;
	}

	public Packages union(Packages others) {
		if (isNone() || !others.isLimited()) return others;
		if (others.isNone() || !isLimited()) return this;
		return wrap(Arr.union(set, others.set, Package::equalTo));
	}

	public Packages subtract(Packages others) {
		if (!others.isLimited() || isNone()) return NONE;
		if (others.isNone() || !isLimited()) return this;
		return wrap(Arr.subtract(set, others.set, Package::equalTo));
	}

	private Packages wrap(Package[] set) {
		return set == this.set ? this : new Packages(set);
	}

}
