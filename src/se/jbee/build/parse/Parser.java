package se.jbee.build.parse;

import static se.jbee.build.Label.label;
import static se.jbee.build.Package.pkg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import se.jbee.build.Build;
import se.jbee.build.Dependency;
import se.jbee.build.Dest;
import se.jbee.build.Goal;
import se.jbee.build.Label;
import se.jbee.build.Package;
import se.jbee.build.Packages;
import se.jbee.build.Runner;
import se.jbee.build.Sequence;
import se.jbee.build.Src;
import se.jbee.build.Structure;
import se.jbee.build.Structure.Module;
import se.jbee.build.WrongFormat;

public final class Parser implements AutoCloseable {

	public static Build parseBuild(File build) throws FileNotFoundException, IOException, WrongFormat {
		try (Parser in = new Parser(build, new Vars())) {
			return parseBuild(in);
		}
	}

	public static Build parseBuild(Parser in) throws IOException {
		Structure modules = null;
		List<Goal> goals = new ArrayList<>();
		List<Sequence> sequences = new ArrayList<>();
		String line;
		try {
			while ((line = in.readLine()) != null) {
				if (!isComment(line)) {
					int colonAt = line.indexOf(':');
					if (colonAt > 0) {
						int equalAt = line.indexOf(" = ");
						if (equalAt > 0) {
							in.vars.defineVar(line.substring(0, equalAt).trim(), line.substring(equalAt+3).trim());
						} else {
							int dot = line.indexOf('.');
							if (dot > 0 && dot < colonAt) {
								modules = in.parseStructure();
							} else {
								goals.add(in.parseGoal());
							}
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

	private static boolean isComment(String line) {
		return line.startsWith("--") || line.startsWith("#") || line.matches("^\\s*$");
	}


	private final BufferedReader in;
	private final Vars vars;

	private int lineNr;
	private String lastLine = "";
	private boolean unread = false;

	@SuppressWarnings("resource")
	public Parser(File build, Vars vars) throws FileNotFoundException {
		this(new BufferedReader(new FileReader(build)), vars);
	}

	public Parser(BufferedReader in, Vars vars) {
		this.in = in;
		this.vars = vars;
	}

	private String readLine() throws IOException {
		if (unread) {
			unread = false;
			return lastLine;
		}
		lineNr++;
		lastLine = in.readLine(); // update temporary so errors during var subst show the line
		lastLine = substVars(lastLine);
		return lastLine;
	}

	private String lastLine() {
		return lastLine;
	}

	private void unreadLine() {
		unread = true;
	}

	private String substVars(String line) {
		if (line == null)
			return line;
		int open =line.indexOf('{');
		while (open >= 0) {
			int close = line.indexOf('}', open);
			String var = line.substring(open+1, close);
			String subst = vars.resolve(var, vars);
			line = line.replace("{"+var+"}", subst);
			open = line.indexOf('{', open + subst.length() - var.length());
		}
		return line;
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	private void fail(String msg, String expr) {
		throw new WrongFormat(msg, expr).at(lineNr, lastLine);
	}

	private Structure parseStructure() throws IOException {
		String line = lastLine();
		Package base = pkg(line.substring(0, line.indexOf(':')).trim());
		line = readLine();
		Packages above = Packages.EMPTY;
		List<Module> modules = new ArrayList<>();
		int level = 1;
		while (line != null && line.startsWith("\t") && line.indexOf('[') > 0) {
			String[] sets = line.split("\\]\\[");
			Packages aboveLevel = above;
			for (String set : sets) {
				Packages packages = Packages.parse(set);
				Packages whitelist = aboveLevel.union(packages);
				for (Package p : packages)
					modules.add(new Module(p, level, whitelist));
				above = above.union(packages);
			}
			level++;
			line = readLine();
		}
		unreadLine();
		// complete modules with context
		Module[] layers = new Module[modules.size()];
		for (int i = 0; i < layers.length; i++) {
			layers[i] = modules.get(i).context(above);
		}
		return new Structure(base, layers);
	}

	private Goal parseGoal() throws IOException {
		String line = lastLine();
		int colon = line.indexOf(':');
		if (colon < 0)
			fail("Expected a goal but found", line);
		Label name = label(line.substring(0, colon).trim());
		int endOfSource = line.indexOf(']');
		Src[] from = Src.split(line.substring(line.indexOf('[')+1, endOfSource));
		int toAt = line.indexOf(" to ");
		int ranAt = line.indexOf(" run ");
		Dest to = toAt > 0
				? Dest.parse(line.substring(toAt + 4, ranAt < 0 ? line.length() : ranAt).trim())
				: Dest.yieldTo(Dest.TARGET);
		Runner ran = ranAt < 0 ? Runner.NONE : Runner.parse(line.substring(ranAt+5).trim());
		List<Dependency> dependencies = new ArrayList<>();
		line = readLine();
		while (line != null && line.startsWith("\t") && !isComment(line)) {
			Dependency dep = Dependency.parse(line.trim());
			//TODO handle * deps
			dependencies.add(dep);
			line = readLine();
		}
		unreadLine();
		return new Goal(name, from, to, ran, dependencies.toArray(new Dependency[0]));
	}

	private Sequence parseSequence() {
		String line = lastLine();
		if (line.indexOf("=") <= 0)
			fail("Expected sequence but found", line);
		String[] segs = line.split("\\s*[= ]\\s*");
		Goal[] seq = new Goal[segs.length-1];
		for (int i = 1; i < segs.length; i++)
			seq[i-1] =  new Goal(label(segs[i])); // these are shallow for now and are replace later
		return new Sequence(label(segs[0]), seq);
	}
}
