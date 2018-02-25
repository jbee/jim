package se.jbee.build;

public final class Goal {

	public final Label name;
	public final From[] srcs;
	public final To dest;
	public final Run tool;
	public final Dependencies deps;

	public Goal(Label name) {
		this(name, new From[0], null, Run.NOTHING, Dependencies.NONE);
	}

	public Goal(Label name, From[] srcs, To dest, Run tool, Dependencies deps) {
		this.name = name;
		this.srcs = srcs;
		this.dest = dest;
		this.tool = tool;
		this.deps = deps;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(name).append(": [");
		for (From src : srcs)
			b.append(src).append(' ');
		b.setLength(b.length()-1);
		b.append("]");
		if (!dest.isDefault())
			b.append(" to ").append(dest);
		if (!tool.isNothing())
			b.append(" run ").append(tool);
		for (Dependency dep : deps)
			b.append("\n\t").append(dep);
		return b.toString();
	}
}
