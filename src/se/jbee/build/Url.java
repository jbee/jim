package se.jbee.build;

public final class Url implements Comparable<Url> {

	public static final Url ALL = new Url("*");

	public static Url url(String url) {
		if ("*".equals(url))
			return ALL;
		if (!url.matches("[a-z]+:(//)?[-a-zA-z0-9_+%:.#~&?=/^*]+"))
			throw new WrongFormat("Invalid URL", url);
		return new Url(url);
	}

	public final String url;
	public final boolean virtual;
	public final boolean pattern;

	private Url(String url) {
		this.url = url;
		this.virtual = !url.startsWith("http://") && !url.startsWith("https://");
		this.pattern = "*".equals(url);
	}

	@Override
	public String toString() {
		return url;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof Url && equalTo((Url) obj);
	}

	public boolean equalTo(Url other) {
		return this == other || url.equals(other.url);
	}

	@Override
	public int compareTo(Url other) {
		return url.compareTo(other.url);
	}
}
