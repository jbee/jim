package se.jbee.build;

import static org.junit.Assert.assertTrue;
import static se.jbee.build.Filter.JAVA_SOURCE;

import java.io.File;
import java.nio.file.Path;

import org.junit.Test;

public class TestFilter {

	@Test
	public void javaSource() {
		Path file1 = new File("Foo.java").toPath();
		Path file2 = new File("com/example/Foo.java").toPath();
		assertTrue(JAVA_SOURCE.matches("Foo.java"));
		assertTrue(JAVA_SOURCE.matches(file1));
		assertTrue(JAVA_SOURCE.matches(file2));
		assertTrue(JAVA_SOURCE.test(file1));
		assertTrue(JAVA_SOURCE.test(file2));
	}
}
