package se.jbee.build;

public final class MissingGoal extends IllegalArgumentException {

	public MissingGoal(Label goal, Build build) {
		super("Referenced goal does not exist: "+goal+"\nAvailable are: "+Label.toString(build.goals()));
	}

}
