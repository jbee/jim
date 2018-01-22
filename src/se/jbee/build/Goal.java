package se.jbee.build;

public final class Goal {

	public final Name name;
	public final Source[] from;
	public final Dest to;
	public final Runner[] runs;
	public final Dependency[] dependencies;

	public Goal(Name name) {
		this(name, new Source[0], null, new Runner[0], new Dependency[0]);
	}

	public Goal(Name name, Source[] from, Dest to, Runner[] runs, Dependency[] dependencies) {
		this.name = name;
		this.from = from;
		this.to = to;
		this.runs = runs;
		this.dependencies = dependencies;
	}

}
