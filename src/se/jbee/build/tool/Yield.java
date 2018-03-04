package se.jbee.build.tool;

import static java.nio.file.Files.walk;
import static se.jbee.build.Filter.JAVA_SOURCE;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map.Entry;

import se.jbee.build.Build;
import se.jbee.build.Dependencies;
import se.jbee.build.From;
import se.jbee.build.Goal;
import se.jbee.build.Home;
import se.jbee.build.Package;
import se.jbee.build.Structure.Module;

public class Yield {

	//maybe walk the src dir and collect all files (filter with src filter) - remaining set has to be handled one way or the other
	// this is all external to the actual compiler

	public static void compile(Build build, Goal goal) {
		for (From src : goal.srcs)
			for (Module m : build.modules) {
				for (Entry<Package, Dependencies> submodule : goal.deps.asPackageTreeFor(m.module).entrySet()) {

				}
			}
	}

	public static Iterable<? extends File> javaFiles(Home home, From src) {
		return () -> {
			try {
				return walk(src.dir.toFile(home).toPath()).filter(JAVA_SOURCE).filter(src.pattern).map(p -> p.toFile()).iterator();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		};
	}
}
