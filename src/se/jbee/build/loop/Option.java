package se.jbee.build.loop;

public enum Option {

	/*
	 * Special tasks (non build executions)
	 */

	/**
	 * <code>--help</code> prints the help screen and exits.
	 */
	HELP,

	/**
	 * <code>--version</code> prints the version screen and exits.
	 */
	VERSION,

	/**
	 * <code>--init</code> creates checksum files for dependencies without them and exits.
	 */
	INIT,

	/*
	 * Global options:
	 */

	/**
	 * <code>--watch</code> adds file watch and recompiles/copies on changes.
	 * Applies only to last command if multiple commands are used in a sequence.
	 */
	WATCH,

	/**
	 * <code>--continue</code> a build if failures occur
	 */
	CONTINUE,

	/**
	 * <code>--quiet</code> out is reduced to a bare minimum, it might even be nothing
	 */
	QUIET,

	/*
	 * Per command options:
	 */

	/**
	 * <code>!</code> after the goal (like <code>compile!</code>) causes the builder
	 * to redo steps even if target already exists. This is similar to doing a
	 * "clean" before.
	 */
	REBUILD,

	/**
	 * <code>!!</code> after the goal (like <code>compile!!</code>) causes the
	 * builder to fetch all dependencies again, even if the already exist.
	 *
	 * Implies {@link #REBUILD}.
	 */
	REFETCH,

	/**
	 * <code>!!!</code> after the goal (like <code>compile!!!</code>) causes the
	 * builder to also fetch all tool dependencies gain, even if the already exist.
	 *
	 * Implies {@link #REBUILD} and {@link #REFETCH}.
	 */
	REINSTALL
}
