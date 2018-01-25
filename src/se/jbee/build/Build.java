package se.jbee.build;

public final class Build {

	public final Structure modules;
	public final Goal[] goals;
	public final Sequence[] sequences;

	public Build(Structure modules, Goal[] goals, Sequence[] sequences) {
		this.modules = modules;
		this.goals = goals;
		this.sequences = sequences;
	}


}
