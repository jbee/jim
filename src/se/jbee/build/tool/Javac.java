package se.jbee.build.tool;

import static java.util.Collections.emptyList;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import se.jbee.build.Compilation;
import se.jbee.build.Compiler;
import se.jbee.build.Structure.Module;
import se.jbee.build.report.Progress;

public final class Javac implements Compiler {

	private final JavaCompiler javac;

	public Javac() {
		this.javac = ToolProvider.getSystemJavaCompiler();
	}

	@Override
	public void compile(Compilation unit, Progress report) {
		try (StandardJavaFileManager sfm = javac.getStandardFileManager(null, null, null)) {
			List<JavaFileObject> sources = new ArrayList<>();
			for (Entry<File, List<File>> sourceFolder : unit.sources.entrySet()) {
				for (JavaFileObject source : sfm.getJavaFileObjectsFromFiles(sourceFolder.getValue())) {
					sources.add(source);
				}
			}
			CompilationTask task = javac.getTask(null, new ModuleJavaFileManager(sfm, unit.module), null, options(unit), null, sources);
			task.call();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static List<String> options(Compilation unit) {
		ArrayList<String> options = new ArrayList<>();
		options.add("-d");
		options.add(unit.destination.getAbsolutePath());
		// -implicit none //can be used to suppress generating class files not in source file set
		// -source
		// TODO prefer source when ! (bang) is used - also do not include dest dir in cp
		if (!unit.dependencies.isEmpty()) {
			options.add("-cp");
			String cp = "";
			for (File dep : unit.dependencies) {
				if (!cp.isEmpty()) cp += ";";
				cp += dep.getAbsolutePath();
			}
			options.add(cp);
		}
		return options;
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

		@Override
		public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
				throws IOException {
			// TODO track progress here
			return super.getJavaFileForOutput(location, className, kind, sibling);
		}
	}

}
