package se.jbee.build.loop;

import se.jbee.build.Build;
import se.jbee.build.Dependency;
import se.jbee.build.Goal;
import se.jbee.build.Label;
import se.jbee.build.tool.Fetch;

public class Builder {

	// 1. use args to parse build file
	// 2. use args to extract command sequence
	// 3. apply command sequence

	//TODO as interface for the obj send everywhere to e.g. append to the output

	public static void run(Command... commands) {

	}


	public static void run(Build build, Label goal) {
		Goal g = build.goal(goal);
		for (Dependency d : g.deps)
			Fetch.fetch(build.home, d, true);
	}


}
