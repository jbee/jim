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

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(modules).append("\n\n");
		for (Goal g : goals)
			b.append(g).append("\n\n");
		for (Sequence s : sequences)
			b.append(s).append("\n\n");
		return b.toString();
	}

}
