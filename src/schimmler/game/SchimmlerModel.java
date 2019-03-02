package schimmler.game;

import schimmler.architecture.Level;
import schimmler.architecture.LevelType;
import schimmler.architecture.Model;
import schimmler.architecture.Tile;

public class SchimmlerModel extends Model {

	public SchimmlerModel() {
		super();
	}

	@Override
	public void init() {

		
		registerTileType("square", new MatrixTileType() {
			@Override
			public void init(Tile tile) {
				tile.getData().put("color", "#007FFF");
			}
			@Override
			public int[][] getGeometryMatrix() {
				int[][] geometry = { { 1, 1 }, { 1, 1 } };
				return geometry;
			}
		});
		registerTileType("hookUL", new MatrixTileType() {
			@Override
			public int[][] getGeometryMatrix() {
				int[][] geometry = { { 1, 1 }, { 1, 0 } };
				return geometry;
			}
		});

		registerTileType("hookUR", new MatrixTileType() {
			@Override
			public int[][] getGeometryMatrix() {
				int[][] geometry = { { 1, 1 }, { 0, 1 } };
				return geometry;
			}
		});

		registerTileType("hookBL", new MatrixTileType() {
			@Override
			public int[][] getGeometryMatrix() {
				int[][] geometry = { { 1, 0 }, { 1, 1 } };
				return geometry;
			}
		});

		registerTileType("hookBR", new MatrixTileType() {
			@Override
			public int[][] getGeometryMatrix() {
				int[][] geometry = { { 0, 1 }, { 1, 1 } };
				return geometry;
			}
		});

		registerLevelType("schimmler", new LevelType() {
			@Override
			public void init(Level level) {
				level.setWidth(4);
				level.setHeight(6);
				level.addTile("square", createTile("square", 1, 1));
				level.addTile("hookUL", createTile("hookUL", 0, 0));
				level.addTile("hookUR", createTile("hookUR", 2, 0));
				level.addTile("hookBL", createTile("hookBL", 0, 2));
				level.addTile("hookBR", createTile("hookBR", 2, 2));
			}

			@Override
			public boolean won(Level level) {
				return level.getTile("square").getX() == 0 && level.getTile("square").getY() == 2;
			}

		});

		setLevel(createLevel("schimmler"));

	}

	public static void main(String[] args) {
		SchimmlerModel model = new SchimmlerModel();
		model.registerPlugin(new SchimmlerView());
		model.registerPlugin(new SchimmlerController());
		model.start();
	}

}
