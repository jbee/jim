package se.jbee.build;

public final class Source {

		public final Folder dir;
		public final Pattern filter;

		public Source(Folder dir) {
			this(dir, Pattern.ALL);
		}

		public Source(Folder dir, Pattern filter) {
			this.dir = dir;
			this.filter = filter;
		}

		@Override
		public String toString() {
			return dir+(filter.isFiltered()? filter.toString() : "");
		}
}
