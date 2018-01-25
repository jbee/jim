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
		if (layers.length > 0) {
			b.append("\t[").append(layers[0].module);
			for (int i = 1; i < layers.length; i++) {
				Module m0 = layers[i-1];
				Module m1 = layers[i];
				if (m1.level == m0.level) {
					if (m0.isAllowed(m1.module)) {
						b.append(' ');
					} else {
						b.append("][");
					}
				} else {
					b.append("]\n").append("\t[");
				}
				b.append(m1.module);
			}
			b.append("]\n");
		}
		return b.toString();
	}

	public static final class Module {

		public final int level;
		public final Package module;
		public final Package [] whitelist;
		public final Package [] context;

		public Module(Package module, int level, Package... whitelist) {
			this(module, level, whitelist, new Package[0]);
		}

		private Module(Package module, int level, Package[] whitelist, Package[] context) {
			super();
			this.module = module;
			this.level = level;
			this.whitelist = whitelist;
			this.context = context;
		}

		public Module context(Package... packages) {
			return new Module(module, level, whitelist, packages);
		}

		public boolean isAllowed(Package module) {
			for (int i = 0; i < whitelist.length; i++)
				if (whitelist[i].equals(module))
					return true;
			for (int i = 0; i < context.length; i++)
				if (context[i].equals(module))
					return false;
			throw new IncompleteStructureDefinition("Unexpected module: "+module);
		}

		@Override
		public String toString() {
			return module.toString();
		}
	}
}
