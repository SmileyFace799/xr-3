package no.smileyface.xr3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Chunk {
	private final List<Color> xy;
	private final List<Color> xz;
	private final List<Color> yz;

	public Chunk() {
		xy = new ArrayList<>(List.of(Color.WHITE, Color.BLUE, Color.YELLOW, Color.RED));
		xz = new ArrayList<>(List.of(Color.GREEN, Color.RED, Color.ORANGE, Color.BLUE));
		yz = new ArrayList<>(List.of(Color.WHITE, Color.GREEN, Color.YELLOW, Color.ORANGE));
	}

	public Color getTop() {
		return xy.getFirst();
	}

	public Color getBottom() {
		return xy.get(2);
	}

	public Color getFront() {
		return xz.getFirst();
	}

	public Color getBack() {
		return xz.get(2);
	}

	public Color getLeft() {
		return xy.get(1);
	}

	public Color getRight() {
		return xy.get(3);
	}

	private void shiftColors(Map<Color, Color> shiftMap) {
		Stream.of(xy, xz, yz).forEach(list -> list.replaceAll(shiftMap::get));
	}

	public void rotate(Cube.RotationAxis axis, boolean clockwise) {
		List<Color> shiftReference = switch (axis) {
			case X -> yz;
			case Y -> xz;
			case Z -> xy;
		};
		shiftColors(Arrays.stream(Color.values()).collect(Collectors.toMap(
				Function.identity(),
				color -> shiftReference.contains(color)
						? shiftReference.get((shiftReference.indexOf(color) + (clockwise ? 1 : shiftReference.size() - 1)) % shiftReference.size())
						: color
		)));
	}

	public enum Color {
		WHITE(1, 1, 1),
		YELLOW(1, 1, 0),
		BLUE(0, 0, 1),
		RED(1, 0, 0),
		GREEN(0, 1, 0),
		ORANGE(1, 0.5, 0);

		private final double r;
		private final double g;
		private final double b;

		Color(double r, double g, double b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		double[] getRgb() {
			return new double[]{r, g, b};
		}
	}
}
