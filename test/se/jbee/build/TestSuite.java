package se.jbee.build;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import se.jbee.build.loop.TestCommand;
import se.jbee.build.loop.TestOptions;
import se.jbee.build.parse.TestParser;
import se.jbee.build.tool.TestFind;

@RunWith(Suite.class)
@SuiteClasses({ TestFind.class, TestParser.class, TestOptions.class, TestCommand.class,
		TestFilter.class, TestPackage.class, TestPackages.class, TestDependencies.class })
public class TestSuite {
	// suite for the project
}
