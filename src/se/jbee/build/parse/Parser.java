package se.jbee.build.parse;

import static java.util.Arrays.copyOfRange;
import static se.jbee.build.Folder.folder;
import static se.jbee.build.Label.label;
import static se.jbee.build.Package.pkg;
import static se.jbee.build.Run.runTool;

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
import se.jbee.build.Folder;
import se.jbee.build.Goal;
import se.jbee.build.Home;
import se.jbee.build.Label;
import se.jbee.build.Main;
import se.jbee.build.Package;
import se.jbee.build.Packages;
import se.jbee.build.Run;
import se.jbee.build.Sequence;
import se.jbee.build.Src;
import se.jbee.build.Structure;
import se.jbee.build.Structure.Module;
import se.jbee.build.Url;
import se.jbee.build.WrongFormat;

public final class Parser implements AutoCloseable {

	public static Run parseRunner(File runner) throws FileNotFoundException, IOException, WrongFormat {
		try (Parser in = new Parser(runner, new Vars())) {
			return parseRunner(in);
		}
	}

	public static Run parseRunner(Parser in) throws IOException {
		try {
			String line = in.readLine();
			int colonAt = line.indexOf(':');
			Label tool = Label.label(line.substring(0, colonAt).trim());
			String[] mainAndArgs = line.substring(colonAt+1).trim().split("\\s+");
			Main impl = Main.main(mainAndArgs[0]);
			List<Url> deps = new ArrayList<>();
			line = in.readLine();
			while (line != null && line.startsWith("\t")) {
				deps.add(Url.url(line.trim()));
			}
			return runTool(tool, copyOfRange(mainAndArgs, 1, mainAndArgs.length)).connect(impl, deps.toArray(new Url[0]));
		} catch (WrongFormat e) {
			throw e.at(in.lineNr, in.lastLine);
		}
	}

	public static Build parseBuild(File build, String... args) throws FileNotFoundException, IOException, WrongFormat {
		Home home = new Home(build.getParentFile().getParentFile());
		try (Parser in = new Parser(build, new Vars(args))) {
			return parseBuild(home, in);
		}
	}

	public static Build parseBuild(Home home, Parser in) throws IOException {
		Structure modules = null;
		List<Goal> goals = new ArrayList<>();
		List<Sequence> sequences = new ArrayList<>();
		String line;
		try {
			while ((line = in.readLine()) != null) {
				int colonAt = line.indexOf(':');
				if (colonAt > 0) {
					int equalAt = line.indexOf(" = ");
					if (equalAt > 0) {
						in.vars.define(line.substring(0, equalAt).trim(), line.substring(equalAt+3).trim());
					} else {
						in.unreadLine();
						int dotAt = line.indexOf('.');
						if (dotAt > 0 && dotAt < colonAt) {
							modules = in.parseStructure();
						} else {
							goals.add(in.parseGoal());
						}
					}
				} else {
					in.unreadLine();
					sequences.add(in.parseSequence());
				}
			}
		} catch (WrongFormat e) {
			throw e.at(in.lineNr, in.lastLine);
		}
		return new Build(home, modules, goals.toArray(new Goal[0]), sequences.toArray(new Sequence[0]));
	}

	private static boolean isComment(String line) {
		return line != null && (line.startsWith("--") || line.startsWith("#") || line.matches("^\\s*$"));
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

	/**
	 * Reads the next non comment line with {@link Var}s substituted.
	 */
	private String readLine() throws IOException {
		if (unread) {
			unread = false;
			return lastLine;
		}
		do {
			lineNr++;
			lastLine = in.readLine(); // update temporary so errors during var subst show the line
		} while (isComment(lastLine));
		lastLine = substVars(lastLine);
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
			int closeAt = line.indexOf('}', open);
			String var = line.substring(open+1, closeAt);
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
		String line = readLine();
		Package base = pkg(line.substring(0, line.indexOf(':')).trim());
		line = readLine();
		Packages above = Packages.NONE;
		List<Module> modules = new ArrayList<>();
		int level = 1;
		while (line != null && line.startsWith("\t") && line.indexOf('[') > 0) {
			String[] sets = line.split("\\]\\[");
			Packages aboveLevel = above;
			for (String set : sets) {
				Packages packages = Packages.parse(set);
				Packages plusList = aboveLevel.union(packages);
				for (Package p : packages)
					modules.add(new Module(base, p, level, p.hub ? plusList : above));
				above = above.union(packages);
			}
			level++;
			line = readLine();
		}
		unreadLine();
		// complete modules with context
		Module[] layers = new Module[modules.size()];
		for (int i = 0; i < layers.length; i++) {
			layers[i] = modules.get(i).in(above);
		}
		return new Structure(layers);
	}

	private Goal parseGoal() throws IOException {
		String line = readLine();
		int colonAt = line.indexOf(':');
		if (colonAt < 0)
			fail("Expected a goal but found", line);
		Label name = label(line.substring(0, colonAt).trim());
		int endOfSourceAt = line.indexOf(']');
		Src[] from = Src.split(line.substring(line.indexOf('[')+1, endOfSourceAt));
		int toAt = line.indexOf(" to ");
		int ranAt = line.indexOf(" run ");
		Folder dir = folder(vars.resolve(Var.DEFAULT_OUTDIR, vars));
		Dest to = toAt > 0
				? Dest.parse(line.substring(toAt + 4, ranAt < 0 ? line.length() : ranAt).trim())
				: Dest.yieldTo(dir);
		Run run = ranAt < 0 ? Run.NONE : Run.parse(line.substring(ranAt+5).trim());
		return new Goal(name, from, to, run, parseDependencies());
	}

	private Dependency[] parseDependencies() throws IOException {
		List<Dependency> dependencies = new ArrayList<>();
		String line = readLine();
		Dependency group = null;
		Folder to = folder(vars.resolve(Var.DEFAULT_LIBDIR, vars));
		while (line != null && line.startsWith("\t") && !isComment(line)) {
			Dependency dep = Dependency.parse(line.trim(), to);
			if (dep.resource.virtual) {
				group = dep;
			} else {
				if (line.startsWith("\t\t") && group != null) {
					dep = new Dependency(dep.resource,
						dep.in == Packages.NONE ? group.in : dep.in,
						dep.to == to ? group.to : dep.to);
				} else {
					group = null;
				}
				dependencies.add(dep);
			}
			line = readLine();
		}
		unreadLine();
		return dependencies.toArray(new Dependency[0]);
	}

	private Sequence parseSequence() throws IOException {
		String line = readLine();
		if (line.indexOf("=") <= 0)
			fail("Expected sequence but found", line);
		String[] segs = line.split("\\s*[= ]\\s*");
		Goal[] seq = new Goal[segs.length-1];
		for (int i = 1; i < segs.length; i++)
			seq[i-1] =  new Goal(label(segs[i])); // these are shallow for now and are replace later
		return new Sequence(label(segs[0]), seq);
	}

}
