package schimmler.test;

import schimmler.architecture.Model;
import schimmler.architecture.plugin.GraphicalFilterPlugin;
import schimmler.architecture.plugin.GraphicalView;

public class TestFilter implements GraphicalFilterPlugin {

	int SPEED_FACTOR = 2;
	int ITERATIONS = 8;

	float SUSTAIN = 0.98f;

	int OVERHEAD_FLOW = 5;
	int FRAMES_TO_SHOW = 1;
	/*
	 * function getPixelXY(x, y) { return getPixel((y+60)*imgData.width+(x)); }
	 */

	private int addColors(int p1, int p2) {
		int r = ((p1 >> 16) & 0xFF) + ((p2 >> 16) & 0xFF);
		if (r > 255)
			r = 255;
		int g = ((p1 >> 8) & 0xFF) + ((p2 >> 8) & 0xFF);
		if (g > 255)
			g = 255;
		int b = (p1 & 0xFF) + (p2 & 0xFF);
		if (b > 255)
			b = 255;
		return r << 16 | g << 8 | b;
	}

	private float[][] velocities;
	private float[][] values;
	private int[][] blocked;
	private float[][] offset;

	private int width;
	private int height;

	private void initFields() {
		velocities = new float[width / SPEED_FACTOR][height / SPEED_FACTOR];
		values = new float[width / SPEED_FACTOR][height / SPEED_FACTOR];
		blocked = new int[width / SPEED_FACTOR][height / SPEED_FACTOR];
		offset = new float[width / SPEED_FACTOR][height / SPEED_FACTOR];
	}

	private void updatePixels(int[] img) {
		for (int x = 0; x < width / SPEED_FACTOR; x++) {
			for (int y = 0; y < height / SPEED_FACTOR; y++) {
				if (blocked[x][y] == 1) {
					//for (int i = 0; i < SPEED_FACTOR; i++)
					//	for (int j = 0; j < SPEED_FACTOR; j++)
					//		img[(x * SPEED_FACTOR + i) + (y * SPEED_FACTOR + j) * width] = 0xFFFFFF;
					continue;
				}
				
				/*
				 * Commented out because not used.
					float dX, dY;
					if (x < width / SPEED_FACTOR - 1)
						dX = values[x][y] - values[x + 1][y];
					else
						dX = 0;
					if (y < height / SPEED_FACTOR - 1)
						dY = values[x][y] - values[x][y + 1];
					else
						dY = 0;
				*/
				
				// bg=getPixelXY(x+parseInt(dX*20), y+parseInt(dY*20))
				if (values[x][y] >= 0) {
					int b = (int) (values[x][y] * 255);
					if (b > 255)
						b = 255;
					for (int i = 0; i < SPEED_FACTOR; i++)
						for (int j = 0; j < SPEED_FACTOR; j++)
							img[(x * SPEED_FACTOR + i) + (y * SPEED_FACTOR + j) * width] = addColors(
									img[(x * SPEED_FACTOR + i) + (y * SPEED_FACTOR + j) * width], b);
				} else {
					int b = (int) (values[x][y] * -255);
					if (b > 255)
						b = 255;
					for (int i = 0; i < SPEED_FACTOR; i++)
						for (int j = 0; j < SPEED_FACTOR; j++)
							img[(x * SPEED_FACTOR + i) + (y * SPEED_FACTOR + j) * width] = addColors(
									img[(x * SPEED_FACTOR + i) + (y * SPEED_FACTOR + j) * width], b);
				}
			}
		}
	}

	private void clear() {
		for (int x = 0; x < width / SPEED_FACTOR; x++)
			for (int y = 0; y < height / SPEED_FACTOR; y++) {
				offset[x][y] = 0;
				blocked[x][y] = 0;
			}
	}

	private float getGradient(int x, int y, int x2, int y2) {
		if (x < 0 || y < 0 || x >= width / SPEED_FACTOR || y >= height / SPEED_FACTOR)
			return 0;
		if (blocked[x][y] == 1)
			return 0;
		return values[x][y] - values[x2][y2];
	}

	private void updateVelocities() {
		for (int x = 0; x < width / SPEED_FACTOR; x++)
			for (int y = 0; y < height / SPEED_FACTOR; y++) {
				float v = (getGradient(x + 1, y, x, y) + getGradient(x, y + 1, x, y) + getGradient(x - 1, y, x, y)
						+ getGradient(x, y - 1, x, y)) / 4;
				velocities[x][y] -= 1.9 * v;
			}
	}

	private void updateValues() {
		for (int x = 0; x < width / SPEED_FACTOR; x++)
			for (int y = 0; y < height / SPEED_FACTOR; y++) {
				values[x][y] = values[x][y] * SUSTAIN - velocities[x][y] + offset[x][y];
				if (values[x][y] < -1)
					values[x][y] = -1;
				if (values[x][y] > 1)
					values[x][y] = 1;
				
				// This part makes the "fields" reset when they reach a specific velocity and value
				// This is needed to prevent idle tiles that (because of their low velocity) loose color way too slow (thus tint the whole screen blue)
				// These parameters need adjusting for more optimal results. 
				if(Math.abs(velocities[x][y]) != 0 && Math.abs(velocities[x][y]) < 0.005 && values[x][y] != 0.0 && Math.abs(values[x][y]) < 1.0) {
					values[x][y]=0;
					velocities[x][y]=0;
				}
				
			}
	}

	@Override
	public void init(Model m) {
	}

	@Override
	public String getName() {
		return "TestFilter";
	}

	@Override
	public void onlevelSet(Model m) {
		log("level set");
	}

	String lastSelected;
	int iterationsShown = 0;

	@Override
	public void onFilter(Model m, GraphicalView view, int[] img, int width, int height) {
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			initFields();
		}
		clear();
		String sel = m.getLevel().getSelected();
		iterationsShown++;
		if (sel != lastSelected) {
			iterationsShown = 0;
			lastSelected = sel;
		}
		for (int column = 0; column < m.getLevel().getWidth(); column++)
			for (int row = 0; row < m.getLevel().getHeight(); row++)
				if (m.getLevel().fieldOccupied(column, row) == sel && sel != null)
					for (int x = -OVERHEAD_FLOW; x < view.getPixelsPerTile()[0] + OVERHEAD_FLOW; x++)
						for (int y = -OVERHEAD_FLOW; y < view.getPixelsPerTile()[0] + OVERHEAD_FLOW; y++) {
							int curX = view.getCoordAtLvl(row, column)[0] + x + view.getSelectedOffset()[0];
							int curY = view.getCoordAtLvl(row, column)[1] + y + view.getSelectedOffset()[1];
							if (curX > 0 && curX < width && curY > 0 && curY < height)
								if (iterationsShown < FRAMES_TO_SHOW)
									offset[curX / SPEED_FACTOR][curY / SPEED_FACTOR] = 1;
							if (x >= 0 && x <= view.getPixelsPerTile()[0])
								if (y >= 0 && y <= view.getPixelsPerTile()[1])
									blocked[curX / SPEED_FACTOR][curY / SPEED_FACTOR] = 1;
						}
		for (int i = 0; i < ITERATIONS; i++) {
			updateVelocities();
			updateValues();
		}
		updatePixels(img);
	}

}
