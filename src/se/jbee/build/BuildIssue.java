package se.jbee.build;

import java.io.IOException;
import java.nio.file.Path;

import se.jbee.build.Structure.Module;

public abstract class BuildIssue extends RuntimeException {

	public BuildIssue(String message) {
		super(message);
	}

	public BuildIssue(IOException cause) {
		super(cause);
	}

	public static final class Misconfiguration extends BuildIssue {

		public Misconfiguration(IOException cause) {
			super(cause);
		}

	}

	public static final class IncompleteStructureDefinition extends BuildIssue {

		public IncompleteStructureDefinition(Module module, Package pkg) {
			super("Unexpected module: "+pkg);
		}
	}

	public static final class MissingGoal extends BuildIssue {

		public MissingGoal(Label goal, Build build) {
			super("Referenced goal does not exist: "+goal+"\nAvailable are: "+Label.toString(build.goals()));
		}
	}

	public static final class MissingSource extends BuildIssue {

		public MissingSource(Main clazz) {
			super("Referenced source file not found: "+clazz);
		}
	}

	public static final class AmbiguousSource extends BuildIssue {

		public AmbiguousSource(Main clazz, Path one, Path other) {
			super("Referenced source is not unique: "+clazz);
		}
	}
}
