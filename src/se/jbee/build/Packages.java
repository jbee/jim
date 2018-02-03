package se.jbee.build;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;
import static se.jbee.build.Package.pkg;

import java.util.Iterator;

public final class Packages implements Iterable<Package> {

	public static final Packages EMPTY = new Packages(new Package[0]);

	public static Packages parse(String packages) {
		String[] members = packages.trim().replaceAll("[\\[\\]]+", "").split("[ ,]\\s*");
		Package[] res = new Package[members.length];
		for (int i = 0; i < members.length; i++)
			res[i] = pkg(members[i]);
		return new Packages(res);
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

	public boolean contains(Package pkg) {
		for (int i = 0; i < set.length; i++)
			if (set[i].equalTo(pkg))
				return true;
		return false;
	}

	public Packages add(Package pkg) {
		if (contains(pkg))
			return this;
		Package[] res = copyOf(set, set.length + 1);
		res[set.length] = pkg;
		return new Packages(res);
	}

	@Override
	public Iterator<Package> iterator() {
		return asList(set).iterator();
	}

	public int count() {
		return set.length;
	}

	public Packages union(Packages others) {
		if (others.set.length == 0)
			return this;
		if (set.length == 0)
			return others;
		Package[] res = copyOf(set, set.length + others.set.length);
		int j = set.length;
		for (int i = 0; i < others.set.length; i++) {
			if (!contains(others.set[i]))
				res[j++] = others.set[i];
		}
		if (j < res.length)
			res = copyOf(res, j);
		return new Packages(res);
	}

	public Packages subtract(Packages others) {
		if (others.set.length == 0 || set.length == 0)
			return this;
		Package[] res = new Package[set.length];
		int i = 0;
		for (Package p : this)
			if (!others.contains(p))
				res[i++] = p;
		return i == set.length ? this : new Packages(copyOf(res, i));
	}
}
