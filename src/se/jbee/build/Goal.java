package se.jbee.build;

public final class Goal {

	public final Label name;
	public final Src[] from;
	public final Dest to;
	public final Run run;
	public final Dependency[] deps;

	public Goal(Label name) {
		this(name, new Src[0], null, Run.NONE, Dependency.NONE);
	}

	public Goal(Label name, Src[] from, Dest to, Run tool, Dependency[] deps) {
		this.name = name;
		this.from = from;
		this.to = to;
		this.run = tool;
		this.deps = deps;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(name).append(": [");
		for (Src s : from)
			b.append(s).append(' ');
		b.setLength(b.length()-1);
		b.append("]");
		if (!to.isDefault()) {
			b.append(" to ").append(to);
		}
		if (!run.isNone()) {
			b.append(" run ").append(run);
		}
		for (Dependency d : deps)
			b.append("\n\t").append(d);
		return b.toString();
	}
}
