package se.jbee.build.exec;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public final class Exec {

	public static class Result {
		public final int exitCode;
		public final String output;

		public Result(int exitCode, String output) {
			this.exitCode = exitCode;
			this.output = output;
		}

		public boolean error() {
			return exitCode != 0;
		}
	}

	public static Result command(String... command) {
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
			return new Result(p.exitValue(), res);
		} catch (Exception e) {
			return new Result(-1, e.getMessage());
		}
	}
}
