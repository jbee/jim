package se.jbee.build.loop;

import static org.junit.Assert.assertArrayEquals;
import static se.jbee.build.loop.Command.command;
import static se.jbee.build.loop.Command.parseCommands;
import static se.jbee.build.loop.Option.CONTINUE;
import static se.jbee.build.loop.Option.HELP;
import static se.jbee.build.loop.Option.REBUILD;
import static se.jbee.build.loop.Option.REFETCH;
import static se.jbee.build.loop.Option.REINSTALL;
import static se.jbee.build.loop.Option.VERSION;
import static se.jbee.build.loop.Option.WATCH;

import org.junit.Test;

public class TestCommand {

	private static void assertEqual(Command[] actuals, Command...expecteds) {
		assertArrayEquals(expecteds, actuals);
	}

	@Test
	public void parseBareCommand() {
		assertEqual(parseCommands("compile"), command("compile"));
	}

	@Test
	public void parseCommandSequence() {
		assertEqual(parseCommands("compile", "test"), command("compile"), command("test"));
	}

	@Test
	public void parseCommandSequenceWithGlobalOptions() {
		assertEqual(parseCommands("compile", "test", "--continue"), command("compile", CONTINUE), command("test", CONTINUE));
	}

	@Test
	public void parseCommandSequenceWithWatchOption() {
		assertEqual(parseCommands("--watch", "compile", "test"), command("compile"), command("test", WATCH));
	}

	@Test
	public void parseSingleCommandTailingOptions() {
		assertEqual(parseCommands("compile", "--continue"), command("compile", CONTINUE));
	}

	@Test
	public void parseSingleCommandLeadingOptions() {
		assertEqual(parseCommands("--continue", "compile"), command("compile", CONTINUE));
	}

	@Test
	public void parseSingleCommandLeadingAndTailingOptions() {
		assertEqual(parseCommands("--continue", "compile", "--watch"), command("compile", CONTINUE, WATCH));
	}

	@Test
	public void parseCommandWithHelpOption() {
		assertEqual(parseCommands("compile", "--help"), command("", HELP));
	}

	@Test
	public void parseBareHelpOption() {
		assertEqual(parseCommands("--help"), command("", HELP));
	}

	@Test
	public void parseCommandWithVersionOption() {
		assertEqual(parseCommands("compile", "--version"), command("", VERSION));
	}

	@Test
	public void parseBareVersionOption() {
		assertEqual(parseCommands("--version"), command("", VERSION));
	}

	@Test
	public void parseCommandWithBang() {
		assertEqual(parseCommands("compile!"), command("compile", REBUILD));
	}

	@Test
	public void parseCommandWithBangBang() {
		assertEqual(parseCommands("compile!!"), command("compile", REBUILD, REFETCH));
	}

	@Test
	public void parseCommandWithBangBangBang() {
		assertEqual(parseCommands("compile!!!"), command("compile", REBUILD, REFETCH, REINSTALL));
	}

	@Test
	public void parseCommandSequenceWithBang() {
		assertEqual(parseCommands("compile!", "test"), command("compile", REBUILD), command("test"));
	}
}
