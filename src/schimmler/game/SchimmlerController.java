package schimmler.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;
import javax.swing.JFrame;

import schimmler.architecture.Model;
import schimmler.architecture.Tile;
import schimmler.architecture.plugin.GraphicalView;
import schimmler.architecture.plugin.InputPlugin;
import schimmler.architecture.plugin.KeyboardInputPlugin;
import schimmler.architecture.plugin.MouseInputPlugin;
import schimmler.architecture.plugin.Plugin;

public class SchimmlerController implements Plugin {

	@Override
	public void init(Model m) {

	}

	@Override
	public void onPluginsLoaded(Model model) {
		// search for plugins which can have mouse listeners and are pixel based views
		List<Plugin> plugins = model.getPlugins();
		for (Plugin plugin : plugins) {
			if (plugin instanceof GraphicalView && plugin instanceof JFrame) { 
				SchimmlerControllerListener scl = new SchimmlerControllerListener(model, plugin);
				((JFrame) plugin).addMouseListener(scl);
				((JFrame) plugin).addMouseMotionListener(scl);
				((JFrame) plugin).addKeyListener(scl);
			}
		}
	}

	@Override
	public String getName() {
		return "SchimmlerController";
	}
	
	private class SchimmlerControllerListener implements MouseListener, MouseMotionListener, KeyListener {

		private int[] dragStart;
		private int[] dragMouseStart;
		private Model model;
		private Plugin plugin;
		
		public SchimmlerControllerListener(Model model, Plugin plugin) {
			this.model = model;
			this.plugin = plugin;
		}

		@Override
		public void mouseReleased(MouseEvent e) {

			//int[] dragEnd = ((GraphicalView) plugin).getLvlCoordAt(e.getX(), e.getY()); // not used anywhere, commented out to remove warning and to annotate it not being used anywhere

			String name = model.getLevel().getSelected();

			if (name == null)
				return;

			Tile tile = model.getLevel().getTile(name);

			if (tile == null)
				return;

			// proposed delta by mouse
			int[] mouseDeltas = new int[] { e.getX() - dragMouseStart[0], e.getY() - dragMouseStart[1] };

			// resict movement to one direction
			if (Math.abs(mouseDeltas[0]) > Math.abs(mouseDeltas[1]))
				mouseDeltas[1] = 0;
			else
				mouseDeltas[0] = 0;

			// tile we point to
			int[] dragTarget = ((GraphicalView) plugin).getLvlCoordAt(dragMouseStart[0] + mouseDeltas[0],
					dragMouseStart[1] + mouseDeltas[1]);
			// maximum delta we may go
			int[] movementDeltas = getClosestViableDelta(name, dragTarget[0], dragTarget[1]);

			// if we wouldnt move at all try again the other direction
			if (movementDeltas[0] == 0 && movementDeltas[1] == 0) {
				mouseDeltas = new int[] { e.getX() - dragMouseStart[0], e.getY() - dragMouseStart[1] };
				if (Math.abs(mouseDeltas[0]) > Math.abs(mouseDeltas[1]))
					mouseDeltas[0] = 0;
				else
					mouseDeltas[1] = 0;
				dragTarget = ((GraphicalView) plugin).getLvlCoordAt(dragMouseStart[0] + mouseDeltas[0],
						dragMouseStart[1] + mouseDeltas[1]);
				movementDeltas = getClosestViableDelta(name, dragTarget[0], dragTarget[1]);
			}

			if (movementDeltas != null) {
				model.setTilePosition(name, tile.getX() + movementDeltas[0],
						tile.getY() + movementDeltas[1]);
				InputPlugin.tileMoved(model, tile, name, tile.getX(), tile.getY());
			}

			// and deselect the tile
			model.getLevel().setSelected(null);
			InputPlugin.tileDeselected(model, tile, name);
			((GraphicalView) plugin).setSelectedOffset(0,0); // reset offset to not have it displayed for the next tile
			}

		@Override
		public void mousePressed(MouseEvent e) {
			
			((GraphicalView) plugin).setSelectedOffset(0,0); // reset offset to not have it displayed for the next tile
			
			// find coordinates in the system of the model
			dragStart = ((GraphicalView) plugin).getLvlCoordAt(e.getX(), e.getY());
			dragMouseStart = new int[] { e.getX(), e.getY() };
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
			String name = model.getLevel().getSelected();

			if (name == null)
				return;

			Tile tile = model.getLevel().getTile(name);

			if (tile == null)
				return;

			// proposed delta by mouse
			int[] mouseDeltas = new int[] { e.getX() - dragMouseStart[0], e.getY() - dragMouseStart[1] };

			// resict movement to one direction
			if (Math.abs(mouseDeltas[0]) > Math.abs(mouseDeltas[1]))
				mouseDeltas[1] = 0;
			else
				mouseDeltas[0] = 0;

			// tile we point to
			int[] dragTarget = ((GraphicalView) plugin).getLvlCoordAt(dragMouseStart[0] + mouseDeltas[0],
					dragMouseStart[1] + mouseDeltas[1]);
			dragTarget[0] += (int) Math.signum(mouseDeltas[0]);
			dragTarget[1] += (int) Math.signum(mouseDeltas[1]);

			// maximum delta we may go
			int[] movementDeltas = getClosestViableDelta(name, dragTarget[0], dragTarget[1]);

			// if we wouldnt move at all try again the other direction
			if (movementDeltas[0] == 0 && movementDeltas[1] == 0) {
				mouseDeltas = new int[] { e.getX() - dragMouseStart[0], e.getY() - dragMouseStart[1] };
				if (Math.abs(mouseDeltas[0]) > Math.abs(mouseDeltas[1]))
					mouseDeltas[0] = 0;
				else
					mouseDeltas[1] = 0;
				dragTarget = ((GraphicalView) plugin).getLvlCoordAt(dragMouseStart[0] + mouseDeltas[0],
						dragMouseStart[1] + mouseDeltas[1]);
				dragTarget[0] += (int) Math.signum(mouseDeltas[0]);
				dragTarget[1] += (int) Math.signum(mouseDeltas[1]);
				movementDeltas = getClosestViableDelta(name, dragTarget[0], dragTarget[1]);
			}

			// if we wanna go more than we may, restrict it
			int[] pxPerTile = ((GraphicalView) plugin).getPixelsPerTile();
			if (Math.abs(mouseDeltas[0]) > Math.abs(movementDeltas[0] * pxPerTile[0]))
				mouseDeltas[0] = movementDeltas[0] * pxPerTile[0];
			if (Math.abs(mouseDeltas[1]) > Math.abs(movementDeltas[1] * pxPerTile[1]))
				mouseDeltas[1] = movementDeltas[1] * pxPerTile[1];

			// finally set our result
			((GraphicalView) plugin).setSelectedOffset(mouseDeltas[0], mouseDeltas[1]);

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
			return new int[] { 0, 0 };

		}
		

		@Override
		public void mouseMoved(MouseEvent e) {
			MouseInputPlugin.cursorMove(model, e.getX(), e.getY());
		}
		

		@Override
		public void keyPressed(KeyEvent e) {
			KeyboardInputPlugin.keyPressed(model, Character.toLowerCase(e.getKeyChar()), e.isShiftDown());
		}

		@Override
		public void keyReleased(KeyEvent e) {
			KeyboardInputPlugin.keyReleased(model, Character.toLowerCase(e.getKeyChar()), e.isShiftDown());
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}


	};

}
