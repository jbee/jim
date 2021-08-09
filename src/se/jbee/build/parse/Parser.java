package se.jbee.build.parse;

import static java.lang.Long.parseLong;
import static java.util.Arrays.copyOfRange;
import static se.jbee.build.Dependencies.dependsOn;
import static se.jbee.build.Filter.filter;
import static se.jbee.build.Folder.folder;
import static se.jbee.build.Label.label;
import static se.jbee.build.Package.pkg;
import static se.jbee.build.Run.run;
import static se.jbee.build.Timestamp.timestamp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.jbee.build.Build;
import se.jbee.build.Compiler;
import se.jbee.build.Dependencies;
import se.jbee.build.Dependency;
import se.jbee.build.Filter;
import se.jbee.build.Folder;
import se.jbee.build.From;
import se.jbee.build.Goal;
import se.jbee.build.Home;
import se.jbee.build.Label;
import se.jbee.build.Main;
import se.jbee.build.Package;
import se.jbee.build.Packages;
import se.jbee.build.Run;
import se.jbee.build.Sequence;
import se.jbee.build.Structure;
import se.jbee.build.Structure.Module;
import se.jbee.build.To;
import se.jbee.build.Tool;
import se.jbee.build.Url;
import se.jbee.build.Var;
import se.jbee.build.WrongFormat;
import se.jbee.build.tool.Javac;
import se.jbee.build.tool.Yield;

public final class Parser implements AutoCloseable {

	public static Run parseRunner(File runner, Vars vars) throws UncheckedIOException, WrongFormat {
		try (Parser in = new Parser(runner, vars)) {
			return parseRunner(in);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static Run parseRunner(Parser in) throws IOException {
		String line;
		try {
			while ((line = in.readLine()) != null) {
				int equalAt = line.indexOf("=");
				if (equalAt > 0) {
					defineVar(in, line);
				} else {
					int colonAt = line.indexOf(':');
					Label tool = Label.label(line.substring(0, colonAt).trim());
					String[] mainAndArgs = line.substring(colonAt+1).trim().split("\\s+");
					Main impl = Main.main(mainAndArgs[0]);
					List<Url> deps = new ArrayList<>();
					while ((line = in.readLine()) != null && line.startsWith("\t")) {
						deps.add(Url.url(line.trim()));
					}
					return run(tool, copyOfRange(mainAndArgs, 1, mainAndArgs.length)).connect(impl, deps.toArray(new Url[0]));
				}
			}
			throw new WrongFormat("Runner declaration missing.", "");
		} catch (WrongFormat e) {
			throw e.at(in.lineNr, in.lastLine);
		}
	}

	private static void defineVar(Parser in, String line) {
		int equalAt = line.indexOf("=");
		in.vars.define(line.substring(0, equalAt).trim(), line.substring(equalAt+1).trim());
	}

	public static Build parseBuild(File build, String... args) throws UncheckedIOException, WrongFormat {
		try (Parser in = new Parser(build, new Vars(args))) {
			return parseBuild(new Home(build.getParentFile().getParentFile()), in);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
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
					int equalAt = line.indexOf("=");
					if (equalAt > 0) {
						defineVar(in, line);
					} else {
						in.unreadLine();
						int dotAt = line.indexOf('.');
						if (dotAt > 0 && dotAt < colonAt) {
							modules = in.parseStructure();
						} else {
							goals.add(in.parseGoal(home));
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
		long since = parseLong(in.vars.resolve(Var.TIME_NOW));
		return new Build(timestamp(since), home, modules, goals.toArray(new Goal[0]), sequences.toArray(new Sequence[0]), in.compilers());
	}

	private Tool[] compilers() {
		if (!vars.isDefined(Var.COMPILER_GROUP+":java"))
			vars.define(Var.COMPILER_GROUP+":java", Javac.class.getName());
		List<Tool> res = new ArrayList<>();
		Map<String, Compiler> compilersByImpl = new IdentityHashMap<>(); // make sure just one impl per type
		for (Entry<String, String> e : vars) {
			if (Var.group(e.getKey()).equals(Var.COMPILER_GROUP)) {
				String fileExtension = Var.name(e.getKey());
				String compilerImpl = vars.resolve(Var.compiler(fileExtension));
				Compiler compiler = compilersByImpl.computeIfAbsent(compilerImpl,
						k -> Compiler.newInstance(fileExtension, vars));
				res.add(new Tool(filter("*." + fileExtension), compiler));
			}
		}
		res.add(new Tool(Filter.UNFILTERED, Yield.COPY));
		return res.toArray(new Tool[0]);
	}

	private static boolean isComment(String line) {
		return line != null && (line.startsWith("--") || line.startsWith("#") || line.matches("^\\s*$"));
	}


	private final BufferedReader in;
	private final Vars vars;
	private final Map<Label, Run> runners = new HashMap<>();

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
				Packages packages = Packages.parsePackages(set);
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

	private Goal parseGoal(Home home) throws IOException {
		String line = readLine();
		int colonAt = line.indexOf(':');
		if (colonAt < 0)
			fail("Expected a goal but found", line);
		Label name = label(line.substring(0, colonAt).trim());
		int endOfSourceAt = line.indexOf(']');
		From[] from = From.parseSources(line.substring(line.indexOf('[')+1, endOfSourceAt));
		int toAt = line.indexOf(" to ");
		int ranAt = line.indexOf(" run ");
		Folder dir = folder(vars.resolve(Var.DEFAULT_OUTDIR));
		To to = toAt > 0
				? To.parseDest(line.substring(toAt + 4, ranAt < 0 ? line.length() : ranAt).trim())
				: To.yieldTo(dir);
		Run run = ranAt < 0 ? Run.NOTHING : Run.parse(line.substring(ranAt+5).trim());
		return new Goal(name, from, to, parseRunnerFor(run, home), parseDependencies());
	}

	private Run parseRunnerFor(Run run, Home home) {
		if (run.isNothing())
			return run;
		final Label tool = run.tool;
		if (!runners.containsKey(tool)) {
			try {
				runners.put(tool, parseRunner(new File(new File(home.dir, Folder.RUN.name), tool.name), vars));
			} catch (UncheckedIOException e) {
				if (e.getCause() instanceof FileNotFoundException) {
					throw new WrongFormat("File for referenced runner is missing.", tool.name);
				}
				throw new WrongFormat("Error reading reference runner file: "+e.toString(), tool.name);
			} catch (WrongFormat e) {
				throw new WrongFormat("File for reference runner has a syntax error: "+e.toString(), tool.name);
			}
		}
		return run.use(runners.get(tool));
	}

	private Dependencies parseDependencies() throws IOException {
		List<Dependency> dependencies = new ArrayList<>();
		String line = readLine();
		Dependency group = null;
		Folder to = folder(vars.resolve(Var.DEFAULT_LIBDIR));
		while (line != null && line.startsWith("\t") && !isComment(line)) {
			Dependency dep = Dependency.parseDependency(line.trim(), to);
			if (dep.resource.virtual) {
				group = dep;
			} else {
				if (line.startsWith("\t\t") && group != null) {
					dep = new Dependency(dep.resource,
						dep.in == Packages.ALL ? group.in : dep.in,
						dep.to == to ? group.to : dep.to);
				} else {
					group = null;
				}
				dependencies.add(dep);
			}
			line = readLine();
		}
		unreadLine();
		return dependsOn(dependencies);
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
