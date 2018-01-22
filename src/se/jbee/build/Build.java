package se.jbee.build;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		      		if (line.matches("^([a-zA-Z0-9_]+\\.)+[a-zA-Z0-9_]:\\s*$")) {
		      			modules = parseStructure(in);
		      		} else if (line.matches("^[-a-z_]+\\s*:\\s*\\[.*")) {
		      			goals.add(parseGoal(in));
		      		} else if (line.matches("\"^[-a-z_]+\\s*=.*")) {
		      			sequences.add(parseSequence(in));
		      		} else {
		      			in.fail("What do you mean?");
		      		}
		      	}
		    }
		}
		return new Build(modules, goals.toArray(new Goal[0]), sequences.toArray(new Sequence[0]));
	}

	private static Structure parseStructure(Parser in) {

		return null;
	}

	private static Goal parseGoal(Parser in) {

		return null;
	}

	private static Sequence parseSequence(Parser in) throws IOException {
		String[] segs = in.readLine().split("\\s*[= ]\\s*");
		Goal[] seq = new Goal[segs.length-1];
		for (int i = 1; i < segs.length; i++)
			seq[i-1] =  new Goal(new Name(segs[i])); // these are shallow for now and are replace later
		return new Sequence(new Name(segs[0]), seq);
	}
}
