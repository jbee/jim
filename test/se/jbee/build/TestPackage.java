package se.jbee.build;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.jbee.build.Package.ANY;
import static se.jbee.build.Package.pkg;

import org.junit.Test;

public class TestPackage {

	@Test
	public void packageIncludesItself() {
		assertTrue(pkg("foo").includes(pkg("foo")));
	}

	@Test
	public void packageIncludesSubpackages() {
		assertTrue(pkg("foo").includes(pkg("foo.bar")));
	}

	@Test
	public void packageNotIncludesSiblings() {
		assertFalse(pkg("foo").includes(pkg("bar")));
	}

	@Test
	public void anyIncludesAll() {
		assertTrue(ANY.includes(pkg("foo")));
		assertTrue(ANY.includes(pkg("foo.bar")));
		assertTrue(ANY.includes(ANY));
	}
}
