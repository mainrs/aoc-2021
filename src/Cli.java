import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import api.NoSubProblemException;

/**
 * A utility CLI that can compile and run Advent of Code solutions.
 */
public class Cli {

	private static int parseArguments(String[] args) {
		try {
			return Integer.valueOf(args[0]);
		} catch (RuntimeException e) {
			System.err.println("Usage: java Cli.java <dayToRun>");
			System.exit(1);
		}

		return 0; // only needed for compilation.
	}

	/**
	 * Entry point.
	 *
	 * @param args The first argument is the numerical day (1-25). The corresponding
	 *             solution will then be compiled and run with its matching input.
	 */
	public static void main(String[] args) {
		// Construct class name to run.
		int dayToRun = parseArguments(args);
		String classname = "Day" + dayToRun;
		String filename = classname + ".java";
		Path compilationTarget = Paths.get("src", "solutions", filename);

		// Compile the class file.
		DiagnosticCollector<JavaFileObject> diagnosticListener = new DiagnosticCollector<>();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticListener, null,
				StandardCharsets.UTF_8);

		// Construct the correct classpath.
		List<String> options = new ArrayList<>();
		options.add("-classpath");
		options.add(System.getProperty("java.class.path"));

		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(compilationTarget);
		boolean couldCompile = compiler.getTask(null, fileManager, diagnosticListener, options, null, compilationUnits)
				.call();

		if (!couldCompile) {
			for (Diagnostic<?> diagnostic : diagnosticListener.getDiagnostics()) {
				System.err.printf("Error on line %s in %s%n", diagnostic.getLineNumber(), diagnostic);
			}
		}

		// Create a new url classloader, load the newly compiled file and execute its
		// solve method.
		URL[] urls;
		try {
			urls = new URL[] {
				new File("src").toURI().toURL()
			};
		} catch (MalformedURLException e) {
			throw new NoSubProblemException(e);
		}

		try(URLClassLoader cl = new URLClassLoader(urls)) {
			Class<?> solution = cl.loadClass("solutions." + classname);
			Method solveMethod = solution.getMethod("solve");

			Object instance = solution.getDeclaredConstructor().newInstance();
			solveMethod.invoke(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
