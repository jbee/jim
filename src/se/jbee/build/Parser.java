package se.jbee.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Parser implements AutoCloseable {

	private BufferedReader in;
	private int lineNr;
	private String lastLine;
	private boolean unread = false;

	@SuppressWarnings("resource")
	public Parser(File build) throws FileNotFoundException {
		this(new BufferedReader(new FileReader(build)));
	}

	public Parser(BufferedReader in) {
		super();
		this.in = in;
	}

	public String readLine() throws IOException {
		if (unread) {
			unread = false;
			return lastLine;
		}
		lineNr++;
		lastLine = in.readLine();
		return lastLine;
	}

	public String lastLine() {
		return lastLine;
	}

	public void unreadLine() {
		unread = true;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	public void fail(String msg) {
		throw new WrongFormat("Line "+lineNr+": "+msg+" '"+lastLine+"'");
	}

}
