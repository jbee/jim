package se.jbee.build;

import static java.lang.Math.max;
import static se.jbee.build.Folder.folder;
import static se.jbee.build.Main.main;

public final class To {

	public static final To TRASH = new To(Type.ERASE, Folder.TRASH, Filename.NO_SPECIFIC, Main.NONE);

	/**
	 * Possible destination expressions:
	 * <pre>
	 * (empty)
	 * ?
	 * some/folder/
	 * some/folder/some.jar
	 * some/folder/some.jar:MainClass
	 * </pre>
	 */
	public static To parseDest(String dest) {
		if (dest.isEmpty() || "?".equals(dest))
			return TRASH;
		if (dest.contains(".jar:")) {
			int fileAt = dest.lastIndexOf('/');
			Folder dir = fileAt < 0 ? Folder.HOME : folder(dest.substring(0, fileAt));
			int mainAt = dest.lastIndexOf(':');
			Filename jar = Filename.file(dest.substring(max(0, fileAt), mainAt));
			return jarTo(dir, jar, main(dest.substring(mainAt+1)));
		}
		if (dest.endsWith(".jar")) {
			int fileAt = dest.lastIndexOf('/');
			Folder dir = fileAt < 0 ? Folder.HOME : folder(dest.substring(0, fileAt));
			Filename jar = Filename.file(dest.substring(max(0, fileAt)));
			return jarTo(dir, jar);
		}
		return yieldTo(folder(dest));
	}

	public static To yieldTo(Folder dir) {
		return new To(Type.YIELD, dir, Filename.NO_SPECIFIC, Main.NONE);
	}

	public static To jarTo(Folder dir, Filename jar) {
		return jarTo(dir, jar, Main.NONE);
	}

	public static To jarTo(Folder dir, Filename jar, Main clazz) {
		return new To(Type.JAR, dir, jar, clazz);
	}

	public static enum Type {
		/**
		 * copy, download or compile
		 */
		YIELD,
		JAR,
		ERASE,
		ZIP
	}


	public final Type type;
	public final Folder dir;
	public final Filename artefact;
	public final Main launcher;

	private To(Type type, Folder dir, Filename artefact, Main launcher) {
		this.type = type;
		this.dir = dir;
		this.artefact = artefact;
		this.launcher = launcher;
	}

	public java.io.File toFile() {
			return new java.io.File(new java.io.File(dir.name), artefact.name);
	}

	public boolean isDefault() { //FIXME folder is changeable via Var - bad for deciding default or not
		return type == Type.YIELD && dir.equalTo(Folder.OUTPUT);
	}

	@Override
	public String toString() {
		String res = dir.name;
		if (!artefact.name.isEmpty())
			res += "/"+artefact.name;
		if (!launcher.cls.isEmpty())
			res += ":"+launcher.cls;
		return res;
	}
}
