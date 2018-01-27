package se.jbee.build.parse;

import se.jbee.build.util.Exec;
import se.jbee.build.util.Exec.Output;

public class Var_git_tag implements Var {

	@Override
	public String resolve(String var, Var env) {
		String now = env.resolve("time:now", env);
		Output revHEAD = Exec.command("git", "rev-parse", "--short", "HEAD");
		if (revHEAD.error())
			return "???-"+now;
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
