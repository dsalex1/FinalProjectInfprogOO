package schimmler.game;

import schimmler.architecture.Level;
import schimmler.architecture.Model;
import schimmler.test.TestController;

public class SchimmlerModel extends Model {

	public SchimmlerModel() {
		super();
	}

	@Override
	public void init() {
		setLevel(new Level() {
			@Override
			public void init() {
				this.width = 4;
				this.height = 6;
				this.addTile("square", new MatrixTile(1, 1) {
					@Override
					public int[][] getGeometryMatrix() {
						int[][] geometry = { { 1, 1 }, { 1, 1 } };
						return geometry;
					}
				});
				this.addTile("hookUL", new MatrixTile(0, 0) {
					@Override
					public int[][] getGeometryMatrix() {
						int[][] geometry = { { 1, 1 }, { 1, 0 } };
						return geometry;
					}
				});
				this.addTile("hookUR", new MatrixTile(2, 0) {
					@Override
					public int[][] getGeometryMatrix() {
						int[][] geometry = { { 1, 1 }, { 0, 1 } };
						return geometry;
					}
				});
				this.addTile("hookBL", new MatrixTile(0, 2) {
					@Override
					public int[][] getGeometryMatrix() {
						int[][] geometry = { { 1, 0 }, { 1, 1 } };
						return geometry;
					}
				});
				this.addTile("hookBR", new MatrixTile(2, 2) {
					@Override
					public int[][] getGeometryMatrix() {
						int[][] geometry = { { 0, 1 }, { 1, 1 } };
						return geometry;
					}
				});
			}

			@Override
			public boolean won() {
				return this.getTile("square").getX() == 1 || this.getTile("square").getY() == 4;
			}
		});

	}

	public static void main(String[] args) {
		SchimmlerModel model = new SchimmlerModel();
		model.registerPlugin(new SchimmlerView());
		model.registerPlugin(new TestController());
		System.out.println(model.getPlugins());
		model.start();
	}

}
