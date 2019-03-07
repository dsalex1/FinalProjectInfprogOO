package schimmler.architecture.plugin;

import schimmler.architecture.Model;
import schimmler.architecture.Tile;

/**
 * An interface for plugins that want to listen to filtered user input related to interaction with the model.
 */
public interface InputPlugin extends Plugin {


	/**
	 * Event for the selection of a tile in a level. Tiles need to be selected
	 * before they can be moved. Only one tile can be selected at once.
	 * 
	 * @param m    the model this event is called from
	 * @param tile the tile selected
	 * @param id   the identifier of the tile within the current level of this tile
	 */
	default public void onTileSelected(Model m, Tile tile, String id) {
	};

	/**
	 * Call the {@link #onTileSelected(Model,Tile,String)} event for all plugins
	 * that subscribed to it.
	 * 
	 * @param m    the model this event is called from.
	 * @param tile the tile selected
	 * @param id   the identifier of the tile within the current level of this tile
	 */
	public static void tileSelected(Model m, Tile tile, String id) {
		m.call("onTileSelected", InputPlugin.class, new Class[] { Model.class, Tile.class, String.class },
				new Object[] { m, tile, id });
	}

	/**
	 * Event for the deselection of a tile in a level. Tiles need to be deselected
	 * before another tile can be selected.
	 * 
	 * @param m    the model this event is called from
	 * @param tile the tile deselected
	 * @param id   the identifier of the tile within the current level of this tile
	 */
	default public void onTileDeselected(Model m, Tile tile, String id) {
	};

	/**
	 * Call the {@link #onTileDeselected(Model,Tile,String)} event for all plugins
	 * that subscribed to it.
	 * 
	 * @param m    the model this event is called from
	 * @param tile the tile deselected
	 * @param id   the identifier of the tile within the current level of this tile
	 */
	public static void tileDeselected(Model m, Tile tile, String id) {
		m.call("onTileDeselected", InputPlugin.class, new Class[] { Model.class, Tile.class, String.class },
				new Object[] { m, tile, id });
	}

	/**
	 * Event for a tile being moved in a level. A tile can be moved to the same
	 * position it originated from.
	 * 
	 * @param m    the model this event is called from
	 * @param tile the tile that got moved
	 * @param id   the identifier of the tile within the current level of this tile
	 * @param oldx the old x position of the tile (may be the same as current)
	 * @param oldy the old y position of the tile (may be the same as current)
	 */
	default public void onTileMoved(Model m, Tile tile, String id, int oldx, int oldy) {
	};

	/**
	 * Call the {@link #onTileMoved(Model,Tile,String,int,int)} event for all
	 * plugins that subscribed to it.
	 * 
	 * @param m    the model this event is called from
	 * @param tile the tile that got moved
	 * @param id   the identifier of the tile within the current level of this tile
	 * @param oldx the old x position of the tile (may be the same as current)
	 * @param oldy the old y position of the tile (may be the same as current)
	 */
	public static void tileMoved(Model m, Tile tile, String id, int oldx, int oldy) {
		m.call("onTileMoved", InputPlugin.class,
				new Class[] { Model.class, Tile.class, String.class, int.class, int.class },
				new Object[] { m, tile, id, oldx, oldy });
	}
}
