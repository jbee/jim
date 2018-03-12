package se.jbee.build.parse;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import se.jbee.build.Folder;
import se.jbee.build.Var;
import se.jbee.build.WrongFormat;

public final class Vars implements Var, Iterable<Entry<String, String>> {

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

	@Override
	public Iterator<Entry<String, String>> iterator() {
		return vars.entrySet().iterator();
	}

	private void defineArg(String name, String val) {
		unusedArgs.add(name);
		vars.put(name, val);
	}

	public Iterable<String> unusedArgs() {
		return unusedArgs;
	}

	public void define(String name, String val) {
		if (isDefined(name) && !name.startsWith("default:"))
			throw new WrongFormat("Cannot redefine variable", name);
		vars.put(name, val);
	}

	public boolean isDefined(String name) {
		return vars.containsKey(name);
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
			Class<?> resolverType = Class.forName(Var.class.getPackage().getName()+".var.Var_"+var.replace(':', '_'));
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
