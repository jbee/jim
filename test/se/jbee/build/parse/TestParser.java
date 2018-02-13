package se.jbee.build.parse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.jbee.build.Package.pkg;

import java.io.File;

import org.junit.Test;

import se.jbee.build.Build;
import se.jbee.build.Structure.Module;

public class TestParser {

	@Test
	public void parseTrackerBuildFile() throws Exception {
		Build build = Parser.parseBuild(new File("./examples/collaborate/build"));
		System.out.println(build);
	}

	@Test
	public void parseJimBuildFile() throws Exception {
		Build build = Parser.parseBuild(new File("./.jim/build"));
		System.out.println(build);

		Module parse = build.modules.of(pkg("parse"));
		assertTrue(parse.isAccessible("foo.bar"));
		assertTrue(parse.isAccessible("se.jbee.builder"));
		assertTrue(parse.isAccessible("se.jbee.build"));
		assertTrue(parse.isAccessible("se.jbee.build.parse"));
		assertTrue(parse.isAccessible("se.jbee.build.parse.internal"));
		assertFalse(parse.isAccessible("se.jbee.build.tool"));

		Module loop = build.modules.of(pkg("loop"));
		assertTrue(loop.isAccessible("se.jbee.build.tool"));
		assertTrue(loop.isAccessible("se.jbee.build.parse"));
		assertTrue(loop.isAccessible("se.jbee.build"));
		assertFalse(loop.isAccessible("se.jbee.build.tool.internal"));
		assertFalse(loop.isAccessible("se.jbee.build.tool.x.y"));
	}
}
