package schimmler.game;

import java.util.Map.Entry;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JPanel;

import schimmler.architecture.GraphicalView;
import schimmler.architecture.InputPlugin;
import schimmler.architecture.Model;
import schimmler.architecture.Plugin;
import schimmler.architecture.Tile;

public class SchimmlerController implements Plugin {

	private Model model;
	private Thread thread;
	private int[] dragStart;

	@Override
	public void init(Model m) {
		this.model = m;

		// search for plugins which can have mouse listeners, here explicitly the
		// SchimmlerView, TODO: add litener to all plugins appliciable
		List<Plugin> plugins = model.getPlugins();
		for (Plugin plugin : plugins) {
			if (plugin instanceof GraphicalView && plugin instanceof JComponent) {
				((JComponent) plugin).addMouseListener(new MouseAdapter() {

					@Override
					public void mouseReleased(MouseEvent e) {

						int[] dragEnd = ((GraphicalView) plugin).getLvlCoordAt(e.getX(), e.getY());

						String name = model.getLevel().getSelected();
						Tile tile = model.getLevel().getTile(name);
						int oX = tile.getX();
						int oY = tile.getY();
						int dX = dragEnd[0] - dragStart[0];
						int dY = dragEnd[1] - dragStart[1];

						if (model.getLevel().canTileMoveTo(name, oX + dX, oY + dY)) {

							tile.setX(oX + dX);
							tile.setY(oY + dY);
							InputPlugin.tileMoved(model, tile, name, oX, oY);
						}
						// and deselect the tile
						model.getLevel().setSelected(null);
						InputPlugin.tileDeselected(m, tile, name);
					}

					@Override
					public void mousePressed(MouseEvent e) {
						// find coordinates in the system of the model
						dragStart = ((GraphicalView) plugin).getLvlCoordAt(e.getX(), e.getY());
						// and get the corresponsing name of the tile
						String name = model.getLevel().fieldOccupied(dragStart[0], dragStart[1]);

						Tile tile = model.getLevel().getTile(name);

						model.getLevel().setSelected(name);
						InputPlugin.tileSelected(model, tile, name);
					}

					@Override
					public void mouseDragged(MouseEvent e) {

					}
				});
			}
		}
	}

	@Override
	public String getName() {
		return "Mouse Controller";
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onKill(Model m) {

	}

}
