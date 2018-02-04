package se.jbee.build.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public interface Exec {

	public class Output {
		public final int exitCode;
		public final String msg;

		public Output(int exitCode, String output) {
			this.exitCode = exitCode;
			this.msg = output;
		}

		public boolean error() {
			return exitCode != 0;
		}
	}

	/**
	 * A simple util meant for running commands with zero or one line output. In
	 * case of an error the {@link #msg} contains the error description.
	 *
	 * @param command
	 *            The command and arguments to run
	 * @return The exit code and first line of output
	 */
	public static Output command(String... command) {
		try {
			ProcessBuilder b = new ProcessBuilder(command);
			b.directory(new File("."));
			b.redirectErrorStream(false);
			Process p = b.start();
			String res = "";
			try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
				res = br.readLine();
			}
			p.waitFor();
			return new Output(p.exitValue(), res == null ? "" : res);
		} catch (Exception e) {
			return new Output(-1, e.getMessage());
		}
	}
}
