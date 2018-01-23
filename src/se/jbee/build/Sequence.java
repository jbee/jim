package se.jbee.build;

public final class Sequence {

	public final Label name;
	public final Goal[] goals;

	public Sequence(Label name, Goal[] goals) {
		this.name = name;
		this.goals = goals;
	}

}
