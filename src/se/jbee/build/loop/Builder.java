package se.jbee.build.loop;

import se.jbee.build.Build;

public class Builder {

	// 1. use args to parse build file
	// 2. use args to extract command sequence
	// 3. apply command sequence

	//TODO as interface for the obj send everywhere to e.g. append to the output

	//TODO build state: a list of URL/Dependencies loaded...
	//src->dest combinations already done

	private final Build build;

	private Builder(Build build) {
		this.build = build;
	}

	public static void run(Build build, Command... commands) {
		Builder b = new Builder(build);
		for (Command cmd : commands)
			b.run(cmd);
	}

	private void run(Command cmd) {

	}
}
