package se.jbee.build.run;

import se.jbee.build.To;
import se.jbee.build.From;
import se.jbee.build.report.Progress;

public interface Runner {

	void runWith(Progress recorder, From[] from, To to, String... args);
}
