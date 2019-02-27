package schimmler.test;

import java.io.File;

import schimmler.architecture.Level;
import schimmler.architecture.Model;
import schimmler.architecture.Tile;
import schimmler.js.JavaScriptPluginLoader;

public class TestModel extends Model {

	public TestModel() {
		super();
	}

	@Override
	public void init() {
		setLevel(new Level() {
			@Override
			public void init() {
				this.width = 3;
				this.height = 3;
				this.addTile("square", new Tile(2, 2) {
					@Override
					public boolean fieldOccupiedRelative(int cx, int cy) {
						return cx == 0 && cy == 0;
					}
				});
				this.addTile("hook", new Tile(0, 1) {
					@Override
					public boolean fieldOccupiedRelative(int cx, int cy) {
						return (cx == 0 && cy == 0) || (cx == 1 && cy == 0) || (cx == 0 && cy == 1);
					}
				});
			}

			@Override
			public boolean won() {
				return this.getTile("square").getX() != 1 || this.getTile("square").getY() != 1;
			}
		});
	}

	public static void main(String[] args) {
		TestModel model = new TestModel();
		model.registerPlugin(new TestView());
		model.registerPlugin(new TestController());
		model.loadPlugins(new JavaScriptPluginLoader(new File(System.getProperty("user.dir")+"/plugins/")));
		System.out.println(model.getPlugins());
	}

}
