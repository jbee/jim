package se.jbee.build.loop;

import static se.jbee.build.Label.label;
import static se.jbee.build.loop.Options.options;

import java.util.ArrayList;
import java.util.List;

import se.jbee.build.Label;

/**
 * The usage is:
 * <pre>
 * jim [OPTION...] [GOAL...] [VARARGS...]
 * </pre>
 *
 * In practice it is allowed to mix goals and {@link Option}s and variable arguments.
 * These have the form of <code>-X</code> while all options use <code>--name</code>.
 * Goals do not start with a minus at all.
 */
public final class Command {

	public static Command[] parseCommands(String... args) {
		List<Command> seq = new ArrayList<>();
		Options global = Options.NONE;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("--")) {
				global = global.with(Option.valueOf(arg.substring(2).toUpperCase()));
			} else if (arg.startsWith("-")) {
				if (arg.length() == 2) {
					i++; // skip next arg as it is expected to belong to -X
				}
			} else if (arg.endsWith("!!!")) {
				seq.add(new Command(label(arg.substring(0, arg.length() - 3)), Options.REINSTALL));
			} else if (arg.endsWith("!!")) {
				seq.add(new Command(label(arg.substring(0, arg.length() - 2)), Options.REFETCH));
			} else if (arg.endsWith("!")) {
				seq.add(new Command(label(arg.substring(0, arg.length() - 1)), Options.REBUILD));
			} else {
				seq.add(new Command(label(arg), Options.NONE));
			}
		}
		if (global.has(Option.HELP))
			return new Command[] { new Command(Label.NONE, Options.HELP) };
		if (global.has(Option.VERSION))
			return new Command[] { new Command(Label.NONE, Options.VERSION) };
		boolean watch = global.has(Option.WATCH);
		Options add = global.without(Option.WATCH);
		if (!global.isEmpty())
			seq.replaceAll((c) -> c.with(add));
		if (watch)
			seq.set(seq.size()-1, seq.get(seq.size()-1).with(Option.WATCH));
		return seq.toArray(new Command[0]);
	}

	public static Command command(String goal, Option... ops) {
		return new Command(label(goal), options(ops));
	}

	public final Label goal;
	public final Options ops;

	public Command(Label goal, Options ops) {
		this.goal = goal;
		this.ops = ops;
	}

	public Command with(Option o) {
		return new Command(goal, ops.with(o));
	}

	public Command with(Options set) {
		return new Command(goal, ops.with(set));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Command && equalTo((Command) obj);
	}

	public boolean equalTo(Command other) {
		return goal.equalTo(other.goal) && ops.equalTo(other.ops);
	}

	@Override
	public int hashCode() {
		return goal.hashCode() ^ ops.hashCode();
	}

	@Override
	public String toString() {
		return goal.name+ops;
	}
}
