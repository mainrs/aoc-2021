package api;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A helper class that handles file loading and method execution for problems
 * and their solution.
 */
public abstract class AbstractProblem {
	/**
	 * The number of child problems the problem has.
	 *
	 * Usually, each day has two problems.
	 */
	private final int childProblems;

	public AbstractProblem(int childProblems) {
		if (childProblems < 1) {
			throw new IllegalArgumentException("Solution has to have at least one sub-problem");
		}

		this.childProblems = childProblems;
	}

	/**
	 * Reads the input file of a given child problem.
	 *
	 * @param childProblem The child problem to read the input file from.
	 * @return
	 */
	protected List<String> readInput() throws IOException {
		// Detect the problem by checking the class name.
		String className = getClass().getSimpleName();

		// Construct the right path for loading the input file.
		String filename = className.toLowerCase() + ".txt";
		Path inputFile = Paths.get("inputs", filename);

		// Load file content.
		return Files.readAllLines(inputFile);
	}

	protected List<String> readInputSkipEmptyLines() throws IOException {
		return readInput()
			.stream()
			.filter(Predicate.not(String::isBlank))
			.collect(Collectors.toList());
	}

	/**
	 * Solves each child problem.
	 *
	 * The method reads in the corresponding input file. The file has to be named
	 * {@code day<dayNumber>_<childProblem>.txt}.
	 *
	 * @throws NoSubProblemException if the solution tries to solve a sub-problem
	 *                               without an input file or a corresponding
	 *                               solution method.
	 */
	public void solve() {
		for (int i = 1; i <= childProblems; i++) {
			final int currentSubProblem = i;

			// Detect matching solution implementation.
			Optional<Method> solutionMethod = Stream.of(getClass().getDeclaredMethods())
					.filter(m -> m.isAnnotationPresent(Solution.class))
					.filter(m -> m.getAnnotation(Solution.class).value() == currentSubProblem)
					.findFirst();

			if (solutionMethod.isEmpty()) {
				throw new NoSubProblemException();
			}

			try {
				solutionMethod.get().invoke(this);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
}
