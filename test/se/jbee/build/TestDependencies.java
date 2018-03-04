package se.jbee.build;

import static org.junit.Assert.assertEquals;
import static se.jbee.build.Dependencies.dependsOn;
import static se.jbee.build.Package.pkg;
import static se.jbee.build.Packages.parsePackages;
import static se.jbee.build.Url.url;

import java.util.SortedMap;

import org.junit.Test;

public class TestDependencies {

	@Test
	public void packageTreeSimple() {
		Folder to = Folder.LIB;
		Dependency a = new Dependency(url("http://example.com/que"), Packages.ALL, to);

		SortedMap<Package, Dependencies> deps = dependsOn(a).asPackageTreeFor(pkg("a"));
		assertEquals(1, deps.size());
		assertEquals(dependsOn(a), deps.get(deps.firstKey()));
	}

	@Test
	public void packageTreeTwoLevel() {
		Folder to = Folder.LIB;
		Dependency a = new Dependency(url("http://example.com/foo"), parsePackages("[a]"), to);
		Dependency b = new Dependency(url("http://example.com/bar"), parsePackages("[b]"), to);
		Dependency c = new Dependency(url("http://example.com/baz"), parsePackages("[a.c]"), to);
		Dependency d = new Dependency(url("http://example.com/que"), Packages.ALL, to);

		Dependencies list = dependsOn(a, b, c, d);

		SortedMap<Package, Dependencies> deps = list.asPackageTreeFor(pkg("a"));
		assertEquals(2, deps.size());
		assertEquals("a.c", deps.firstKey().name);
		Dependencies cad = deps.get(deps.firstKey());
		assertEquals(3, cad.count());
		assertEquals(dependsOn(c, a, d), cad);
		Dependencies ad = deps.get(deps.lastKey());
		assertEquals(2, ad.count());
		assertEquals(dependsOn(a, d), ad);

		deps = list.asPackageTreeFor(pkg("b"));
		assertEquals(1, deps.size());
		assertEquals("b", deps.firstKey().name);
		assertEquals(dependsOn(b, d), deps.get(deps.firstKey()));
	}

}
