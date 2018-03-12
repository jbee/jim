package se.jbee.build;

public final class Tool {

	public final Filter source;
	public final Compiler compiler;

	public Tool(Filter from, Compiler compiler) {
		this.source = from;
		this.compiler = compiler;
	}

}
