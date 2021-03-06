package schimmler.game;

import java.io.File;

import lightHouseSimulator.LightHouseOnlinePlugin;
import lightHouseSimulator.LightHousePlugin;
import schimmler.architecture.Level;
import schimmler.architecture.LevelType;
import schimmler.architecture.MatrixTileType;
import schimmler.architecture.Model;
import schimmler.architecture.Tile;
import schimmler.architecture.TileType;
import schimmler.architecture.plugin.Plugin;
import schimmler.architecture.plugin.View;
import schimmler.jar.JarFilePluginLoader;
import schimmler.js.JavaScriptPluginLoader;
import schimmler.test.TestController;
import schimmler.test.TestFilter;

public class SchimmlerModel extends Model {

	public SchimmlerModel() {
		super();
	}

	@Override
	public void init() {
		registerMenu();
		registerSchimmler();
		setLevel(createLevel("menu"));
	}

	private void registerMenu() {

		registerTileType("menuGreen", new MatrixTileType() {
			private int[][] geometry = { { 1, 1, 1 } };

			@Override
			public void init(Tile tile) {
				tile.getData().put("color", "#00FF00");
			}

			@Override
			public int[][] getGeometryMatrix() {
				return geometry;
			}

		});

		registerTileType("menuBlue", new MatrixTileType() {
			private int[][] geometry = { { 1, 1, 1 } };

			@Override
			public void init(Tile tile) {
				tile.getData().put("color", "#0000FF");
			}

			@Override
			public int[][] getGeometryMatrix() {
				return geometry;
			}
		});

		registerTileType("menuRed", new MatrixTileType() {
			private int[][] geometry = { { 1, 1, 1 } };

			@Override
			public void init(Tile tile) {
				tile.getData().put("color", "#FF0000");
			}

			@Override
			public int[][] getGeometryMatrix() {
				return geometry;
			}
		});

		registerTileType("pixelPURPLE", new TileType() {
			@Override
			public void init(Tile tile) {
				tile.getData().put("color", "#FF00FF");
			}

			@Override
			public boolean fieldOccupiedRelative(Tile tile, int cx, int cy) {
				return cx == 0 && cy == 0;
			}
		});

		registerTileType("pixelYELLOW", new TileType() {
			@Override
			public void init(Tile tile) {
				tile.getData().put("color", "#00FFFF");
			}

			@Override
			public boolean fieldOccupiedRelative(Tile tile, int cx, int cy) {
				return cx == 0 && cy == 0;
			}
		});

		registerTileType("pixelGREEN", new TileType() {
			@Override
			public void init(Tile tile) {
				tile.getData().put("color", "#00FF00");
			}

			@Override
			public boolean fieldOccupiedRelative(Tile tile, int cx, int cy) {
				return cx == 0 && cy == 0;
			}
		});

		registerTileType("exitSign", new MatrixTileType() {
			private int[][] geometry = { { 1, 0, 1 }, { 0, 1, 0 }, { 1, 0, 1 } };

			@Override
			public void init(Tile tile) {
				tile.getData().put("color", "#FF0000");
			}

			@Override
			public int[][] getGeometryMatrix() {
				return geometry;
			}
		});
		registerTileType("pixelBLUE", new TileType() {
			@Override
			public void init(Tile tile) {
				tile.getData().put("color", "#0000FF");
			}

			@Override
			public boolean fieldOccupiedRelative(Tile tile, int cx, int cy) {
				return cx == 0 && cy == 0;
			}
		});

		registerTileType("pixelRED", new TileType() {
			@Override
			public void init(Tile tile) {
				tile.getData().put("color", "#FF0000");
			}

			@Override
			public boolean fieldOccupiedRelative(Tile tile, int cx, int cy) {
				return cx == 0 && cy == 0;
			}
		});

		registerLevelType("menu", new LevelType() {

			private Level wonLevel;

			@Override
			public void init() {
				wonLevel = new Level(this);
				wonLevel.setWidth(4);
				wonLevel.setHeight(13);

				// Schimmler Icon
				wonLevel.addTile("pixel_00", createTile("pixelPURPLE", 0, 0));
				wonLevel.addTile("pixel_10", createTile("pixelPURPLE", 1, 0));
				wonLevel.addTile("pixel_20", createTile("pixelYELLOW", 2, 0));
				wonLevel.addTile("pixel_30", createTile("pixelYELLOW", 3, 0));

				wonLevel.addTile("pixel_01", createTile("pixelPURPLE", 0, 1));
				wonLevel.addTile("pixel_11", createTile("pixelBLUE", 1, 1));
				wonLevel.addTile("pixel_21", createTile("pixelBLUE", 2, 1));
				wonLevel.addTile("pixel_31", createTile("pixelYELLOW", 3, 1));

				wonLevel.addTile("pixel_02", createTile("pixelRED", 0, 2));
				wonLevel.addTile("pixel_12", createTile("pixelBLUE", 1, 2));
				wonLevel.addTile("pixel_22", createTile("pixelBLUE", 2, 2));
				wonLevel.addTile("pixel_32", createTile("pixelGREEN", 3, 2));

				wonLevel.addTile("pixel_03", createTile("pixelRED", 0, 3));
				wonLevel.addTile("pixel_13", createTile("pixelRED", 1, 3));
				wonLevel.addTile("pixel_23", createTile("pixelGREEN", 2, 3));
				wonLevel.addTile("pixel_33", createTile("pixelGREEN", 3, 3));

				// Menu
				wonLevel.addTile("pixel_07", createTile("pixelBLUE", 0, 6));
				wonLevel.addTile("pixel_37", createTile("pixelBLUE", 3, 6));

				wonLevel.addTile("pixel_08", createTile("pixelBLUE", 0, 7));
				wonLevel.addTile("pixel_38", createTile("pixelBLUE", 3, 7));

				// Exit
				wonLevel.addTile("exitSign", createTile("exitSign", 0, 9));

				// wonLevel.addTile("pixel_010", createTile("pixelRED", 0, 9));
				// wonLevel.addTile("pixel_310", createTile("pixelRED", 3, 9));

				// wonLevel.addTile("pixel_111", createTile("pixelRED", 1, 10));
				// wonLevel.addTile("pixel_211", createTile("pixelRED", 2, 10));
				// wonLevel.addTile("pixel_112", createTile("pixelRED", 1, 11));
				// wonLevel.addTile("pixel_212", createTile("pixelRED", 2, 11));

				// wonLevel.addTile("pixel_013", createTile("pixelRED", 0, 12));
				// wonLevel.addTile("pixel_313", createTile("pixelRED", 3, 12));
			}

			@Override
			public void init(Model m, Level level) {
				level.setWidth(4);
				level.setHeight(6);
				level.addTile("startGame", createTile("menuGreen", 0, 1));
				level.addTile("openMenu", createTile("menuBlue", 0, 3));
				level.addTile("exit", createTile("menuRed", 0, 5));
			}

			@Override
			public boolean canTileMoveTo(Level level, String name, int x, int y) {
				if (y != level.getTile(name).getY())
					return false;
				return super.canTileMoveTo(level, name, x, y);
			}

			@Override
			public void update(Model model, Level level) {

				if (level.getTile("startGame").getX() != 0) {
					model.setLevel(model.createLevel("schimmler"));
					View.update(model);
					return;
				}

				if (level.getTile("openMenu").getX() != 0) {
					model.setLevel(model.createLevel("levelSelect"));
					View.update(model);
					return;
				}

				if (level.getTile("exit").getX() != 0) {
					Plugin.kill(model);
					System.exit(0);
				}

			}

			@Override
			public boolean won(Level level) {
				return false;
			}

			@Override
			public Level getWonLevel() {
				return wonLevel;
			}

		});
	}

	private void registerSchimmler() {
		registerTileType("square", new MatrixTileType() {
			private int[][] geometry = { { 1, 1 }, { 1, 1 } };

			@Override
			public int[][] getGeometryMatrix() {
				return geometry;
			}
		});
		registerTileType("hookUL", new MatrixTileType() {
			private int[][] geometry = { { 1, 1 }, { 1, 0 } };

			@Override
			public int[][] getGeometryMatrix() {
				return geometry;
			}
		});

		registerTileType("hookUR", new MatrixTileType() {
			private int[][] geometry = { { 1, 1 }, { 0, 1 } };

			@Override
			public int[][] getGeometryMatrix() {
				return geometry;
			}
		});

		registerTileType("hookBL", new MatrixTileType() {
			private int[][] geometry = { { 1, 0 }, { 1, 1 } };

			@Override
			public int[][] getGeometryMatrix() {
				return geometry;
			}
		});

		registerTileType("hookBR", new MatrixTileType() {
			private int[][] geometry = { { 0, 1 }, { 1, 1 } };

			@Override
			public int[][] getGeometryMatrix() {
				return geometry;
			}
		});

		registerLevelType("schimmler", new LevelType() {

			private Level wonLevel;

			@Override
			public void init() {
				wonLevel = new Level(this);
				wonLevel.setWidth(4);
				wonLevel.setHeight(6);
				wonLevel.addTile("square", createTile("square", 1, 4));
				wonLevel.addTile("hookUL", createTile("hookUL", 0, 0));
				wonLevel.addTile("hookUR", createTile("hookUR", 2, 0));
				wonLevel.addTile("hookBL", createTile("hookBL", 0, 2));
				wonLevel.addTile("hookBR", createTile("hookBR", 2, 2));
			}

			@Override
			public void init(Model m, Level level) {
				level.setWidth(4);
				level.setHeight(6);
				level.addTile("square", createTile("square", 1, 1));
				level.addTile("hookUL", createTile("hookUL", 0, 0));
				level.addTile("hookUR", createTile("hookUR", 2, 0));
				level.addTile("hookBL", createTile("hookBL", 0, 2));
				level.addTile("hookBR", createTile("hookBR", 2, 2));
			}

			public Level getWonLevel() {
				return wonLevel;
			}

		});

		registerLevelType("levelSelect", new LevelType() {

			private Level wonLevel;

			@Override
			public void init() {
				wonLevel = new Level(this);
				wonLevel.setWidth(4);
				wonLevel.setHeight(6);
				wonLevel.addTile("square4", createTile("square", 0, 0));
				wonLevel.addTile("square2", createTile("square", 2, 4));
				wonLevel.addTile("square3", createTile("square", 2, 2));
			}

			@Override
			public void init(Model m, Level level) {
				level.setWidth(4);
				level.setHeight(6);
				level.addTile("square4", createTile("square", 0, 0));
				level.addTile("square2", createTile("square", 2, 2));
				level.addTile("square3", createTile("square", 2, 4));
			}

			public Level getWonLevel() {
				return wonLevel;
			}

		});
	}

	public static void loadPlugins(Model model, String[] args) {
		String folderName = "plugins";
		if (args.length >= 1)
			folderName = args[0];

		String path = System.getProperty("user.dir") + File.separatorChar + folderName + File.separatorChar;

		System.out.println(path);

		model.loadPlugins(new JavaScriptPluginLoader(new File(path)));
		model.loadPlugins(new JarFilePluginLoader(new File(path)));
	}

	public static void main(String[] args) {
		SchimmlerModel model = new SchimmlerModel();
		// loadPlugins(model, args);
		model.registerPlugin(new SchimmlerView());
		model.registerPlugin(new SchimmlerController());
		model.registerPlugin(new TestController());
		model.registerPlugin(new TestFilter());
		model.registerPlugin(new LightHousePlugin());
		model.registerPlugin(new LightHouseOnlinePlugin());
		model.loadPlugins(new JavaScriptPluginLoader(new File(System.getProperty("user.dir") + "/plugins/")));
		model.loadPlugins(new JarFilePluginLoader(new File(System.getProperty("user.dir") + "/plugins/")));

		model.start();
	}

}
