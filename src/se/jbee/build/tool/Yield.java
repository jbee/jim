package se.jbee.build.tool;

import static java.nio.file.Files.walk;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

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

	public static void yield(Build build, Goal goal) {
		// 1. find all source files
		// 2. pick compiler to feed
		//    or else copy to dest

		for (Module m : build.modules) {
			if (m.module.isRoot()) {

			} else {
				for (Entry<Package, Dependencies> submodule : goal.deps.asPackageTreeFor(m.module).entrySet()) {
					for (From src : goal.srcs) {

					}
				}
			}
		}

	}

	public static Stream<? extends File> files(Home home, From src) {
		return uncheckedIO(() -> walk(src.dir.toFile(home).toPath())).filter(src.pattern).map(p -> p.toFile());
	}

	static <T> T uncheckedIO(Callable<T> f) {
		try {
			return f.call();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
