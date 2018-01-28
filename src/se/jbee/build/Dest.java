package se.jbee.build;

import static java.lang.Math.max;
import static se.jbee.build.Folder.folder;
import static se.jbee.build.Main.main;

public final class Dest {

	public static final Dest TRASH = new Dest(Type.ERASE, Folder.TRASH, Filename.NO_SPECIFIC, Main.NONE);
	public static final Folder TARGET = folder("target");

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
			int mainAt = dest.lastIndexOf(':');
			Filename file = Filename.file(dest.substring(max(0, fileAt), mainAt));
			return jarTo(folder, file, main(dest.substring(mainAt+1)));
		}
		if (dest.endsWith(".jar")) {

		}
		return yieldTo(folder(dest));
	}

	public static Dest yieldTo(Folder folder) {
		return new Dest(Type.YIELD, folder, Filename.NO_SPECIFIC, Main.NONE);
	}

	public static Dest jarTo(Folder dir, Filename jar) {
		return jarTo(dir, jar, Main.NONE);
	}

	public static Dest jarTo(Folder dir, Filename jar, Main clazz) {
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
	public final Filename artefact;
	public final Main launcher;

	private Dest(Type type, Folder dir, Filename artefact, Main launcher) {
		this.type = type;
		this.dir = dir;
		this.artefact = artefact;
		this.launcher = launcher;
	}

	public java.io.File toFile() {
			return new java.io.File(new java.io.File(dir.name), artefact.name);
	}

	public boolean isDefault() {
		return type == Type.YIELD && dir.equalTo(TARGET);
	}

	@Override
	public String toString() {
		String res = dir.name;
		if (!artefact.name.isEmpty())
			res += "/"+artefact.name;
		if (!launcher.clazz.isEmpty())
			res += ":"+launcher.clazz;
		return res;
	}
}
