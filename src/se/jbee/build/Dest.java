package se.jbee.build;

public class Dest {

	public final Folder dir;
	public final File artefact;
	public final Launch mainClass;

	public Dest(Folder dir, File artefact, Launch mainClass) {
		this.dir = dir;
		this.artefact = artefact;
		this.mainClass = mainClass;
	}

}
