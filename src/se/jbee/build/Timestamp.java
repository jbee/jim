package se.jbee.build;

public final class Timestamp implements Comparable<Timestamp> {

	public static Timestamp timestamp(long millisSinceEpoch) {
		return new Timestamp(millisSinceEpoch);
	}

	public final long millisSinceEpoch;

	private Timestamp(long millisSinceEpoch) {
		super();
		this.millisSinceEpoch = millisSinceEpoch;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Timestamp && equalTo((Timestamp) obj);
	}

	public boolean equalTo(Timestamp other) {
		return millisSinceEpoch == other.millisSinceEpoch;
	}

	@Override
	public int hashCode() {
		return (int) millisSinceEpoch;
	}

	@Override
	public String toString() {
		return String.valueOf(millisSinceEpoch);
	}

	@Override
	public int compareTo(Timestamp other) {
		return Long.compare(millisSinceEpoch, other.millisSinceEpoch);
	}
}
