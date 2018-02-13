package se.jbee.build.run;

import se.jbee.build.Dest;
import se.jbee.build.Src;
import se.jbee.build.report.Flow;

public interface Runner {

	void runWith(Flow recorder, Src[] from, Dest to, String... args);
}
