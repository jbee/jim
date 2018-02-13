package se.jbee.build.loop;

public final class Options {

	public static final Options
		NONE      = options(),
		HELP      = options(Option.HELP),
		VERSION   = options(Option.VERSION),
		REBUILD   = options(Option.REBUILD),
		REFETCH   = options(Option.REBUILD, Option.REFETCH),
		REINSTALL = options(Option.REBUILD, Option.REFETCH, Option.REINSTALL);

	public static Options options(Option... set) {
		return new Options(mask(set));
	}

	private final int set;

	private Options(Option... set) {
		this(mask(set));
	}

	private Options(int set) {
		this.set = set;
	}

	private static int mask(Option... set) {
		int mask = 0;
		for (Option o : set)
			mask |= mask(o);
		return mask;
	}

	private static int mask(Option o) {
		return 1 << o.ordinal();
	}

	public Options with(Option o) {
		return new Options(set | mask(o));
	}

	public Options with(Options other) {
		return new Options(set | other.set);
	}

	public Options without(Option o) {
		return has(o) ? new Options(set ^ mask(o)) : this;
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj instanceof Options && equalTo((Options) obj);
	}

	public boolean equalTo(Options other) {
		return set == other.set;
	}

	@Override
	public int hashCode() {
		return set;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (Option o : Option.values())
			if (has(o))
				b.append(' ').append(o.name());
		return b.toString();
	}

	public boolean has(Option o) {
		return (mask(o) & set) != 0;
	}

	public boolean hasAll(Options other) {
		return (set & other.set) == other.set;
	}

	public boolean isEmpty() {
		return set == 0;
	}

}
