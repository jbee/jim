package se.jbee.build.tool;

import static java.util.Collections.emptyList;

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

import se.jbee.build.Structure.Module;
import se.jbee.build.report.Progress;

public final class Javac implements Compiler {

	@Override
	public void compile(Compilation unit, Progress report) {
		final JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		try (StandardJavaFileManager sfm = javac.getStandardFileManager(null, null, null)) {
			Iterable<? extends JavaFileObject> sources = sfm.getJavaFileObjectsFromFiles(unit.sources);
			CompilationTask task = javac.getTask(null, new ModuleJavaFileManager(sfm, unit.module), null, args(unit), null, sources);
			task.call();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static List<String> args(Compilation unit) {
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
