package se.jbee.build.tool;

import static java.nio.file.Files.walk;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import se.jbee.build.Build;
import se.jbee.build.Compiler;
import se.jbee.build.Dependencies;
import se.jbee.build.From;
import se.jbee.build.Goal;
import se.jbee.build.Home;
import se.jbee.build.Package;
import se.jbee.build.Structure.Module;
import se.jbee.build.Tool;

public class Yield {

	public static final Compiler COPY = (unit, progress) -> {
		progress.next("copy");
		for (Entry<Path, List<Path>> srcGroup : unit.sources.entrySet()) {
			Path srcFolder = srcGroup.getKey();
			for (Path src : srcGroup.getValue()) {
				try {
					Files.copy(src, unit.destination(srcFolder, src), StandardCopyOption.REPLACE_EXISTING);
					progress.ok(src);
				} catch (IOException e) {
					progress.fail(src);
					return false;
				}
			}
		}
		return true;
	};

	//maybe walk the src dir and collect all files (filter with src filter) - remaining set has to be handled one way or the other
	// this is all external to the actual compiler

	public static void yield(Build build, Goal goal) {
		// 1. find all source files
		// 2. pick compiler to feed (last is copy)
		//
		Tool[] tools = build.tools;
		List<Path>[] files = new List[tools.length];
		for (int i = 0; i < files.length; i++)
			files[i] = new ArrayList<>();
		for (From src : goal.srcs) {
			files(build.in, src).forEach(path -> {
				int j = 0;
				while (j < tools.length && !tools[j].source.matches(path)) j++;
				if (j < files.length)
					files[j].add(path);
			});
		}

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

	public static Stream<? extends Path> files(Home home, From src) {
		return uncheckedIO(() -> walk(src.dir.toFile(home).toPath())).filter(src.pattern);
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
