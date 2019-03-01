package schimmler.game;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JComponent;
import schimmler.architecture.GraphicalView;
import schimmler.architecture.InputPlugin;
import schimmler.architecture.Model;
import schimmler.architecture.Plugin;
import schimmler.architecture.Tile;

public class SchimmlerController implements Plugin {

	@Override
	public void init(Model m) {

	}

	@Override
	public void onPluginsLoaded(Model model) {
		// search for plugins which can have mouse listeners, here explicitly the
		List<Plugin> plugins = model.getPlugins();
		for (Plugin plugin : plugins) {
			if (plugin instanceof GraphicalView && plugin instanceof JComponent) {
				MouseAdapter listener = new MouseAdapter() {

					private int[] dragStart;

					@Override
					public void mouseReleased(MouseEvent e) {

						int[] dragEnd = ((GraphicalView) plugin).getLvlCoordAt(e.getX(), e.getY());

						String name = model.getLevel().getSelected();

						if (name == null)
							return;

						Tile tile = model.getLevel().getTile(name);

						if (tile == null)
							return;

						int[] deltas = getClosestViableDelta(name, dragEnd[0], dragEnd[1]);
						if (deltas != null) {
							tile.setX(tile.getX() + deltas[0]);
							tile.setY(tile.getY() + deltas[1]);
							InputPlugin.tileMoved(model, tile, name, tile.getX(), tile.getY());
						}

						// and deselect the tile
						model.getLevel().setSelected(null);
						InputPlugin.tileDeselected(model, tile, name);
					}

					@Override
					public void mousePressed(MouseEvent e) {
						// find coordinates in the system of the model
						dragStart = ((GraphicalView) plugin).getLvlCoordAt(e.getX(), e.getY());
						// and get the corresponsing name of the tile
						String name = model.getLevel().fieldOccupied(dragStart[0], dragStart[1]);

						if (name == null)
							return;

						Tile tile = model.getLevel().getTile(name);

						if (tile == null)
							return;

						model.getLevel().setSelected(name);
						InputPlugin.tileSelected(model, tile, name);
					}

					@Override
					public void mouseDragged(MouseEvent e) {
						log("test");
						int[] dragEnd = ((GraphicalView) plugin).getLvlCoordAt(e.getX(), e.getY());

						String name = model.getLevel().getSelected();

						if (name == null)
							return;

						Tile tile = model.getLevel().getTile(name);

						if (tile == null)
							return;

						int[] deltas = getClosestViableDelta(name, dragEnd[0], dragEnd[1]);
						if (deltas != null) {
							// TODO: dont really set it to the place
							tile.setX(tile.getX() + deltas[0]);
							tile.setY(tile.getY() + deltas[1]);
							dragStart[0] += deltas[0];
							dragStart[1] += deltas[1];
							InputPlugin.tileMoved(model, tile, name, tile.getX(), tile.getY());
						}
					}

					private int[] getClosestViableDelta(String name, int endX, int endY) {
						Tile tile = model.getLevel().getTile(name);

						if (tile == null)
							return null;

						int oX = tile.getX();
						int oY = tile.getY();
						int dX = endX - dragStart[0];
						int dY = endY - dragStart[1];

						// autocorrect unprecise userInput

						// if slightly not straight
						if (Math.abs(dX) > Math.abs(dY))
							dY = 0;
						if (Math.abs(dY) > Math.abs(dX))
							dX = 0;

						while (dY != 0 || dX != 0) {
							// try at current delta
							if (model.getLevel().canTileMoveTo(name, oX + dX, oY + dY)) {
								return new int[] { dX, dY };
							}
							// if not possible try one closer
							if (dX != 0) {
								dX -= Math.signum(dX);
							}
							if (dY != 0) {
								dY -= Math.signum(dY);
							}
						}
						return null;

					}
				};
				((JComponent) plugin).addMouseListener(listener);
				((JComponent) plugin).addMouseMotionListener(listener);
			}
		}
	}

	@Override
	public String getName() {
		return "SchimmlerController";
	}

}
