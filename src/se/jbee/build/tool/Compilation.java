package se.jbee.build.tool;

import java.io.File;
import java.util.List;

import se.jbee.build.Structure.Module;
import se.jbee.build.Timestamp;

/**
 * Description of a compilation group.
 *
 * Groups usually differ by their {@link #dependencies}. If all sources have
 * same dependencies this is represented by a single {@link Compilation} unit.
 */
public final class Compilation {

	/**
	 * The moment the build was initiated. Files created after this are already
	 * handled within this run of the build and should not be recreated.
	 */
	public final Timestamp initiated;
	/**
	 * The module being compiled
	 */
	public final Module module;
	/**
	 * The source files to compile, usually {@code .java} files.
	 * But could be other JVM language source files.
	 */
	public final List<File> sources;
	/**
	 * The destination root folder
	 */
	public final File destination;
	/**
	 * The {@code jar} files to add to the {@code classpath}.
	 */
	public final List<File> dependencies;

	public Compilation(Timestamp initiated, Module module, List<File> sources, File destination, List<File> dependencies) {
		this.initiated = initiated;
		this.module = module;
		this.sources = sources;
		this.destination = destination;
		this.dependencies = dependencies;
	}

}
