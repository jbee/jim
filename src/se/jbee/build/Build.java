package se.jbee.build;

import static java.util.Arrays.asList;
import static se.jbee.build.Label.label;
import static se.jbee.build.Package.pkg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se.jbee.build.Structure.Module;

public final class Build {

	public final Structure modules;
	public final Goal[] goals;
	public final Sequence[] sequences;

	public Build(Structure modules, Goal[] goals, Sequence[] sequences) {
		this.modules = modules;
		this.goals = goals;
		this.sequences = sequences;
	}

	public static Build parse(File build) throws FileNotFoundException, IOException {
		Structure modules = null;
		List<Goal> goals = new ArrayList<>();
		List<Sequence> sequences = new ArrayList<>();
		try (Parser in = new Parser(build)) {
		    String line;
		    while ((line = in.readLine()) != null) {
		      	if (!line.startsWith("--") && !line.trim().isEmpty()) {
		      		int colon = line.indexOf(':');
		      		if (colon > 0) {
		      			int dot = line.indexOf('.');
		      			if (dot > 0 && dot < colon) {
		      				modules = parseStructure(in);
		      			} else {
		      				goals.add(parseGoal(in));
		      			}
		      		} else {
		      			sequences.add(parseSequence(in));
		      		}
		      	}
		    }
		}
		return new Build(modules, goals.toArray(new Goal[0]), sequences.toArray(new Sequence[0]));
	}

	private static Structure parseStructure(Parser in) throws IOException {
		String line = in.lastLine();
		Package base = pkg(line.substring(0, line.indexOf(':')).trim());
		line = in.readLine();
		Set<Package> above = new HashSet<>();
		List<Module> modules = new ArrayList<>();
		while (line != null && line.startsWith("\t") && line.indexOf('[') > 0) {
			String[] sets = line.split("\\]\\[");
			for (String set : sets) {
				String[] members = set.trim().replaceAll("[\\[\\]]+", "").split("[ ,]\\s*");
				Package[] packages = new Package[members.length];
				for (int i = 0; i < members.length; i++)
					packages[i] = pkg(members[i]);
				above.addAll(asList(packages));
				Package[] whitelist = above.toArray(new Package[0]);
				for (Package p : packages) {
					Module m = new Module(p, whitelist);
					modules.add(m);
				}
				above.removeAll(asList(packages)); // other set on same level has no access...
			}
		}
		Module[] layers = new Module[modules.size()];
		for (int i = 0; i < layers.length; i++) {
			HashSet<Package> all = new HashSet<>(above);
			all.removeAll(asList(modules.get(i).whitelist));
			layers[i] = modules.get(i).blacklist(all.toArray(new Package[0]));
		}
		in.unreadLine();
		return new Structure(base, layers);
	}

	private static Goal parseGoal(Parser in) {
		String line = in.lastLine();
		Label name = label(line.substring(0, line.indexOf(':')).trim());
		String[] sn = line.substring(line.indexOf('[')+1, line.indexOf(']')).split("[ ,]\\s*");
		Source[] sources = new Source[sn.length];
		for (int i = 0; i < sn.length; i++)
			sources[i] = Source.parse(sn[i]);
		return null;
	}

	private static Sequence parseSequence(Parser in) {
		String line = in.lastLine();
		if (line.indexOf("=") <= 0)
			in.fail("Expected sequence but found ");
		String[] segs = line.split("\\s*[= ]\\s*");
		Goal[] seq = new Goal[segs.length-1];
		for (int i = 1; i < segs.length; i++)
			seq[i-1] =  new Goal(label(segs[i])); // these are shallow for now and are replace later
		return new Sequence(label(segs[0]), seq);
	}
}
