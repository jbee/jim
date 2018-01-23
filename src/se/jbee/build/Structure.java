package se.jbee.build;

/**
 * The inter-module dependencies.
 *
 * The {@link #base} is the root package of the application or library that all
 * classes of a code base have in common.
 *
 * The next level of packages are the {@link Module}s. They are restricted in
 * the way they allow dependencies.
 *
 * @author jan
 */
public final class Structure {

	public final Package base;
	/**
	 * From independent at index 0 to most dependent at last index.
	 */
	private final Module[] layers;

	public Structure(Package base, Module[] layers) {
		this.base = base;
		this.layers = layers;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(base).append(":\n");
		for (Module l : layers)
			l.toString(b.append("\t[")).append("]\n");
		return b.toString();
	}

	public static final class Module {

		public final Package module;
		public final Package [] whitelist;
		public final Package [] blacklist;

		public Module(Package module, Package... whitelist) {
			this(module, whitelist, new Package[0]);
		}

		private Module(Package module, Package[] whitelist, Package[] blacklist) {
			super();
			this.module = module;
			this.whitelist = whitelist;
			this.blacklist = blacklist;
		}

		public Module blacklist(Package... packages) {
			return new Module(module, whitelist, packages);
		}

		public boolean isAllowed(Package module) {
			for (int i = 0; i < whitelist.length; i++)
				if (whitelist[i].equals(module))
					return true;
			for (int i = 0; i < blacklist.length; i++)
				if (blacklist[i].equals(module))
					return false;
			throw new IncompleteStructureDefinition("Unexpected module: "+module);
		}

		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		StringBuilder toString(StringBuilder b) {
			b.append(module).append('[');
			for (Package w : whitelist)
				b.append(w).append(' ');
			b.setCharAt(b.length()-1, ']');
			return b;
		}
	}
}
