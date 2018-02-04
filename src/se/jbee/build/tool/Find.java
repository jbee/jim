package se.jbee.build.tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import se.jbee.build.Folder;
import se.jbee.build.Home;
import se.jbee.build.Main;

public class Find {

	public static String qualifiedName(Home dir, Folder subdir, Main clazz) {
		String filename = clazz.clazz+".java";
		Path sourceFolder = subdir.toFile(dir).toPath();
		try {
			Iterator<Path> matches = Files.find(sourceFolder, 16,
					(path, attr) -> path.getFileName().toString().equals(filename)).iterator();
			if (!matches.hasNext())
				return null;
			Path match = matches.next();
			if (matches.hasNext())
				throw new IllegalStateException("Multiple matches");
			String file = sourceFolder.relativize(match).toString();
			return file.substring(0, file.length()-5).replace('/', '.');
		} catch (IOException e) {
			return null;
		}
	}
}


