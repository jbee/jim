package se.jbee.build;

public final class Goal {

	public final Label name;
	public final Src[] from;
	public final Dest to;
	public final Runner run;
	public final Dependency[] dependencies;

	public Goal(Label name) {
		this(name, new Src[0], null, null, new Dependency[0]);
	}

	public Goal(Label name, Src[] from, Dest to, Runner run, Dependency[] dependencies) {
		this.name = name;
		this.from = from;
		this.to = to;
		this.run = run;
		this.dependencies = dependencies;
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
		if (run != Runner.NONE) {
			b.append(" run ").append(run);
		}
		return b.toString();
	}
}
