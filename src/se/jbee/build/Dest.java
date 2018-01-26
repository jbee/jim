package se.jbee.build;

import static se.jbee.build.Folder.folder;
import static se.jbee.build.Main.main;

public final class Dest {

	public static final Dest TRASH = new Dest(Type.ERASE, Folder.TRASH, File.NO_SPECIFIC, Main.NONE);

	// to
	// to ?
	// to some/folder/
	// to some/folder/some.jar
	// to some/folder/some.jar:MainClass

	public static Dest parse(String dest) {
		if (dest.isEmpty() || "?".equals(dest))
			return TRASH;
		if (dest.contains(".jar:")) {
			int fileAt = dest.lastIndexOf('/');
			Folder folder = fileAt < 0 ? Folder.HOME : folder(dest.substring(0, fileAt));
			int mainAt = dest.lastIndexOf(':', fileAt);
			File file = null;
			return jarTo(folder, file, main(dest.substring(mainAt+1)));
		}
		if (dest.endsWith(".jar")) {

		}
		return yieldTo(folder(dest));
	}

	public static Dest yieldTo(Folder folder) {
		return new Dest(Type.YIELD, folder, File.NO_SPECIFIC, Main.NONE);
	}

	public static Dest jarTo(Folder dir, File jar) {
		return jarTo(dir, jar, Main.NONE);
	}

	public static Dest jarTo(Folder dir, File jar, Main clazz) {
		return new Dest(Type.JAR, dir, jar, clazz);
	}

	public static enum Type {
		/**
		 * copy, download or compile
		 */
		YIELD,
		JAR,
		ERASE
	}


	public final Type type;
	public final Folder dir;
	public final File artefact;
	public final Main launcher;

	private Dest(Type type, Folder dir, File artefact, Main launcher) {
		this.type = type;
		this.dir = dir;
		this.artefact = artefact;
		this.launcher = launcher;
	}

	@Override
	public String toString() {
		String res = dir.folder;
		if (!artefact.filename.isEmpty())
			res += "/"+artefact.filename;
		if (!launcher.clazz.isEmpty())
			res += ":"+launcher.clazz;
		return res;
	}
}
