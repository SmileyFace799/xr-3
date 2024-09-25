package no.smileyface.xr3;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Cube {
	private final int size;
	private final Chunk[][][] chunkArray;

	public Cube(int size) {
		this.size = size;
		chunkArray = loadCube();
	}

	public int getSize() {
		return size;
	}

	public Chunk getChunk(int[] xyz) {
		return getChunk(xyz[0], xyz[1], xyz[2]);
	}

	public Chunk getChunk(int x, int y, int z) {
		return chunkArray[x][y][z];
	}

	public void setChunk(int[] xyz, Chunk chunk) {
		setChunk(xyz[0], xyz[1], xyz[2], chunk);
	}

	public void setChunk(int x, int y, int z, Chunk chunk) {
		chunkArray[x][y][z] = chunk;
	}

	public Stream<Entry3D<Chunk>> stream() {
		return IntStream
				.range(0, size)
				.boxed()
				.flatMap(x -> IntStream
						.range(0, size)
						.boxed()
						.flatMap(y -> IntStream
								.range(0, size)
								.mapToObj(z -> new Entry3D<>(x, y, z, chunkArray[x][y][z]))
						)
				);
	}

	private void shiftChunks(Map<int[], int[]> shiftMap) {
		Map<int[], Chunk> entriesToInsert = shiftMap
				.entrySet()
				.stream()
				.map(shift -> Map.entry(shift.getValue(), getChunk(shift.getKey())))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		shiftMap.keySet().forEach(oldXyz -> setChunk(oldXyz, null));
		entriesToInsert.forEach(this::setChunk);
	}

	public void rotate(RotationAxis axis, int slice, boolean clockwise) {
		Map<int[], int[]> shiftMap = IntStream.range(0, size)
				.boxed()
				.flatMap(i -> IntStream.range(0, size)
						.mapToObj(j -> Map.entry(new int[]{i, j}, clockwise == (axis == RotationAxis.Y) ? new int[]{-j + size - 1, i} : new int[]{j, -i + size - 1}))
				)
				.collect(Collectors.toMap(
						entry -> axis.get3dAroundAxis(slice, entry.getKey()),
						entry -> axis.get3dAroundAxis(slice, entry.getValue())
				));
		shiftMap.keySet().forEach(xyz -> getChunk(xyz).rotate(axis, clockwise));
		shiftChunks(shiftMap);
	}

	private Chunk[][][] loadCube() {
		return IntStream.range(0, size).mapToObj(x -> this.loadSide()).toArray(Chunk[][][]::new);
	}

	private Chunk[][] loadSide() {
		return IntStream.range(0, size).mapToObj(y -> loadRow()).toArray(Chunk[][]::new);
	}

	private Chunk[] loadRow() {
		return IntStream.range(0, size).mapToObj(z -> loadChunk()).toArray(Chunk[]::new);
	}

	private Chunk loadChunk() {
		return new Chunk();
	}

	public enum RotationAxis {
		X(0),
		Y(1),
		Z(2);

		private final int constIndex;

		RotationAxis(int constIndex) {
			this.constIndex = constIndex;
		}

		public int[] get3dAroundAxis(int sliceIndex, int... ij) {
			int[] threeD = new int[3];
			threeD[constIndex] = sliceIndex;
			if (constIndex > 0) {
				System.arraycopy(ij, 0, threeD, 0, constIndex);
			}
			if (constIndex < threeD.length - 1) {
				System.arraycopy(ij, constIndex, threeD, constIndex + 1, threeD.length - constIndex - 1);
			}
			return threeD;
		}
	}

	public record Entry3D<V>(int x, int y, int z, V value) {}
}
