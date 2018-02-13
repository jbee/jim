package se.jbee.build.loop;

import static org.junit.Assert.assertEquals;
import static se.jbee.build.loop.Command.parseCommands;
import static se.jbee.build.loop.Option.CONTINUE;
import static se.jbee.build.loop.Option.WATCH;
import static se.jbee.build.loop.Options.options;

import org.junit.Test;

import se.jbee.build.Label;

public class TestCommand {

	@Test
	public void parseBareCommand() {
		Command[] commands = parseCommands("compile");

		assertEquals(1, commands.length);
		assertEquals("compile", commands[0].goal.name);
		assertEquals(Options.NONE, commands[0].ops);
	}

	@Test
	public void parseCommandSequence() {
		Command[] commands = parseCommands("compile", "test");

		assertEquals(2, commands.length);
		assertEquals("compile", commands[0].goal.name);
		assertEquals(Options.NONE, commands[0].ops);
		assertEquals("test", commands[1].goal.name);
		assertEquals(Options.NONE, commands[1].ops);
	}

	@Test
	public void parseSingleCommandTailingOptions() {
		Command[] commands = parseCommands("compile", "--continue");

		assertEquals(1, commands.length);
		assertEquals("compile", commands[0].goal.name);
		assertEquals(options(CONTINUE), commands[0].ops);
	}

	@Test
	public void parseSingleCommandLeadingOptions() {
		Command[] commands = parseCommands("--continue", "compile");

		assertEquals(1, commands.length);
		assertEquals("compile", commands[0].goal.name);
		assertEquals(options(CONTINUE), commands[0].ops);
	}

	@Test
	public void parseSingleCommandLeadingAndTailingOptions() {
		Command[] commands = parseCommands("--continue", "compile", "--watch");

		assertEquals(1, commands.length);
		assertEquals("compile", commands[0].goal.name);
		assertEquals(options(CONTINUE, WATCH), commands[0].ops);
	}

	@Test
	public void parseCommandWithHelpOption() {
		Command[] commands = parseCommands("compile", "--help");

		assertEquals(1, commands.length);
		assertEquals(Label.NONE, commands[0].goal);
		assertEquals(Options.HELP, commands[0].ops);
	}

	@Test
	public void parseBareHelpOption() {
		Command[] commands = parseCommands("--help");

		assertEquals(1, commands.length);
		assertEquals(Label.NONE, commands[0].goal);
		assertEquals(Options.HELP, commands[0].ops);
	}

	@Test
	public void parseCommandWithVersionOption() {
		Command[] commands = parseCommands("compile", "--version");

		assertEquals(1, commands.length);
		assertEquals(Label.NONE, commands[0].goal);
		assertEquals(Options.VERSION, commands[0].ops);
	}

	@Test
	public void parseBareVersionOption() {
		Command[] commands = parseCommands("--version");

		assertEquals(1, commands.length);
		assertEquals(Label.NONE, commands[0].goal);
		assertEquals(Options.VERSION, commands[0].ops);
	}

	@Test
	public void parseCommandWithBang() {
		Command[] commands = parseCommands("compile!");

		assertEquals(1, commands.length);
		assertEquals("compile", commands[0].goal.name);
		assertEquals(Options.REBUILD, commands[0].ops);
	}

	@Test
	public void parseCommandWithBangBang() {
		Command[] commands = parseCommands("compile!!");

		assertEquals(1, commands.length);
		assertEquals("compile", commands[0].goal.name);
		assertEquals(Options.REFETCH, commands[0].ops);
	}

	@Test
	public void parseCommandWithBangBangBang() {
		Command[] commands = parseCommands("compile!!!");

		assertEquals(1, commands.length);
		assertEquals("compile", commands[0].goal.name);
		assertEquals(Options.REINSTALL, commands[0].ops);
	}
}
