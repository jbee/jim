package se.jbee.build;

import se.jbee.build.Structure.Module;

public class IncompleteStructureDefinition extends IllegalArgumentException {

	public IncompleteStructureDefinition(Module module, Package pkg) {
		super("Unexpected module: "+pkg);
	}

}
