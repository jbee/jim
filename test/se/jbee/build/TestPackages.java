package se.jbee.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static se.jbee.build.Package.ROOT;
import static se.jbee.build.Package.pkg;
import static se.jbee.build.Packages.ALL;
import static se.jbee.build.Packages.NONE;

import org.junit.Test;

public class TestPackages {

	private static final Packages ONLY_ROOT = new Packages(ROOT);
	private static final Packages ONLY_FOO = new Packages(pkg("foo"));
	private static final Packages FOO_BAR = new Packages(pkg("foo"), pkg("bar"));

	@Test
	public void allUnionAnylIsStillAll() {
		assertSame(ALL, ALL.union(ALL));
		assertSame(ALL, ALL.union(NONE));
		assertSame(ALL, ALL.union(ONLY_ROOT));
		assertSame(ALL, ALL.union(ONLY_FOO));
		assertSame(ALL, ALL.union(FOO_BAR));
	}

	@Test
	public void allSubtractSomeIsStillAll() {
		assertSame(ALL, ALL.subtract(NONE));
		assertSame(ALL, ALL.subtract(ONLY_ROOT));
		assertSame(ALL, ALL.subtract(ONLY_FOO));
	}

	@Test
	public void allSubtractAllIsNone() {
		assertSame(NONE, ALL.subtract(ALL));
	}

	@Test
	public void noneUnionSomeIsSome() {
		assertEquals(ALL, NONE.union(ALL));
		assertEquals(ONLY_ROOT, NONE.union(ONLY_ROOT));
		assertEquals(ONLY_FOO, NONE.union(ONLY_FOO));
		assertEquals(FOO_BAR, NONE.union(FOO_BAR));
	}

	@Test
	public void noneSubtractAnyIsStillNone() {
		assertSame(NONE, NONE.subtract(ALL));
		assertSame(NONE, NONE.subtract(ONLY_ROOT));
		assertSame(NONE, NONE.subtract(ONLY_FOO));
		assertSame(NONE, NONE.subtract(FOO_BAR));
	}

	@Test
	public void allContainsAnyOtherPackage() {
		assertTrue(ALL.contains(ROOT));
		assertTrue(ALL.contains(Package.ANY));
		assertTrue(ALL.contains(pkg("foo")));
		assertTrue(ALL.contains(pkg("foo.bar")));
	}

	@Test
	public void allIncludesAnyOtherPackage() {
		assertTrue(ALL.effectiveIn(ROOT));
		assertTrue(ALL.effectiveIn(Package.ANY));
		assertTrue(ALL.effectiveIn(pkg("foo")));
		assertTrue(ALL.effectiveIn(pkg("foo.bar")));
	}

	@Test
	public void noneContainsOnly$() {
		assertFalse(NONE.contains(Package.ANY));
		assertFalse(NONE.contains(ROOT));
		assertFalse(NONE.contains(pkg("foo")));
	}

	@Test
	public void noneIncludesOnly$() {
		assertFalse(NONE.effectiveIn(Package.ANY));
		assertFalse(NONE.effectiveIn(ROOT));
		assertFalse(NONE.effectiveIn(pkg("foo")));
	}

	@Test
	public void someIncludesItselfAndItsSubpackages() {
		assertTrue(ONLY_FOO.effectiveIn(pkg("foo")));
		assertTrue(ONLY_FOO.effectiveIn(pkg("foo.bar")));
		assertFalse(ONLY_FOO.effectiveIn(pkg("bar")));
		assertFalse(ONLY_FOO.effectiveIn(pkg("bar.foo")));
		assertFalse(ONLY_FOO.effectiveIn(Package.ANY));
		assertTrue(FOO_BAR.effectiveIn(pkg("foo")));
		assertTrue(FOO_BAR.effectiveIn(pkg("foo.bar")));
		assertTrue(FOO_BAR.effectiveIn(pkg("bar")));
		assertTrue(FOO_BAR.effectiveIn(pkg("bar.foo")));
		assertFalse(FOO_BAR.effectiveIn(Package.ANY));
	}

	@Test
	public void someContainsItselfButNotItsSubpackages() {
		assertTrue(ONLY_FOO.contains(pkg("foo")));
		assertFalse(ONLY_FOO.contains(pkg("foo.bar")));
		assertFalse(ONLY_FOO.contains(pkg("bar")));
		assertFalse(ONLY_FOO.contains(pkg("bar.foo")));
		assertFalse(ONLY_FOO.contains(Package.ANY));
		assertTrue(FOO_BAR.contains(pkg("foo")));
		assertFalse(FOO_BAR.contains(pkg("foo.bar")));
		assertTrue(FOO_BAR.contains(pkg("bar")));
		assertFalse(FOO_BAR.contains(pkg("bar.foo")));
		assertFalse(FOO_BAR.contains(Package.ANY));
	}

	@Test
	public void ToStringExcludes$() {
		assertEquals("[]", NONE.toString());
		assertEquals("[*]", ALL.toString());
		assertEquals("[foo]", ONLY_FOO.toString());
		assertEquals("[.]", ONLY_ROOT.toString());
		assertEquals("[foo bar]", FOO_BAR.toString());
	}
}
