package se.jbee.build;

public final class MissingGoal extends IllegalArgumentException {

	public MissingGoal(Label goal) {
		super("Referenced goal does not exist: "+goal);
	}

}
