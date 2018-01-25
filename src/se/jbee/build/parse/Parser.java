package se.jbee.build.parse;

import static java.util.Arrays.asList;
import static se.jbee.build.Label.label;
import static se.jbee.build.Package.pkg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se.jbee.build.Build;
import se.jbee.build.Goal;
import se.jbee.build.Label;
import se.jbee.build.Package;
import se.jbee.build.Sequence;
import se.jbee.build.Source;
import se.jbee.build.Structure;
import se.jbee.build.Structure.Module;
import se.jbee.build.WrongFormat;

public final class Parser implements AutoCloseable {

	public static Build parse(File build) throws FileNotFoundException, IOException, WrongFormat {
		try (Parser in = new Parser(build, null)) {
			return parse(in);
		}
	}

	public static Build parse(Parser in) throws IOException {
		Structure modules = null;
		List<Goal> goals = new ArrayList<>();
		List<Sequence> sequences = new ArrayList<>();
		String line;
		try {
			while ((line = in.readLine()) != null) {
				if (!line.startsWith("--") && !line.trim().isEmpty()) {
					int colon = line.indexOf(':');
					if (colon > 0) {
						int dot = line.indexOf('.');
						if (dot > 0 && dot < colon) {
							modules = in.parseStructure();
						} else {
							goals.add(in.parseGoal());
						}
					} else {
						sequences.add(in.parseSequence());
					}
				}
			}
		} catch (WrongFormat e) {
			throw e.at(in.lineNr, in.lastLine);
		}
		return new Build(modules, goals.toArray(new Goal[0]), sequences.toArray(new Sequence[0]));
	}


	private final BufferedReader in;
	private final Variables vars;

	private int lineNr;
	private String lastLine;
	private boolean unread = false;

	@SuppressWarnings("resource")
	public Parser(File build, Variables vars) throws FileNotFoundException {
		this(new BufferedReader(new FileReader(build)), vars);
	}

	public Parser(BufferedReader in, Variables vars) {
		this.in = in;
		this.vars = vars;
	}

	private String readLine() throws IOException {
		if (unread) {
			unread = false;
			return lastLine;
		}
		lineNr++;
		lastLine = substVars(in.readLine());
		return lastLine;
	}

	private String lastLine() {
		return lastLine;
	}

	private void unreadLine() {
		unread = true;
	}

	private String substVars(String line) {
		int open =line.indexOf('{');
		while (open >= 0) {
			int close = line.indexOf('}', open);
			String var = line.substring(open+1, close);
			String subst = vars.resolve(var);
			line = line.replace("{"+var+"}", subst);
			open = line.indexOf('{', open + subst.length() - var.length());
		}
		return line;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	private void fail(String msg) {
		throw new WrongFormat(msg).at(lineNr, lastLine);
	}

	private Structure parseStructure() throws IOException {
		String line = lastLine();
		Package base = pkg(line.substring(0, line.indexOf(':')).trim());
		line = readLine();
		Set<Package> above = new HashSet<>();
		Set<Package> all = new HashSet<>();
		List<Module> modules = new ArrayList<>();
		int level = 1;
		while (line != null && line.startsWith("\t") && line.indexOf('[') > 0) {
			String[] sets = line.split("\\]\\[");
			for (String set : sets) {
				Package[] packages = Package.split(set);
				above.addAll(asList(packages));
				all.addAll(asList(packages));
				Package[] whitelist = above.toArray(new Package[0]);
				for (Package p : packages) {
					Module m = new Module(p, level, whitelist);
					modules.add(m);
				}
				above.removeAll(asList(packages)); // other set on same level has no access...
			}
			level++;
		}
		unreadLine();
		// complete modules with context
		Module[] layers = new Module[modules.size()];
		Package[] context = all.toArray(new Package[0]);
		for (int i = 0; i < layers.length; i++) {
			layers[i] = modules.get(i).context(context);
		}
		return new Structure(base, layers);
	}

	private Goal parseGoal() {
		String line = lastLine();
		Label name = label(line.substring(0, line.indexOf(':')).trim());
		Source[] sources = Source.split(line.substring(line.indexOf('[')+1, line.indexOf(']')));
		return null;
	}

	private Sequence parseSequence() {
		String line = lastLine();
		if (line.indexOf("=") <= 0)
			fail("Expected sequence but found ");
		String[] segs = line.split("\\s*[= ]\\s*");
		Goal[] seq = new Goal[segs.length-1];
		for (int i = 1; i < segs.length; i++)
			seq[i-1] =  new Goal(label(segs[i])); // these are shallow for now and are replace later
		return new Sequence(label(segs[0]), seq);
	}
}
