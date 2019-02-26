package schimmler.game;

import java.util.HashMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

import lightHouseSimulator.LightHouseSimulator;
import schimmler.architecture.Model;
import schimmler.architecture.Tile;
import schimmler.architecture.View;

@SuppressWarnings("serial")
public class SchimmlerView extends Canvas implements View {

	private static final Boolean SHOW_LIGHTHOUSE = true;
	private static final int SUBPIXEL_COUNT = 20;
	private static final int WIDTH = 28 * SUBPIXEL_COUNT;
	private static final int HEIGHT = 14 * 2 * SUBPIXEL_COUNT;

	private static final int TILE_WIDTH = 4 * SUBPIXEL_COUNT;
	private static final int TILE_HEIGHT = 4 * SUBPIXEL_COUNT;

	private static final int OFFSET_X = 2 * SUBPIXEL_COUNT;
	private static final int OFFSET_Y = 2 * SUBPIXEL_COUNT;

	private BufferedImage currentFrame = null;

	private JFrame window = null;

	private LightHouseSimulator lightHouse;

	public SchimmlerView() {
		super();
	}

	@Override
	public void init(Model m) {
		window = new JFrame();
		window.add(this);
		window.setVisible(true);
		window.setSize(WIDTH, HEIGHT + window.getInsets().top - 2);

		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		if (SHOW_LIGHTHOUSE) {
			lightHouse = new LightHouseSimulator();
			lightHouse.setGapsRatio(1, 0);
		}

		View.update(m);
	}

	@Override
	public String getName() {
		return "SchimmlerView";
	}

	@Override
	public void onUpdate(Model m) {
		currentFrame = render(m);

		if (SHOW_LIGHTHOUSE) {
			lightHouse.setData(currentFrame);
		}

		this.repaint();

	}

	// render the current frame
	private BufferedImage render(Model m) {
		BufferedImage frame = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		if (m.getLevel() == null)
			return frame;

		HashMap<String, Color> map = new HashMap<String, Color>();
		int i = '0';
		for (String s : m.getLevel().getTileMap().keySet())
			map.put(s, new Color(Color.HSBtoRGB(0.2f * i++, 1, 1f)));
		map.put(null, Color.GRAY);

		for (int column = 0; column < m.getLevel().getWidth(); column++)
			for (int row = 0; row < m.getLevel().getHeight(); row++)
				drawTile(column, row, map.get(m.getLevel().fieldOccupied(column, row)), frame);

		return frame;
	}

	private void drawTile(int column, int row, Color color, BufferedImage img) {
		for (int x = 0; x < TILE_WIDTH; x++)
			for (int y = 0; y < TILE_HEIGHT; y++)
				img.setRGB(column * TILE_WIDTH + OFFSET_X + x, row * TILE_HEIGHT + OFFSET_Y + y, color.getRGB());
	}

	@Override
	public void paint(Graphics g) {
		if (currentFrame != null)
			g.drawImage(currentFrame, 0, 0, this);
	}

	@Override
	public void onCursorMove(Model m, int x, int y) {
		View.update(m);
	}

	@Override
	public void onTileSelected(Model m, Tile tile, String id) {
		View.update(m);
	}

	@Override
	public void onTileDeselected(Model m, Tile tile, String id) {
		View.update(m);
	}

	@Override
	public void onTileMoved(Model m, Tile tile, String id, int oldx, int oldy) {
		View.update(m);
	}
}
