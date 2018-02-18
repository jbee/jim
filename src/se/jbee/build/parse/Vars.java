package se.jbee.build.parse;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import se.jbee.build.Folder;
import se.jbee.build.WrongFormat;

public final class Vars implements Var {

	/**
	 * The set of explicitly command line arguments of type {@code -X} that are not
	 * used in the build file.
	 */
	private final Set<String> unusedArgs = new HashSet<>();
	private final Map<String, String> vars = new HashMap<>();

	public Vars(String... args) {
		for (int i = 0; i < args.length; i++) {
			String key = args[i];
			if (key.length() >= 2 && key.charAt(0) == '-' && Character.isLetterOrDigit(key.charAt(1))) {
				if (key.length() > 2) {
					defineArg(key.substring(0, 2), key.substring(2));
				} else {
					defineArg(key, args[++i]);
				}
			}
		}
		define(TIME_NOW, String.valueOf(System.currentTimeMillis()));
		define(DEFAULT_OUTDIR, Folder.OUTPUT.name);
		define(DEFAULT_LIBDIR, Folder.LIB.name);
	}

	private void defineArg(String name, String val) {
		unusedArgs.add(name);
		vars.put(name, val);
	}

	public Iterable<String> unusedArgs() {
		return unusedArgs;
	}

	public void define(String name, String val) {
		if (!vars.containsKey(name) || name.startsWith("default:"))
			vars.put(name, val);
	}

	@Override
	public String resolve(String varExpr, Var env) {
		String[] chain = varExpr.split("\\|");
		for (String var : chain) {
			if (var.startsWith("-")) {
				String res = vars.get(var);
				unusedArgs.remove(var);
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
