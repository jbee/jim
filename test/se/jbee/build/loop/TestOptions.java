package se.jbee.build.loop;

import static org.junit.Assert.assertTrue;
import static se.jbee.build.loop.Option.CONTINUE;
import static se.jbee.build.loop.Option.WATCH;

import org.junit.Test;

public class TestOptions {

	@Test
	public void with() {
		assertTrue(Options.NONE.with(WATCH).has(WATCH));
		assertTrue(Options.HELP.with(WATCH).has(WATCH));
		assertTrue(Options.HELP.with(WATCH).with(CONTINUE).has(WATCH));
	}
}
