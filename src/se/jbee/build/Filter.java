package se.jbee.build;

import java.util.regex.Pattern;

public final class Filter implements Comparable<Filter> {

	public static final Filter UNFILTERED = new Filter("*");

	/**
	 * <pre>
	 * *.css
	 * Foo.*
	 * *Foo.java
	 * </pre>
	 */
	public static Filter filter(String pattern) {
		if (!pattern.matches("[-_+.*~?a-zA-Z]+"))
			throw new WrongFormat("Illegal filter: "+pattern);
		return new Filter(pattern);
	}

	public final String pattern;
	private final Pattern regex;

	private Filter(String pattern) {
		this.pattern = pattern;
		this.regex = Pattern.compile(pattern.replace(".", "\\.").replace("*", ".*"));
	}

	public boolean isFiltered() {
		return this != UNFILTERED;
	}

	public boolean matches(String s) {
		return regex.matcher(s).matches();
	}

	@Override
	public String toString() {
		return pattern;
	}

	@Override
	public int hashCode() {
		return pattern.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof Filter && equalTo((Filter) obj);
	}

	public boolean equalTo(Filter other) {
		return this == other || pattern.equals(other.pattern);
	}

	@Override
	public int compareTo(Filter other) {
		return pattern.compareTo(other.pattern);
	}

}
