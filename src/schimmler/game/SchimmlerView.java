package schimmler.game;

import java.util.HashMap;
import java.util.Map;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import schimmler.architecture.Model;
import schimmler.architecture.plugin.GraphicalView;
import schimmler.architecture.plugin.InputPlugin;
import schimmler.architecture.plugin.View;

//fiddle for aminations: http://jsfiddle.net/q5Lc4fv2/38/
//http://jsfiddle.net/90ze2yjh/1/
@SuppressWarnings("serial")
public class SchimmlerView extends JFrame implements GraphicalView, InputPlugin { // changed to JFrame to properly be
																					// able to add a keylistener to it

	private static final int SUBPIXEL_COUNT = 20;
	private static final int WIDTH = 28 * SUBPIXEL_COUNT;
	private static final int HEIGHT = 14 * 2 * SUBPIXEL_COUNT;

	private static final int TILE_WIDTH = 4 * SUBPIXEL_COUNT;
	private static final int TILE_HEIGHT = 4 * SUBPIXEL_COUNT;

	private static final int OFFSET_X = 1 * SUBPIXEL_COUNT;
	private static final int OFFSET_Y = 2 * SUBPIXEL_COUNT;

	private BufferedImage currentFrame = null;

	public SchimmlerView() {
		super();
	}

	/**
	 * The offset the currently selected tile has been moved by.
	 */
	protected int[] selectedOffset = new int[] { 0, 0 };

	@Override
	public void init(Model m) {
		setSize(WIDTH, HEIGHT + getInsets().top - 2);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		View.update(m);
	}

	@Override
	public void paint(Graphics g) { // returned to paint because still double buffering and JFrame does not have a
									// paintComponent method
		if (currentFrame != null)
			g.drawImage(currentFrame, 0, 0, this);
	}

	@Override
	public String getName() {
		return "SchimmlerView";
	}

	@Override
	public void onUpdate(Model m) {
		currentFrame = render(m);

		this.repaint();

	}

	// render the current frame
	private BufferedImage render(Model m) {
		BufferedImage frame = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		// if theres no level render nothing
		if (m.getLevel() == null)
			return frame;

		// map tiles to colors, so every tile has a different one
		HashMap<String, Color> colorMap = new HashMap<String, Color>();
		int i = '0';
		for (String name : m.getLevel().getTileMap().keySet()) {
			Map<String, String> tileData = m.getLevel().getTile(name).getData();
			if (tileData.containsKey("color")) {
				colorMap.put(name, Color.decode(tileData.get("color")));
			} else {
				colorMap.put(name, new Color(Color.HSBtoRGB(0.2f * i++, 1, 1f)));
			}
		}

		// fill in playGround
		fillGround(m, Color.getHSBColor(0, 0, 0.2f), frame);
		drawMenu(m, Color.gray, frame);

		// render static tiles (those whuch are not selected)
		for (String tile : m.getLevel().getTileMap().keySet()) {
			Color color = colorMap.get(tile);
			if (m.getLevel().getSelected() != null && m.getLevel().getSelected().equals(tile))
				continue;
			drawTile(m, tile, color, frame);
		}

		colorMap = new HashMap<String, Color>();
		i = '0';
		for (String name : m.getLevel().getType().getWonLevel().getTileMap().keySet()) {
			Map<String, String> tileData = m.getLevel().getType().getWonLevel().getTile(name).getData();
			if (tileData.containsKey("color")) {
				colorMap.put(name, Color.decode(tileData.get("color")));
			} else {
				colorMap.put(name, new Color(Color.HSBtoRGB(0.2f * i++, 1, 1f)));
			}
		}

		if (m.getLevel().getType().getWonLevel() != null)
			for (String tile : m.getLevel().getType().getWonLevel().getTileMap().keySet()) {
				if (m.getLevel().getType().getWonLevel().getSelected() != null
						&& m.getLevel().getType().getWonLevel().getSelected().equals(tile))
					continue;
				if (m.getLevel().getType().getWonLevel().getTile(tile).getData().get("color") != null)
					drawTileSmall(m, tile, colorMap.get(tile), frame);
				else
					drawTileSmall(m, tile,
							Color.decode(m.getLevel().getType().getWonLevel().getTile(tile).getData().get("color")),
							frame);
			}

		// draw selected tile
		String sel = m.getLevel().getSelected();
		if (sel != null) {
			Color color = colorMap.get(sel).darker().darker();
			drawTile(m, sel, color, frame, selectedOffset[0], selectedOffset[1]);
		}

		// <GraphicalFilterCode>
		GraphicalView.filter(m, this, ((DataBufferInt) frame.getRaster().getDataBuffer()).getData(), frame.getWidth(),
				frame.getHeight());
		// </GraphicalFilterCode>

		return frame;

	}

	private void drawTile(Model m, String tile, Color color, BufferedImage img) {
		drawTile(m, tile, color, img, 0, 0);
	}

	private void drawTile(Model m, String tile, Color color, BufferedImage img, int offsetX, int offsetY) {
		Graphics2D g2d = img.createGraphics();
		for (int column = 0; column < m.getLevel().getWidth(); column++)
			for (int row = 0; row < m.getLevel().getHeight(); row++) {
				String occp = m.getLevel().fieldOccupied(column, row);
				if (occp != null && occp.equals(tile)) {
					g2d.setColor(color);
					g2d.fillRect(column * TILE_WIDTH + OFFSET_X + offsetX, row * TILE_HEIGHT + OFFSET_Y + offsetY,
							TILE_WIDTH, TILE_HEIGHT);
				}
			}
	}

	private void drawTileSmall(Model m, String tile, Color color, BufferedImage img) {
		Graphics2D g2d = img.createGraphics();
		for (int column = 0; column < m.getLevel().getWidth(); column++)
			for (int row = 0; row < m.getLevel().getHeight(); row++) {
				String occp = m.getLevel().fieldOccupied(column, row);
				if (occp != null && occp.equals(tile)) {
					g2d.setColor(color);
					g2d.fillRect(
							(int) ((column + 0.5) * SUBPIXEL_COUNT * 2 + 2 * OFFSET_X
									+ m.getLevel().getWidth() * TILE_WIDTH),
							(row + 1) * SUBPIXEL_COUNT * 2, SUBPIXEL_COUNT * 2, SUBPIXEL_COUNT * 2);
				}
			}
	}

	private void fillGround(Model m, Color color, BufferedImage img) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(color);
		g2d.fillRect(OFFSET_X, OFFSET_Y, TILE_WIDTH * m.getLevel().getWidth(), TILE_HEIGHT * m.getLevel().getHeight());
	}

	private void drawMenu(Model m, Color color, BufferedImage img) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(color);
		g2d.fillRect(2 * OFFSET_X + TILE_WIDTH * m.getLevel().getWidth(), 0,
				WIDTH - TILE_WIDTH * m.getLevel().getWidth() - 2 * OFFSET_X, HEIGHT);
	}

	@Override
	public void onWon(Model m) {
		// this.onUpdate(m);
		log("we WON!:D");
	}

	@Override
	public int[] getLvlCoordAt(int x, int y) {
		int relX = (x - OFFSET_X) / TILE_WIDTH;
		int relY = (y - OFFSET_Y) / TILE_HEIGHT;
		return new int[] { relX, relY };
	}

	@Override
	public int[] getPixelsPerTile() {
		return new int[] { TILE_WIDTH, TILE_HEIGHT };
	}

	/**
	 * @param selectedOffset the offset of the selected tile to set
	 */
	public void setSelectedOffset(int x, int y) {
		this.selectedOffset = new int[] { x, y };
	}

	@Override
	public int[] getSelectedOffset() {
		return this.selectedOffset;
	}

	@Override
	public int getPlaygroundWidth(Model m) {
		return 2 * OFFSET_X + TILE_WIDTH * m.getLevel().getWidth();
	}

	@Override
	public int[] getCoordAtLvl(int row, int column) {
		int x = column * TILE_WIDTH + OFFSET_X;
		int y = row * TILE_HEIGHT + OFFSET_Y;
		return new int[] { x, y };
	}

}
