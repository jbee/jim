package se.jbee.build;

public final class Sequence {

	public final Label name;
	public final Goal[] goals;

	public Sequence(Label name, Goal[] goals) {
		this.name = name;
		this.goals = goals;
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(name).append(" =");
		for (Goal g : goals)
			b.append(' ').append(g.name);
		return b.toString();
	}
}
