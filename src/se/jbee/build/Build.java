package se.jbee.build;

import static se.jbee.build.Arr.first;
import static se.jbee.build.Arr.map;

public final class Build {

	public final Timestamp since;
	public final Home in;
	public final Structure modules;
	public final Goal[] goals;
	public final Sequence[] sequences;
	public final CompilerType[] compilers;

	public Build(Timestamp since, Home in, Structure modules, Goal[] goals, Sequence[] sequences, CompilerType[] compilers) {
		this.since = since;
		this.in = in;
		this.modules = modules;
		this.goals = goals;
		this.sequences = sequences;
		this.compilers = compilers;
		linkSequences();
	}

	private void linkSequences() {
		for (Sequence s : sequences)
			for (int i = 0; i < s.goals.length; i++)
				s.goals[i] = goal(s.goals[i].name);
	}

	public Goal goal(Label name) {
		return first(goals, g -> g.name.equalTo(name), new BuildIssue.MissingGoal(name, this));
	}

	public Label[] goals() {
		return goals.length == 0 ? new Label[0] : map(goals, g -> g.name);
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
