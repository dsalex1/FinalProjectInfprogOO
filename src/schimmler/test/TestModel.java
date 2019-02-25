package schimmler.test;

import schimmler.Level;
import schimmler.Model;
import schimmler.Tile;

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
				this.addTile("square", new Tile(1,1) {
					@Override
					public boolean canMoveTo(Level level, int newX, int newY) {
						return level.inLevel(newX, newY);
					}
				});
			}
			@Override
			public boolean won() {
				return this.getTile("square").getX() != 1 || this.getTile("square").getY() != 1;
			}
		});
	}
	
}
