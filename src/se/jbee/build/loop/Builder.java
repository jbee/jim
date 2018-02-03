package se.jbee.build.loop;

import se.jbee.build.Build;
import se.jbee.build.Dependency;
import se.jbee.build.Goal;
import se.jbee.build.Label;
import se.jbee.build.tool.Remote;

public class Builder {

	//TODO as interface for the obj send everywhere to e.g. append to the output

	public static void run(Build build, Label goal) {
		Goal g = build.goal(goal);
		for (Dependency d : g.dependencies)
			Remote.fetch(build.home, d, true);
	}


}
