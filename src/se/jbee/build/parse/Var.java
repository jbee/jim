package se.jbee.build.parse;

public interface Var {

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
