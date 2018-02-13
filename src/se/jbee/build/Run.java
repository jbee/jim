package se.jbee.build;

import static java.util.Arrays.copyOfRange;
import static se.jbee.build.Folder.folder;
import static se.jbee.build.Label.label;

public final class Run {
	/**
	 * All jars for the tools are stored in this {@link Folder}.
	 */
	public static final Folder to = folder(".jim/lib/");

	public static final Run NONE = runTool(Label.NONE);

	public final boolean goal;
	public final Label tool;
	public final String [] args;
	public final Main impl;
	public final Dependency[] deps;

	public static Run parse(String run) {
		String[] nameAndArgs = run.split("\\s+");
		String[] args = copyOfRange(nameAndArgs, 1, nameAndArgs.length);
		String name = nameAndArgs[0];
		if (!name.startsWith(":"))
			return runTool(label(name), args);
		return runGoal(label(name.substring(1)), args) ;
	}

	public static Run runTool(Label tool, String... args) {
		return new Run(false, tool, args);
	}

	public static Run runGoal(Label goal, String... args) {
		return new Run(true, goal, args);
	}

	private Run(boolean goal, Label tool, String... args) {
		this(goal, tool, args, null, Dependency.NONE);
	}

	private Run(boolean goal, Label tool, String[] args, Main impl, Dependency[] deps) {
		this.tool = tool;
		this.goal = goal;
		this.args = args;
		this.impl = impl;
		this.deps = deps;
	}

	public Run connect(Main impl, Url...dependencies) {
		if (goal)
			throw new BuildIssue.ImplementationFailure("A goal has no main and extra dependencies.");
		Dependency[] deps = dependencies.length == 0 ? Dependency.NONE : new Dependency[dependencies.length];
		for (int i = 0; i < deps.length; i++)
			deps[i] = new Dependency(dependencies[i], Packages.NONE, to);
		return new Run(goal, tool, args, impl, deps);
	}

	public Run use(Run tool) {
		return tool; //TODO
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		if (goal)
			b.append(":").append(goal);
		b.append(tool);
		for (String arg : args)
			b.append(' ').append(arg);
		return b.toString();
	}
}
