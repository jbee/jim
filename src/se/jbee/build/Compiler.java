package se.jbee.build;

import java.lang.reflect.Constructor;

import se.jbee.build.report.Progress;

/**
 * In JIM a {@link Compiler} is a tool to yield an output file for a certain
 * type of input file.
 *
 * The implementation is bound using a {@link Var} with the
 * {@link Var#COMPILER_GROUP} and the name of the file extension the compiler
 * takes as input.
 */
public interface Compiler {

	void compile(Compilation unit, Progress report);

	/**
	 * Creates a new instance of a certain compiler. The class name of the
	 * implementation is resolved from the {@link Var} config. The class is expected
	 * to have a constructor accepting {@link Var} or no arguments and has to
	 * implement the {@link Compiler} interface.
	 *
	 * @param fileExtension
	 *            not null, like {@code java} for Java source files
	 * @param config
	 *            the context containing the binding but also other variables to
	 *            initialise the implementation
	 * @return a new instance, never null
	 */
	static Compiler newInstance(String fileExtension, Var config) {
		try {
			Class<?> compiler = Class.forName(config.resolve(Var.compiler(fileExtension)));
			Constructor<?> constructor = compiler.getDeclaredConstructors()[0];
			return (Compiler)(constructor.getParameterTypes().length == 1
					? constructor.newInstance(config)
					: constructor.newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
