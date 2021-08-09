package se.jbee.build;

import static se.jbee.build.Package.pkg;

import java.util.Arrays;
import java.util.Iterator;

import se.jbee.build.BuildIssue.IncompleteStructure;

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
public final class Structure implements Iterable<se.jbee.build.Structure.Module> {

	/**
	 * From independent at index 0 to most dependent at last index.
	 */
	private final Module[] levels;

	public Structure(Module[] levels) {
		this.levels = levels;
	}

	@Override
	public Iterator<Module> iterator() {
		return Arrays.asList(levels).iterator();
	}

	public Module of(Package pkg) {
		for (Module m : levels)
			if (m.module.equalTo(pkg))
				return m;
		throw new IncompleteStructure(levels[levels.length-1], pkg);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(levels[0].base).append(":\n");
		if (levels.length > 0) {
			b.append("\t[").append(levels[0].module);
			for (int i = 1; i < levels.length; i++) {
				Module m0 = levels[i-1];
				Module m1 = levels[i];
				if (m1.level == m0.level) {
					if (m0.isAccessible(m1.module)) {
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

	/**
	 * A {@link Module} is a "main level" {@link Package}.
	 *
	 * It is described in term of the {@link Packages} on the same "top level" that
	 * is can ({@link #fanIn}) and can't access but affect ({@link #fanOut}) in the
	 * overall {@link Structure}.
	 */
	public static final class Module {

		public final int level;
		public final Package base;
		public final Package module;
		public final Package canonicalName;
		/**
		 * The set of other packages this module may depend upon (not including this).
		 */
		public final Packages fanIn;
		/**
		 * The set of packages changes in this module can affect (including this).
		 */
		public final Packages fanOut;

		public Module(Package base, Package module, int level, Packages fanIn) {
			this(base, module, level, fanIn, Packages.NONE);
		}

		private Module(Package base, Package module, int level, Packages fanIn, Packages fanOut) {
			this.base = base;
			this.module = module;
			this.canonicalName = pkg(base.path+module.name);
			this.level = level;
			this.fanIn = fanIn;
			this.fanOut = fanOut;
		}

		public Module in(Packages structure) {
			return new Module(base, module, level, fanIn, structure.subtract(fanIn));
		}

		public boolean isAccessible(Package sibling) {
			if (sibling.equalTo(this.module) || fanIn.contains(sibling))
				return true;
			if (fanOut.contains(sibling))
				return false;
			throw new BuildIssue.IncompleteStructure(this, sibling);
		}

		public boolean isAccessible(String packageName) {
			if (packageName.equals(base.name))
				return isAccessible(Package.ROOT);
			if (!packageName.startsWith(base.path) || hasModule(module.name, base.name.length(), packageName))
				return true; // this module or one of its sub-packages
			int dotAt = packageName.indexOf('.', base.name.length());
			if (packageName.lastIndexOf('.') != dotAt)
				return false; // has further sub-packages - only allowed for the module itself
			return fanIn.any(p -> packageName.regionMatches(dotAt+1, p.name, 0, p.name.length())); // is one of the accessible siblings
		}

		private static boolean hasModule(String pkg, int at, String s) {
			int len = s.length();
			return len > at && s.charAt(at) == '.'
					&& s.startsWith(pkg, at+1)
					&& (len == at+1+pkg.length() || s.charAt(at+1+pkg.length()) == '.');
		}

		@Override
		public String toString() {
			return module.toString();
		}
	}
}
