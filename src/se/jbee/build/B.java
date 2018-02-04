package se.jbee.build;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * File size in bytes.
 */
public final class B {

	public static final B UNKNOWN = new B(-1);

	public static B from(URL url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("HEAD");
			conn.getInputStream();
			return new B(conn.getContentLength());
		} catch (IOException e) {
			return B.UNKNOWN;
		} finally {
			if (conn != null) conn.disconnect();
		}
	}

	public final int bytes;

	public B(int bytes) {
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
