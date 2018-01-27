package se.jbee.build.parse;

import se.jbee.build.exec.Exec;
import se.jbee.build.exec.Exec.Result;

public class Var_git_tag implements Var {

	@Override
	public String resolve(String var, Var env) {
		String now = env.resolve("time:now", env);
		Result revHEAD = Exec.command("git", "rev-parse" ,"HEAD");
		if (revHEAD.error())
			return "???-"+now;
		Result tagHEAD = Exec.command("git", "describe", "--abbrev=0", "--exact-match");
		Result uncommittedChanges = Exec.command("git", "status", "--porcelain");
		boolean dirty = uncommittedChanges.error() || !uncommittedChanges.output.isEmpty();
		String build = dirty ? "."+now : "";
		if (!tagHEAD.error()) {
			return tagHEAD.output+build;
		}
		Result lastTag = Exec.command("git", "describe", "--abbrev=0", "--tags");
		if (!lastTag.error()) {
			return lastTag.output+"@"+revHEAD.output+build;
		}
		return revHEAD.output+build;
	}

}
