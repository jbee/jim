package se.jbee.build.parse;

import se.jbee.build.Folder;

/**
 * A {@link Var}iable is a placeholder in a <code>build</code> file that is
 * substituted dynamically before the file is parsed.
 *
 * Variables of the <code>default:</code> group can be redefined in the
 * <code>build</code> file.
 */
public interface Var {

	/**
	 * Variable name for the default folder used for storing dependencies, initially
	 * {@link Folder#LIB}. Can be redefined to X using
	 * <code>default:libdir = X</code> in the <code>build</code> file.
	 */
	String DEFAULT_LIBDIR = "default:libdir";

	/**
	 * Variable name for the default folder for storing output files such as class
	 * files, initially {@link Folder#OUTPUT}. Can be redefined to X using
	 * <code>default:outdir = X</code> in the <code>build</code> file.
	 */
	String DEFAULT_OUTDIR = "default:outdir";

	/**
	 * Variable name for that returns the {@link System#currentTimeMillis()} at the
	 * start of the current build. It is automatically set and cannot be redefined.
	 */
	String TIME_NOW = "time:now";

	/**
	 * Resolves the variable with the given name.
	 *
	 * For usual implementations this will be a simple grouped name like
	 * <code>time:now</code>. Only in special cases this is a full variable
	 * expression as it is handled by {@link Vars}.
	 *
	 * @param var
	 *            the variable expression to resolve
	 * @param env
	 *            the {@link Var} used to resolve variables this implementation
	 *            might depend upon
	 * @return the resolved value, empty string in case undefined or any other error
	 */
	String resolve(String var, Var env);
}
