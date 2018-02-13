package se.jbee.build.run.junit;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import se.jbee.build.Dest;
import se.jbee.build.Src;
import se.jbee.build.report.Flow;
import se.jbee.build.run.Runner;

public class JUnit4Runner implements Runner {

	@Override
	public void runWith(Flow recorder, Src[] from, Dest to, String... args) {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new RunListener());
		Class<?>[] tests = new Class<?>[0];
		Result result = junit.run(tests);
	}

}
