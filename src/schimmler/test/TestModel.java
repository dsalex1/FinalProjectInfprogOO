package schimmler.test;

import schimmler.architecture.Level;
import schimmler.architecture.Model;
import schimmler.architecture.Tile;

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
				this.addTile("square", new Tile(1, 1) {
					@Override
					public boolean fieldOccupied(int cx, int cy) {
						return cx == x && cy == y;
					}
				});
				this.addTile("hook", new Tile(0, 0) {
					@Override
					public boolean fieldOccupied(int cx, int cy) {
						return (cx == x && cy == y) || (cx == x + 1 && cy == y) || (cx == x && cy == y + 1);
					}
				});
			}

			@Override
			public boolean won() {
				return this.getTile("square").getX() != 1 || this.getTile("square").getY() != 1;
			}
		});

		registerPlugin(new TestView());
	}

	public static void main(String[] args) {
		TestModel model = new TestModel();
		System.out.println(model.getPlugins());
	}

}
