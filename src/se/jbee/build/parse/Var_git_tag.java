package se.jbee.build.parse;

import se.jbee.build.tool.Exec;
import se.jbee.build.tool.Exec.Output;

/**
 * Uses <code>git<code> git resolve a version number from git tags.
 *
 * This is most useful to name jar-files.
 *
 * The idea is that the artifact created depends on the code it is compiled
 * from. If we are on a tagged commit (and have no uncommitted changes) the
 * artifact build should be the same and have the name of the tag. If we are not
 * on a tagged commit we use the most recent tag and a addition of the current
 * rev hash. If no such tag exist only the HEAD rev hash is used.
 *
 * In all cases the current timestamp is added to the result if there are
 * uncommitted changes because that might affect the result of the created
 * artifact.
 *
 * Effectively this allows easy release by just going to the commit that should
 * be released and run the jar-goal.
 *
 * @author jan
 */
public final class Var_git_tag implements Var {

	@Override
	public String resolve(String var, Var env) {
		String now = env.resolve("time:now", env);
		Output revHEAD = Exec.command("git", "rev-parse", "--short", "HEAD");
		if (revHEAD.error())
			return null; // no git available - try next in chain
		Output tagHEAD = Exec.command("git", "describe", "--abbrev=0", "--exact-match");
		Output uncommittedChanges = Exec.command("git", "status", "--porcelain");
		boolean dirty = uncommittedChanges.error() || !uncommittedChanges.msg.isEmpty();
		String build = dirty ? "."+now : "";
		if (!tagHEAD.error()) {
			return tagHEAD.msg+build;
		}
		Output lastTag = Exec.command("git", "describe", "--abbrev=0", "--tags");
		if (!lastTag.error()) {
			return lastTag.msg+"@"+revHEAD.msg+build;
		}
		return revHEAD.msg+build;
	}

}
