package se.jbee.build;

import static java.util.Arrays.copyOfRange;
import static se.jbee.build.Label.label;

public final class Runner {

	public static final Runner NONE = run(Label.NONE);

	public final Label tool;
	public final Label goal;
	public final String [] args;
	public final Class<?> impl;
	public final Url[] dependencies;

	public static Runner parse(String runner) {
		String[] nameAndArgs = runner.split("\\s+");
		String[] args = copyOfRange(nameAndArgs, 1, nameAndArgs.length);
		String name = nameAndArgs[0];
		int colon = name.indexOf(':');
		if (colon < 0)
			return run(label(name), args);
		return self(label(name.substring(colon + 1)), args) ;
	}

	public static Runner run(Label tool, String... args) {
		return new Runner(tool, Label.NONE, args);
	}

	public static Runner self(Label goal, String... args) {
		return new Runner(label("self"), goal, args);
	}

	private Runner(Label tool, Label goal, String... args) {
		this(tool, goal, args, null, new Url[0]);
	}

	private Runner(Label tool, Label goal, String[] args, Class<?> impl, Url[] dependencies) {
		this.tool = tool;
		this.goal = goal;
		this.args = args;
		this.impl = impl;
		this.dependencies = dependencies;
	}

	public Runner connect(Class<?> impl, Url...dependencies) {
		return new Runner(tool, goal, args, impl, dependencies);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(tool);
		if (goal != Label.NONE)
			b.append(":").append(goal);
		for (String arg : args)
			b.append(' ').append(arg);
		return b.toString();
	}
}
