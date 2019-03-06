package schimmler.game;

import java.util.HashMap;
import java.util.Map;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;
import javax.swing.JPanel;

import lightHouseSimulator.LightHouseSimulator;
import schimmler.architecture.InputPlugin;
import schimmler.architecture.Model;
import schimmler.architecture.GraphicalView;
import schimmler.architecture.View;

//fiddle for aminations: http://jsfiddle.net/q5Lc4fv2/38/
//http://jsfiddle.net/90ze2yjh/1/
@SuppressWarnings("serial")
public class SchimmlerView extends JPanel implements GraphicalView, InputPlugin {

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

	/**
	 * The offset the currently selected tile has been moved by.
	 */
	protected int[] selectedOffset = new int[] { 0, 0 };

	@Override
	public void init(Model m) {
		window = new JFrame();
		window.setSize(WIDTH, HEIGHT + window.getInsets().top - 2);
		window.setVisible(true);
		window.add(this);

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
		fillGround(m, Color.gray, frame);

		// render static tiles (those whuch are not selected)
		for (String tile : m.getLevel().getTileMap().keySet()) {
			Color color = colorMap.get(tile);
			if (m.getLevel().getSelected() != null && m.getLevel().getSelected().equals(tile))
				continue;
			drawTile(m, tile, color, frame);
		}

		// draw selected tile
		String sel = m.getLevel().getSelected();
		if (sel != null) {
			Color color = colorMap.get(sel).darker().darker();
			drawTile(m, sel, color, frame, selectedOffset[0], selectedOffset[1]);
		}
		
		
		// <GraphicalFilterCode>
		GraphicalView.filter(m, ((DataBufferInt)frame.getRaster().getDataBuffer()).getData(), frame.getWidth(), frame.getHeight());
		// </GraphicalFilterCode>
		
		return frame;

	}

	private void drawTile(Model m, String tile, Color color, BufferedImage img) {
		drawTile(m, tile, color, img, 0, 0);
	}

	private void drawTile(Model m, String tile, Color color, BufferedImage img, int offsetX, int offsetY) {
		Graphics2D g2d = img.createGraphics();
		for (int column = 0; column < m.getLevel().getWidth(); column++)
			for (int row = 0; row < m.getLevel().getHeight(); row++)
				if (m.getLevel().fieldOccupied(column, row) == tile) {
					g2d.setColor(color);
					g2d.fillRect(column * TILE_WIDTH + OFFSET_X + offsetX,
							row * TILE_HEIGHT + OFFSET_Y + offsetY, TILE_WIDTH, TILE_HEIGHT);
				}
	}

	private void fillGround(Model m, Color color, BufferedImage img) {
		Graphics2D g2d = img.createGraphics();
		g2d.setColor(color);
		g2d.fillRect(OFFSET_X, OFFSET_Y, TILE_WIDTH * m.getLevel().getWidth(), TILE_HEIGHT * m.getLevel().getHeight());
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (currentFrame != null)
			g.drawImage(currentFrame, 0, 0, this);
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
}
