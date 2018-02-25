package se.jbee.build.tool;

import static java.nio.file.Files.walk;
import static java.util.Collections.emptyList;
import static se.jbee.build.Filter.JAVA_SOURCE;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import se.jbee.build.Build;
import se.jbee.build.Dependencies;
import se.jbee.build.From;
import se.jbee.build.Goal;
import se.jbee.build.Home;
import se.jbee.build.Structure;
import se.jbee.build.Structure.Module;
import se.jbee.build.Timestamp;
import se.jbee.build.To;

public final class Javac {

	public static void compile(Build build, Goal goal) {
		for (From src : goal.srcs)
			compile(build.since, build.in, src, goal.dest, build.modules, goal.deps);
	}

	public static void compile(Timestamp since, Home in, From src, To dest, Structure modules, Dependencies deps) {
		final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		try (StandardJavaFileManager sfm = javac.getStandardFileManager(null, null, null)) {
			//TODO each source can cause multiple parts in case dependencies are limited to a subpackage of the main level package
			// in such cases the sub-package with a special dependency is compiled first - that might cause compilation of some classes in the main level that the sub-package dependends upon
			// than rest of files are compiled - last modified is used to determine if compilation is needed
			for (Module m : modules) {

				Iterable<? extends JavaFileObject> sources = sfm.getJavaFileObjectsFromFiles(javaFiles(in, src));
				CompilationTask task = javac.getTask(null, new ModuleJavaFileManager(sfm, m), null, args(in, src, dest, m, deps), null, sources);
				task.call();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
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

	public static List<String> args(Home home, From src, To dest, Module mod, Dependencies deps) {
		ArrayList<String> cp = new ArrayList<>();
		return cp;
	}

	static final class ModuleJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

		private final Module compiling;

		protected ModuleJavaFileManager(StandardJavaFileManager fm, Module compiling) {
			super(fm);
			this.compiling = compiling;
		}

		@Override
		public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
				throws IOException {
			if (!compiling.isAccessible(packageName))
				return emptyList(); // should cause a compilation error as class is not found
			return super.list(location, packageName, kinds, recurse);
		}
	}
}
