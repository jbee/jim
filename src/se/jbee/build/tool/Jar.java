package se.jbee.build.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import se.jbee.build.Main;

public final class Jar {

	public static void jar(File classes, Main clazz, File jar) throws IOException {
		Manifest mf = new Manifest();
		Attributes main = mf.getMainAttributes();
		main.put(Attributes.Name.MANIFEST_VERSION, "1.0");
		main.put(Attributes.Name.MAIN_CLASS, clazz.toString());
		try (JarOutputStream out = new JarOutputStream(new FileOutputStream(jar), mf)) {
			putEntriesRecursively(classes, out);
		}
	}

	/**
	 * Correct the paths to conform with zip spec:
	 * <ul>
	 * <li>Directory names must end with a '/' slash.</li>
     * <li>Paths must use '/' slashes, not '\'</li>
     * <li>Entries must not begin with a '/' slash.</li>
     * </ul>
	 */
	private static String jarPath(File f) {
		String path = f.getPath().replace("\\", "/");
		while (path.startsWith("/"))
			path = path.substring(1);
		return f.isFile() || path.endsWith("/") ? path : path + "/";
	}

	private static void putEntriesRecursively(File path, JarOutputStream out) throws IOException {
		if (path.isDirectory()) {
			if (!path.getPath().isEmpty()) {
				newEntry(path, out);
				out.closeEntry();
			}
			for (File child : path.listFiles())
				putEntriesRecursively(child, out);
		} else {
			newEntry(path, out);
			Files.copy(path.toPath(), out);
			out.closeEntry();
		}
	}

	private static void newEntry(File f, JarOutputStream out) throws IOException {
		JarEntry e = new JarEntry(jarPath(f));
		e.setTime(f.lastModified());
		out.putNextEntry(e);
	}

}
