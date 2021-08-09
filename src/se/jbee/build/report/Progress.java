package se.jbee.build.report;

import java.nio.file.Path;

public interface Progress {

	void next(String tool);

	/*
	 * Partial progress can be used to update progress of slow units.
	 * When they are done they still are completed with one of ok, skip or fail.
	 */

	/**
	 * Used for tasks like downloading to update progress.
	 * E.g. called with the KB downloaded so far and file size.
	 */
	void at(Path unit, int n, int ofTotal);

	/**
	 * Attach additional information.
	 */
	void warn(Path unit/* TODO about ... */);

	/*
	 * Unit based progress: A unit of work - like a file is processed with one of the following outcomes.
	 */

	void skip(Path unit);

	void ok(Path unit);

	void fail(Path unit/* TODO more info, e.g. if the build can be continued... */);


	//TODO some generic way to report some total, its more about labeling what can be computed by the process from the previous calls

	//maybe unitID simply is the Path?

}
