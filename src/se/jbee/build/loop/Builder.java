package se.jbee.build.loop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import se.jbee.build.Build;
import se.jbee.build.Folder;
import se.jbee.build.From;
import se.jbee.build.Label;
import se.jbee.build.To;
import se.jbee.build.Url;

public class Builder {

	// 1. use args to parse build file
	// 2. use args to extract command sequence
	// 3. apply command sequence

	//TODO as interface for the obj send everywhere to e.g. append to the output

	//TODO build state: a list of URL/Dependencies loaded...
	//src->dest combinations already done

	public final Build build;
	private Set<Label> goalsDone = new HashSet<>();
	private Map<From, Set<To>> yieldsDone = new HashMap<>();
	private Map<Url, Set<Folder>> fetchedDependencies = new HashMap<>();

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
