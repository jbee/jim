package se.jbee.build;

public final class Pattern {

	public static final Pattern ALL = new Pattern("*");

	public Pattern(String string) {
		// TODO Auto-generated constructor stub
	}

	public boolean isFiltered() {
		return this != ALL;
	}

}
