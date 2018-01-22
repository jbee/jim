package se.jbee.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Parser implements AutoCloseable {

	public BufferedReader in;
	public int lineNr;

	@SuppressWarnings("resource")
	public Parser(File build) throws FileNotFoundException {
		this(new BufferedReader(new FileReader(build)));
	}

	public Parser(BufferedReader in) {
		super();
		this.in = in;
	}

	public String readLine() throws IOException {
		lineNr++;
		return in.readLine();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	public void fail(String msg) {
		throw new IllegalArgumentException(msg); //TODO better
	}

}
