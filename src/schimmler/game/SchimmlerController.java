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
						log("release: " + e.getX() + " " + e.getY());

						model.getLevel().setSelected(null);
						String name = model.getLevel().getSelected();
						Tile tile = model.getLevel().getTile(name);
						InputPlugin.tileDeselected(m, tile, name);
					}

					@Override
					public void mousePressed(MouseEvent e) {
						log("press: " + e.getX() + " " + e.getY());
						// find coordinates in the system of the model
						int[] coords = ((GraphicalView) plugin).getLvlCoordAt(e.getX(), e.getY());
						// and get the corresponsing name of the tile
						String name = model.getLevel().fieldOccupied(coords[0], coords[1]);

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
