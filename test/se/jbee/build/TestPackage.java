package se.jbee.build;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static se.jbee.build.Package.ANY;
import static se.jbee.build.Package.pkg;

import org.junit.Test;

public class TestPackage {

	@Test
	public void packageIncludesItself() {
		assertTrue(pkg("foo").isEqualToOrParentOf(pkg("foo")));
	}

	@Test
	public void packageIncludesSubpackages() {
		assertTrue(pkg("foo").isEqualToOrParentOf(pkg("foo.bar")));
	}

	@Test
	public void packageNotIncludesSiblings() {
		assertFalse(pkg("foo").isEqualToOrParentOf(pkg("bar")));
	}

	@Test
	public void anyIncludesAll() {
		assertTrue(ANY.isEqualToOrParentOf(pkg("foo")));
		assertTrue(ANY.isEqualToOrParentOf(pkg("foo.bar")));
		assertTrue(ANY.isEqualToOrParentOf(ANY));
	}
}
