package se.jbee.build;

import java.lang.reflect.Constructor;

import se.jbee.build.report.Progress;
import se.jbee.build.tool.Compilation;

public interface Compiler {

	void compile(Compilation unit, Progress report);

	static Compiler newInstance(String fileExtension, Var config) {
		try {
			Class<?> compiler = Class.forName(config.resolve(Var.COMPILER_GROUP+":"+fileExtension));
			Constructor<?> constructor = compiler.getDeclaredConstructors()[0];
			return (Compiler)(constructor.getParameterTypes().length == 1
					? constructor.newInstance(config)
					: constructor.newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
