package se.jbee.build.parse;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import se.jbee.build.WrongFormat;

public final class Vars implements Var {

	private final Map<String, String> vars = new HashMap<>();

	public Vars(String... args) {
		for (int i = 0; i < args.length; i++) {
			if (args[i].matches("-[a-zA-Z0-9]")) {
				vars.put(args[i], args[++i]);
			}
		}
		defineVar("time:now", String.valueOf(System.currentTimeMillis()));
	}

	public void defineVar(String name, String val) {
		if (!vars.containsKey(name))
			vars.put(name, val);
	}

	@Override
	public String resolve(String varExpr, Var env) {
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
				if (!vars.containsKey(var)) {
					res = resolveExternal(var, env);
					vars.put(var, res);
					if (res != null)
						return res;
				}
			} else {
				return var; // a default value
			}
		}
		return "";
	}

	private static String resolveExternal(String var, Var env) {
		try {
			Class<?> resolverType = Class.forName(Var.class.getName()+"_"+var.replace(':', '_'));
			Var resolver = (Var) resolverType.getDeclaredConstructor().newInstance();
			return resolver.resolve(var, env);
		} catch (ClassNotFoundException e) {
			throw new WrongFormat("Unknown variable", var);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new WrongFormat("Failed to instanciate variable resolver: "+e.getMessage(), var);
		} catch (ClassCastException e) {
			throw new WrongFormat("Variable resolver does not implement "+Var.class.getName(), var);
		}
	}

}
