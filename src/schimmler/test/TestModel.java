package schimmler.test;

import java.io.File;

import schimmler.architecture.Level;
import schimmler.architecture.LevelType;
import schimmler.architecture.Model;
import schimmler.architecture.Tile;
import schimmler.architecture.TileType;
import schimmler.jar.JarFilePluginLoader;
import schimmler.js.JavaScriptPluginLoader;

public class TestModel extends Model {

	public TestModel() {
		super();
	}

	@Override
	public void init() {
		
		
		registerTileType("square", new TileType() {
			@Override
			public boolean fieldOccupiedRelative(Tile tile, int cx, int cy) {
				return cx == 0 && cy == 0;
			}
		});
		registerTileType("hook", new TileType() {
			@Override
			public boolean fieldOccupiedRelative(Tile tile, int cx, int cy) {
				return (cx == 0 && cy == 0) || (cx == 1 && cy == 0) || (cx == 0 && cy == 1);
			}
		});
		
		registerLevelType("test", new LevelType() {
			@Override
			public void init(Level level) {
				level.setWidth(3);
				level.setHeight(3);
				level.addTile("square", createTile("square", 2, 1));
				level.addTile("hook", createTile("hook", 0, 0));
			}

			@Override
			public boolean won(Level level) {
				return level.getTile("square").getX() != 1 || level.getTile("square").getY() != 1;
			}

		});
		
		setLevel(createLevel("test"));
	}

	public static void main(String[] args) {
		TestModel model = new TestModel();
		model.registerPlugin(new TestView());
		model.registerPlugin(new TestController());
		model.loadPlugins(new JavaScriptPluginLoader(new File(System.getProperty("user.dir")+"/plugins/")));
		model.loadPlugins(new JarFilePluginLoader(new File(System.getProperty("user.dir")+"/plugins/")));
		System.out.println(model.getPlugins());
		model.start();
	}

}
