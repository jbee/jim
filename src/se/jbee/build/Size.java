package se.jbee.build;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Size {

	public static final Size UNKNOWN = new Size(-1);

	public static Size from(URL url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.getInputStream();
			return new Size(conn.getContentLength());
		} catch (IOException e) {
			return Size.UNKNOWN;
		} finally {
			if (conn != null) conn.disconnect();
		}
	}

	public final int bytes;

	public Size(int bytes) {
		super();
		this.bytes = bytes;
	}

	public boolean isUnknown() {
		return bytes < 0;
	}

	public boolean isEmpty() {
		return bytes == 0;
	}

	private static final String[] UNITS = {"B", "K", "M", "G"};
	@Override
	public String toString() {
		int i = 0;
		int size = bytes;
		while (size > 1024 && i < UNITS.length-1) {
			size /= 1024;
			i++;
		}
		if (i > 0)
			size++; // ceil
		return size+UNITS[i];
	}
}
