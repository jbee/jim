package se.jbee.build;

public final class Dependency {

	public final Url source;
	public final Package[] ins;
	public final Folder to;

	public Dependency(Url source, Package[] ins, Folder to) {
		this.source = source;
		this.ins = ins;
		this.to = to;
	}

}
