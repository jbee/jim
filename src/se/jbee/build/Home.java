package se.jbee.build;

import java.io.File;

/**
 * This class really just exists to make more clear what should be supplied.
 */
public final class Home {

	public static Home home() {
		return new Home(new File("."));
	}

	public final File dir;

	public Home(File dir) {
		this.dir = dir;
		if (!dir.isDirectory())
			throw new IllegalArgumentException("Must be a directory: "+dir);
	}

	@Override
	public String toString() {
		return dir.toString();
	}

}
