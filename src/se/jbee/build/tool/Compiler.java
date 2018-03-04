package se.jbee.build.tool;

import se.jbee.build.report.Progress;

public interface Compiler {

	void compile(Compilation unit, Progress report);
}
