package se.jbee.build.parse;

import java.util.HashMap;
import java.util.Map;

import se.jbee.build.WrongFormat;

public final class JimVariables implements Variables {

	private final Map<String, String> vars = new HashMap<>();

	public JimVariables(String... args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].matches("-[a-zA-Z0-9]")) {
				vars.put(args[i], args[++i]);
			}
		}
	}

	@Override
	public String resolve(String varExpr) {
		String[] chain = varExpr.split("\\|");
		for (String var : chain) {
			if (var.startsWith("-")) {
				String res = vars.get(var);
				if (res != null)
					return res;
			} else if (var.indexOf(':') >= 0) {
				String res = vars.get(var);
				if (res != null)
					return res;
				res = resolveComputed(var);
				vars.put(var, res);
				return res;
			} else {
				return var; // a default value
			}
		}
		return "";
	}

	private String resolveComputed(String var) {
		if ("time:now".equals(var))
			return String.valueOf(System.currentTimeMillis());
		//TODO more time:
		//TODO more on path:
		//TODO more git:
		throw new WrongFormat("Unknown variable: "+var);
	}

}
