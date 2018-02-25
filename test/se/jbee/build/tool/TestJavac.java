package se.jbee.build.tool;

import static java.util.stream.StreamSupport.stream;
import static org.junit.Assert.assertTrue;
import static se.jbee.build.Folder.folder;
import static se.jbee.build.Home.home;

import org.junit.Test;

import se.jbee.build.Filter;
import se.jbee.build.From;

public class TestJavac {

	@Test
	public void javaFiles() {
		long n = stream(Javac.javaFiles(home(), new From(folder("src"), Filter.UNFILTERED)).spliterator(), false).count();
		assertTrue(n > 30);
	}
}
