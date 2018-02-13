package se.jbee.build.report;

public interface Flow {

	void next(String tool);

	/*
	 * Partial progress can be used to update progress of slow units.
	 * When they are done they still are completed with one of ok, skip or fail.
	 */

	/**
	 * Used for tasks like downloading to update progress.
	 * E.g. called with the KB downloaded so far and file size.
	 */
	void at(String unitID, int n, int ofTotal);

	/**
	 * Attach additional information.
	 */
	void warn(String unitID/* TODO about ... */);

	/*
	 * Unit based progress: A unit of work - like a file is processed with one of the following outcomes.
	 */

	void skip(String unitID);

	void ok(String unitID);

	void fail(String unitID/* TODO more info, e.g. if the build can be continued... */);


	//TODO some generic way to report some total


}
