package se.jbee.build.parse;

import java.io.File;

import org.junit.jupiter.api.Test;

import se.jbee.build.Build;
import se.jbee.build.Label;
import se.jbee.build.exec.Builder;

public class TestParser {

	@Test
	public void parseTrackerBuildFile() throws Exception {
		Build build = Parser.parseBuild(new File("./examples/build"));

		Builder.run(build, Label.label("test"));
	}
}
