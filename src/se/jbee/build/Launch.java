package se.jbee.build;

/**
 * The main class of a jar.
 *
 * @author jan
 */
public final class Launch {

	public final String name;
	public final Class<?> type;

	public Launch(String name, Class<?> type) {
		this.name = name;
		this.type = type;
	}

}
