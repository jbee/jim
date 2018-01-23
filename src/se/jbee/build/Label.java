package se.jbee.build;

public final class Label implements Comparable<Label> {

	public static Label label(String name) {
		if (!name.matches("[a-zA-Z0-9][-_A-Za-z0-9]*"))
			throw new WrongFormat("Invalid label name: "+name);
		return new Label(name);
	}

	public final String name;

	private Label(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj || obj instanceof Label && equalTo((Label) obj);
	}

	public boolean equalTo(Label other) {
		return this == other || name.equals(other.name);
	}

	@Override
	public int compareTo(Label other) {
		return name.compareTo(other.name);
	}
}
