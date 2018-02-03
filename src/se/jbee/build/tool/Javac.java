package se.jbee.build.tool;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import se.jbee.build.Structure.Module;

public final class Javac {

	public static void main(String[] argsx) {
		JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
		//TODO set output streams (.run(...))
		final StandardJavaFileManager fm = javac.getStandardFileManager(null, null, null);
		JavaFileManager wfm = new JavaFileManager() {

			@Override
			public int isSupportedOption(String option) {
				return fm.isSupportedOption(option);
			}

			@Override
			public ClassLoader getClassLoader(Location location) {
				return fm.getClassLoader(location);
			}

			@Override
			public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds,
					boolean recurse) throws IOException {
				System.out.println("list  "+packageName+" ["+recurse+"]"+kinds);
				Iterable<JavaFileObject> list = fm.list(location, packageName, kinds, recurse);
				if (packageName.equals("a.b"))
					return new ArrayList<>();
				return list;
			}

			@Override
			public String inferBinaryName(Location location, JavaFileObject file) {
				return fm.inferBinaryName(location, file);
			}

			@Override
			public boolean isSameFile(FileObject a, FileObject b) {
				return fm.isSameFile(a, b);
			}

			@Override
			public boolean handleOption(String current, Iterator<String> remaining) {
				return fm.handleOption(current, remaining);
			}

			@Override
			public boolean hasLocation(Location location) {
				return fm.hasLocation(location);
			}

			@Override
			public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind)
					throws IOException {
				System.out.println("j in "+className);
				return fm.getJavaFileForInput(location, className, kind);
			}

			@Override
			public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind,
					FileObject sibling) throws IOException {
				System.out.println("j out  "+className);
				return fm.getJavaFileForOutput(location, className, kind, sibling);
			}

			@Override
			public FileObject getFileForInput(Location location, String packageName, String relativeName)
					throws IOException {
				System.out.println("in   "+packageName+" "+relativeName);
				return fm.getFileForInput(location, packageName, relativeName);
			}

			@Override
			public FileObject getFileForOutput(Location location, String packageName, String relativeName,
					FileObject sibling) throws IOException {
				System.out.println("out   "+packageName+" "+relativeName);
				return fm.getFileForOutput(location, packageName, relativeName, sibling);
			}

			@Override
			public void flush() throws IOException {
				System.out.println("flush");
				fm.flush();
			}

			@Override
			public void close() throws IOException {
				System.out.println("close");
				fm.close();
			}

		};
		final List<JavaFileObject> sources = new ArrayList<>();
		for (JavaFileObject f : fm.getJavaFileObjects(new File("lab/a/A.java"), new File("lab/a/b/B.java")))
			sources.add(f);
		List<String> args = asList("-cp", "lab/");
		CompilationTask task = javac.getTask(null, wfm, null, args, null, sources);
		task.call();
	}

	static final class ModuleJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

		private final Module module;

		protected ModuleJavaFileManager(StandardJavaFileManager fm, Module module) {
			super(fm);
			this.module = module;
		}

		@Override
		public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
				throws IOException {
			if (!module.isAccessible(packageName))
				return emptyList();
			return super.list(location, packageName, kinds, recurse);
		}
	}
}
