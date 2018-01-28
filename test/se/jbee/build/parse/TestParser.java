package se.jbee.build.parse;

import java.io.File;

import org.junit.jupiter.api.Test;

import se.jbee.build.Build;

public class TestParser {

	@Test
	public void parseTrackerBuildFile() throws Exception {
		Build build = Parser.parseBuild(new File("./examples/build"));
		System.out.println(build);
	}

	@Test
	public void parseJimBuildFile() throws Exception {
		Build build = Parser.parseBuild(new File("./.jim/build"));
		System.out.println(build);
	}
}
