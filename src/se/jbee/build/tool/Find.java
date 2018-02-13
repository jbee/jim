package se.jbee.build.tool;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import se.jbee.build.BuildIssue;
import se.jbee.build.Folder;
import se.jbee.build.Home;
import se.jbee.build.Main;

public interface Find {

	public static String qualifiedName(Home dir, Folder src, Main cls) {
		String filename = cls.cls+".java";
		Path sourceFolder = src.toFile(dir).toPath();
		try {
			Iterator<Path> matches = Files.find(sourceFolder, 16,
					(path, attr) -> path.getFileName().toString().equals(filename)).iterator();
			if (!matches.hasNext())
				throw new BuildIssue.MissingSource(cls);
			Path match = matches.next();
			if (matches.hasNext())
				throw new BuildIssue.AmbiguousSource(cls, match, matches.next());
			String file = sourceFolder.relativize(match).toString();
			return file.substring(0, file.length()-5).replace('/', '.');
		} catch (IOException e) {
			throw new BuildIssue.Misconfiguration(e);
		}
	}
}


