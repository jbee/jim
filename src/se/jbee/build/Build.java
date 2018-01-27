package se.jbee.build;

public final class Build {

	public final Structure modules;
	public final Goal[] goals;
	public final Sequence[] sequences;

	public Build(Structure modules, Goal[] goals, Sequence[] sequences) {
		this.modules = modules;
		this.goals = goals;
		this.sequences = sequences;
		for (Sequence s : sequences)
			link(s);
	}

	private void link(Sequence s) {
		for (int i = 0; i < s.goals.length; i++)
			s.goals[i] = goal(s.goals[i].name);
	}

	public Goal goal(Label name) {
		for (Goal g : goals)
			if (g.name.equalTo(name))
				return g;
		throw new MissingGoal(name);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(modules).append("\n");
		for (Goal g : goals)
			b.append(g).append("\n\n");
		for (Sequence s : sequences)
			b.append(s).append("\n\n");
		return b.toString();
	}

}