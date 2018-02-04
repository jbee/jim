package se.jbee.build;

public final class WrongFormat extends BuildIssue {

	public final String expr;
	public final int lineNr;
	public final String line;

	public WrongFormat(String msg, String expr) {
		this(msg, expr, -1, "");

	}

	private WrongFormat(String msg, String expr, int lineNr, String line) {
		super(msg);
		this.expr = expr;
		this.lineNr = lineNr;
		this.line = line;
	}

	public WrongFormat at(int lineNr, String line) {
		return new WrongFormat(getMessage(), expr, lineNr, line);
	}

	@Override
	public String toString() {
		return "Line "+lineNr+": "+getMessage()+" '"+expr+"' in '"+ line + "'";
	}
}
