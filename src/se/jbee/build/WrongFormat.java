package se.jbee.build;

public final class WrongFormat extends IllegalArgumentException {

	public final int lineNr;
	public final String line;

	public WrongFormat(String s) {
		this(s, -1, "");
	}

	private WrongFormat(String s, int lineNr, String line) {
		super(s);
		this.lineNr = lineNr;
		this.line = line;
	}

	public WrongFormat at(int lineNr, String line) {
		return new WrongFormat(getMessage(), lineNr, line);
	}

	@Override
	public String toString() {
		return "Line "+lineNr+": "+getMessage()+" '"+line+"'";
	}
}
