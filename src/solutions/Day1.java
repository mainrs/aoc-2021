package solutions;

import static api.Utils.*;
import api.AbstractProblem;
import api.Solution;

import java.util.List;

public final class Day1 extends AbstractProblem {
	public Day1() {
		super(2);
	}

	private int calculate(List<Integer> numbers) {
		int timesIncreased = 0;

		for (int i = 1; i < numbers.size(); i++) {
			int current = Integer.valueOf(numbers.get(i));
			int last = Integer.valueOf(numbers.get(i - 1));

			if (last < current) {
				timesIncreased++;
			}
		}

		return timesIncreased;
	}

	@Solution(1)
	public void part1() throws Exception {
		List<Integer> numbers = asInt(readInput());
		System.out.println(calculate(numbers));
	}

	@Solution(2)
	public void part2() throws Exception {
		List<Integer> lines = asInt(readInput());

		var skipped1 = lines
				.stream()
				.skip(1)
				.toList();
		var skipped2 = lines
				.stream()
				.skip(1)
				.toList();

		// Create window.
		List<Integer> numbers = zipN(skipped2, skipped1, lines)
				.stream()
				.map(window -> window.stream().reduce(0, Integer::sum))
				.toList();

		System.out.println(calculate(numbers));
	}
}
