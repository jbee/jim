package se.jbee.build;

public class Dest {

	public static enum Type {
		COMPILE_OR_COPY, JAR, SHARE, CLEAN, DOWNLOAD
	}


	public final Type type;
	public final Folder dir;
	public final File artefact;
	//TODO Url (share)
	public final Launch mainClass;

	public Dest(Type type, Folder dir, File artefact, Launch mainClass) {
		this.type = type;
		this.dir = dir;
		this.artefact = artefact;
		this.mainClass = mainClass;
	}

}
