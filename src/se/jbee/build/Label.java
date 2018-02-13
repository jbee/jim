package se.jbee.build;

public final class Label implements Comparable<Label> {

	public static final Label NONE = new Label("");

	public static Label label(String name) {
		if (name.isEmpty())
			return NONE;
		if (!name.matches("[a-zA-Z0-9][-_A-Za-z0-9]*"))
			throw new WrongFormat("Invalid label", name);
		return new Label(name);
	}

	public final String name;

	private Label(String name) {
		this.name = name.intern();
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
		return this == other || name == other.name; // interned names
	}

	@Override
	public int compareTo(Label other) {
		return name.compareTo(other.name);
	}

	public static String toString(Label...labels) {
		if (labels == null || labels.length == 0)
			return "";
		if (labels.length == 1)
			return labels[0].name;
		StringBuilder b = new StringBuilder(labels.length*8);
		b.append(labels[0].name);
		for (int i = 1; i < labels.length; i++)
			b.append(' ').append(labels[i].name);
		return b.toString();
	}
}
