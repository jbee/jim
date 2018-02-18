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

	public static ImplementationFailure failure(String msg) {
		return new ImplementationFailure(msg);
	}

	public static final class Misconfiguration extends BuildIssue {

		public Misconfiguration(IOException cause) {
			super(cause);
		}

	}

	/**
	 * Used when a illegal state has been identified that only can mean the
	 * implementation got something wrong.
	 */
	public static final class ImplementationFailure extends BuildIssue {

		private ImplementationFailure(String message) {
			super("OBS! What just happened? "+message);
		}

	}

	public static final class IncompleteStructure extends BuildIssue {

		public IncompleteStructure(Module module, Package pkg) {
			super("Unexpected module: " + pkg
					+ "\nMost likely a added module is missing in the module definition, known are: "
					+ module.fanIn.union(module.fanOut));
		}
	}

	public static final class UnknownParameter extends BuildIssue {

		public UnknownParameter(Iterable<String> unknown) {
			super("Following parameters where given but are unknown in the build: "+unknown.toString());
		}

	}

	public static final class MissingGoal extends BuildIssue {

		public MissingGoal(Label goal, Build build) {
			super("Referenced goal does not exist: "+goal+"\nAvailable are: "+Label.toString(build.goals()));
		}
	}

	public static final class MissingSource extends BuildIssue {

		public MissingSource(Main cls) {
			super("Referenced source file not found: "+cls);
		}
	}

	public static final class AmbiguousSource extends BuildIssue {

		public AmbiguousSource(Main cls, Path one, Path other) {
			super("Referenced source is not unique: "+cls+"\nFound: "+one+"\nand: "+other);
		}
	}

}
