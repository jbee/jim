package se.jbee.build;

import static java.util.Arrays.asList;
import static se.jbee.build.Arr.map;
import static se.jbee.build.Package.pkg;

import java.util.Iterator;
import java.util.function.Predicate;

public final class Packages implements Iterable<Package> {

	public static final Packages NONE = new Packages(new Package[0]);

	public static Packages parse(String packages) {
		String[] members = packages.trim().replaceAll("[\\[\\]]+", "").split("[ ,]\\s*");
		return members.length == 0 ? NONE : new Packages(map(members, m -> pkg(m)));
	}

	private final Package[] set;

	public Packages(Package... set) {
		super();
		this.set = set;
	}

	@Override
	public String toString() {
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
		return any(p -> p.equalTo(pkg));
	}

	public boolean includes(Package pkg) {
		return any(p -> p.includes(pkg));
	}

	public boolean any(Predicate<Package> test) {
		return Arr.any(set, test);
	}

	public Packages add(Package pkg) {
		return wrap(Arr.add(set, pkg, Package::equalTo));
	}

	@Override
	public Iterator<Package> iterator() {
		return asList(set).iterator();
	}

	public int count() {
		return set.length;
	}

	public boolean isEmpty() {
		return set.length == 0;
	}

	public Packages union(Packages others) {
		return wrap(Arr.union(set, others.set, Package::equalTo));
	}

	public Packages subtract(Packages others) {
		return wrap(Arr.subtract(set, others.set, Package::equalTo));
	}

	private Packages wrap(Package[] set) {
		return set == this.set ? this : new Packages(set);
	}

}
