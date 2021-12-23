package api;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Utils {
	private Utils() {
	}

	@SafeVarargs
	public static <T> List<List<T>> zipN(List<T>... lists) {
		return Arrays.asList(lists);
	}

	private static <T> List<T> as(List<String> lines, Function<String, T> fn) {
		return lines.stream().map(fn).toList();
	}

	public static List<Double> asDouble(List<String> lines) {
		return as(lines, Double::valueOf);
	}

	public static List<Integer> asInt(List<String> lines) {
		return as(lines, Integer::valueOf);
	}
}
