package schimmler.game;

import java.io.File;

import lightHouseSimulator.LightHouseOnlinePlugin;
import lightHouseSimulator.LightHousePlugin;
import schimmler.architecture.Level;
import schimmler.architecture.LevelType;
import schimmler.architecture.MatrixTileType;
import schimmler.architecture.Model;
import schimmler.architecture.Tile;
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

		registerLevelType("menu", new LevelType() {
			@Override
			public void init(Model m, Level level) {
				level.setWidth(4);
				level.setHeight(7);
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
					// model.setLevel(model.createLevel("levelSelect"));
					// View.update(model);
					// return;
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

			@Override
			public boolean won(Level level) {
				return false && level.getTile("square").getX() == 0 && level.getTile("square").getY() == 2;
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
		model.start();
	}

}
