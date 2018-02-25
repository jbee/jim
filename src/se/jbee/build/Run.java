package se.jbee.build;

import static java.util.Arrays.copyOfRange;
import static se.jbee.build.Arr.concat;
import static se.jbee.build.BuildIssue.failure;
import static se.jbee.build.Dependencies.dependencies;
import static se.jbee.build.Folder.folder;
import static se.jbee.build.Label.label;

public final class Run {
	/**
	 * All jars for the tools are stored in this {@link Folder}.
	 */
	public static final Folder to = folder(".jim/lib/");

	public static final Run NOTHING = run(Label.NONE);

	public final Label tool;
	public final String [] args;
	public final Main impl;
	public final Dependencies deps;

	public static Run parse(String run) {
		String[] nameAndArgs = run.split("\\s+");
		String[] args = copyOfRange(nameAndArgs, 1, nameAndArgs.length);
		String name = nameAndArgs[0];
		return run(label(name), args);
	}

	public static Run run(Label tool, String... args) {
		return new Run(tool, args);
	}

	private Run(Label tool, String... args) {
		this(tool, args, null, Dependencies.NONE);
	}

	private Run(Label tool, String[] args, Main impl, Dependencies deps) {
		this.tool = tool;
		this.args = args;
		this.impl = impl;
		this.deps = deps;
	}

	public boolean isNothing() {
		return tool.name.isEmpty();
	}

	public Run connect(Main impl, Url...dependencies) {
		return new Run(tool, args, impl, dependencies(dependencies));
	}

	public Run use(Run tool) {
		if (tool.impl == null)
			throw failure("Expected a tool with main class but got: "+tool);
		if (!this.tool.equalTo(tool.tool))
			throw new WrongFormat("Expected '"+this.tool+"' in runner file but found", tool.tool.name);
		return new Run(this.tool, concat(tool.args, args), tool.impl, tool.deps);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(tool);
		for (String arg : args)
			b.append(' ').append(arg);
		return b.toString();
	}

}
