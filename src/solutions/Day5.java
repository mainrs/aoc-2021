package solutions;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import api.AbstractProblem;
import api.Solution;
import api.Streams;

public class Day5 extends AbstractProblem {

	static class Point {
		private final int x;
		private final int y;

		public static Point fromString(String s) {
			String[] parts = s.split(",");
			assert parts.length == 2;

			return new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
		}

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Point p) {
				return p.x == x && p.y == y;
			}

			return false;
		}

		@Override
		public String toString() {
			return String.format("Point[x=%s, y=%s]", x, y);
		}
	}

	static class Line {
		private final Point from;
		private final Point to;
		private final List<Point> points;

		private Line(Point from, Point to, List<Point> points) {
			this.from = from;
			this.to = to;
			this.points = points;
		}

		private static IntStream constructStream(int from, int to) {
			if (from + 1 == to) {
				return IntStream.generate(() -> from);
			}

			if (from < to) {
				return IntStream.range(from, to + 1);
			} else {
				return IntStream.range(to, from + 1);
			}
		}

		private static List<Point> constructLinePoints(Point from, Point to) {
			IntStream xValues = constructStream(from.x, to.x + 1);
			IntStream yValues = constructStream(from.y, to.y + 1);

			return Streams.zip(xValues.boxed(), yValues.boxed(), (x, y) -> new Point(x, y)).toList();
		}

		public static Line fromString(String s) {
			String[] parts = s.split(" -> ");
			assert parts.length == 2;

			Point from = Point.fromString(parts[0]);
			Point to = Point.fromString(parts[1]);
			List<Point> points = constructLinePoints(from, to);

			return new Line(from, to, points);
		}

		@Override
		public String toString() {
			return String.format("Line[from=%s, to=%s, points=%s]", from, to, points);
		}

		public List<Point> getPoints() {
			return points;
		}

		public boolean isStraight() {
			return from.x == to.x || from.y == to.y;
		}
	}

	public Day5() {
		super(2);
	}

	@Solution(1)
	public void part1() throws Exception {
		List<String> lines = readInput();
		List<Line> vents = lines.stream()
				.map(Line::fromString)
				.filter(Line::isStraight)
				.toList();

		// Convert the stream of lines into a flattened stream of points.
		// Group the points based on their equality (i.e., the resulting key of the map
		// will be the point and the value will be a list of points that are equal.)
		//
		// Then stream over the values of that map, counting how many elements could be
		// found. This corresponds to the number of overlapping lines on that
		// coordinate.
		Function<List<Line>, Long> lambda = (arg) -> arg
				.stream()
				.flatMap(line -> line.getPoints().stream())
				.collect(Collectors.groupingBy(l -> l))
				.values()
				.stream()
				.map(List::size)
				.filter(v -> v >= 2)
				.count();

		System.out.println(lambda.apply(vents));
	}

	@Solution(2)
	public void part2() throws Exception {
	}
}
