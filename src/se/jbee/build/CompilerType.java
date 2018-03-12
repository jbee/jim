package se.jbee.build;

public final class CompilerType {

	public final Filter fileExtension;
	public final Compiler compiler;

	public CompilerType(Filter fileExtension, Compiler compiler) {
		this.fileExtension = fileExtension;
		this.compiler = compiler;
	}

}
