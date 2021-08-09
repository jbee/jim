package se.jbee.build;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import se.jbee.build.Structure.Module;

/**
 * Description of a compilation group.
 *
 * Groups usually differ by their {@link #dependencies}. If all sources have
 * same dependencies this is represented by a single {@link Compilation} unit.
 */
public final class Compilation {

	//TODO failure mode: continue or abort in case one source could not be compiled

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
	public final Map<Path, List<Path>> sources;
	/**
	 * The destination root folder
	 */
	public final Path destination;

	/**
	 * The {@code jar} files to add to the {@code classpath}.
	 */
	public final List<Path> dependencies;

	public Compilation(Timestamp initiated, Module module, Map<Path, List<Path>> sources, Path destination, List<Path> dependencies) {
		this.initiated = initiated;
		this.module = module;
		this.sources = sources;
		this.destination = destination;
		this.dependencies = dependencies;
	}

	public Path destination(Path srcFolder, Path srcFile) {
		return destination.resolve(srcFolder.relativize(srcFile));
	}
}
