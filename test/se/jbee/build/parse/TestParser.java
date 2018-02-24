package se.jbee.build.parse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static se.jbee.build.Filter.filter;
import static se.jbee.build.Label.label;
import static se.jbee.build.Package.pkg;

import java.io.File;

import org.junit.Test;

import se.jbee.build.Build;
import se.jbee.build.Goal;
import se.jbee.build.Run;
import se.jbee.build.Structure.Module;
import se.jbee.build.To;

public class TestParser {

	@Test
	public void parseTrackerBuildFile() throws Exception {
		Build build = Parser.parseBuild(new File("./examples/collaborate/.jim/build"));
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

		Goal test = build.goal(label("test"));
		assertNotNull(test);
		assertEquals("se.jbee.build.run.junit.JUnit4Runner", test.tool.impl.cls);
		assertEquals(To.Type.YIELD, test.dest.type);
		assertEquals(filter("Test*.java"), test.srcs[0].pattern);

		Goal jar = build.goal(label("jar"));
		assertNotNull(jar);
		assertEquals(To.Type.JAR, jar.dest.type);
		assertEquals(Run.NOTHING, jar.tool);
		assertEquals("src", jar.srcs[0].dir.name);

		Goal compile = build.goal(label("compile"));
		assertNotNull(compile);
		assertEquals(To.Type.YIELD, compile.dest.type);
		assertEquals(Run.NOTHING, compile.tool);
		assertEquals("src", compile.srcs[0].dir.name);
	}
}
