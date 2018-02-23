package se.jbee.build.run;

import se.jbee.build.To;
import se.jbee.build.From;
import se.jbee.build.report.Flow;

public interface Runner {

	void runWith(Flow recorder, From[] from, To to, String... args);
}
