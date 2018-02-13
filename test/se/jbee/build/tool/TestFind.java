package se.jbee.build.tool;

import static org.junit.Assert.assertEquals;
import static se.jbee.build.Folder.folder;
import static se.jbee.build.Home.home;
import static se.jbee.build.Main.main;
import static se.jbee.build.tool.Find.qualifiedName;

import org.junit.Test;

public class TestFind {

	@Test
	public void qualifiedNameFind() {
		assertEquals("se.jbee.build.tool.Find", qualifiedName(home(), folder("src"), main("Find")));
	}
}
