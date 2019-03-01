package schimmler.architecture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/** A level containing given tiles that are movable to reach a win condition. */
public abstract class Level {

	/** The width of this level. */
	protected int width;

	/** The height of this level. */
	protected int height;

	/** The tiles present in this level where each tile has a unique identifier. */
	protected Map<String, Tile> tiles;

	/**
	 * The identifier of the currently selected tile, null if no tile is selected.
	 */
	protected String selected;

	/** Create a new Level and initialize it. */
	public Level() {
		tiles = new HashMap<String, Tile>();
		init();
	}

	/**
	 * Return the identifier of the currently selected tile. Null if no tile is
	 * selected.
	 * 
	 * @return the identifier of the currently selected tile or null.
	 */
	public String getSelected() {
		return selected;
	}

	/**
	 * Set the currently selected tile or null if no tile should be selected.
	 * 
	 * @param id the identifier of the new selected tile or null
	 * @throws IllegalArgumentException if an identifier not in this level is
	 *                                  supplied
	 */
	public void setSelected(String id) {
		if (id != null && !tiles.containsKey(id))
			throw new IllegalArgumentException("The tile with the identifier '" + id + "' is not in this level.");
		this.selected = id;
	}

	/**
	 * Return the width of this level.
	 * 
	 * @return the width of this level.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Return the height of this level.
	 * 
	 * @return the height of this level.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Return the identifier to tile mapping of this level.
	 * 
	 * @return the identifier to tile mapping of this level.
	 */
	public Map<String, Tile> getTileMap() {
		return tiles;
	}

	/**
	 * Return the tiles of this level.
	 * 
	 * @return the tiles of this level.
	 */
	public Collection<Tile> getTiles() {
		return tiles.values();
	}

	/**
	 * Add a tile with a given identifier to the level.
	 * 
	 * @param id   the identifier of the tile to add.
	 * @param tile the tile to add.
	 * @throws IllegalArgumentException the identifier was already present in the
	 *                                  mapping.
	 */
	public void addTile(String id, Tile tile) {
		if (tiles.containsKey(id))
			throw new IllegalArgumentException("The identifier '" + id + "' is already present in the tile mapping.");
		tiles.put(id, tile);
	}

	/**
	 * Return a tile with a given identifier (or null if not in the level).
	 * 
	 * @param id the identifier to lookup.
	 * @return the found title with the given identifier (or null if not found).
	 */
	public Tile getTile(String id) {
		return tiles.get(id);
	}

	/**
	 * Return whether the given coordinates are within the level.
	 * 
	 * @param x the x position to check.
	 * @param y the y position to check.
	 * @return whether the position is within the level.
	 */
	public boolean inLevel(int x, int y) {
		return (x >= 0 && y >= 0 && width > x && height > y);
	}

	/**
	 * Return the identifier of the field that occupies the given x and y position
	 * and exclude a given identifier (null if the field is free).
	 * 
	 * @param x       the x position of the field to check.
	 * @param y       the y position of the field to check.
	 * @param exclude the identifier to exclude
	 * @return the found fields identifier or null if it is free.
	 */
	public String fieldOccupied(int x, int y, String exclude) {
		for (Entry<String, Tile> t : tiles.entrySet()) {
			if (exclude != null && t.getKey().equals(exclude))
				continue;
			if (t.getValue().fieldOccupied(x, y))
				return t.getKey();
		}
		return null;
	}

	/**
	 * Return the identifier of the field that occupies the given x and y position
	 * (null if the field is free).
	 * 
	 * @param x the x position of the field to check.
	 * @param y the y position of the field to check.
	 * @return the found fields identifer or null if it is free.
	 */
	public String fieldOccupied(int x, int y) {
		return fieldOccupied(x, y, null);
	}

	/**
	 * A method for initializing this level by placing the tiles and setting the
	 * width and height.
	 */
	public abstract void init();

	/**
	 * Returns true if a level has been finished successfully.
	 * 
	 * @return return if the level was completed correctly.
	 */
	public abstract boolean won();

	public Boolean canTileMoveTo(String name, int x, int y) {
		Tile tile = this.getTile(name);
		int oX = tile.getX();
		int oY = tile.getY();

		int dX = x - oX;
		int dY = y - oY;

		// Position is outside of the map
		if (!this.inLevel(x, y)) {
			return false;
		}
		// may only move in x xor y direction
		if ((dY != 0) && (dX != 0)) {
			return false;
		}

		for (int i = 0; i < this.getHeight(); i++)
			for (int j = 0; j < this.getWidth(); j++) {
				if (!tile.fieldOccupiedRelative(i, j))
					continue;
				if (!this.inLevel(i + x, j + y)) {
					// out of map
					return false;
				}
				String f = this.fieldOccupied(i + x, j + y, name);
				if (f != null) {
					// field is occupied
					return false;
				}
			}

		// we need to check if the path is blocked. if we shall go more than 1 space in
		// x direction, we need to check whether we may go to the next narrower space
		// too, successivly,
		if (Math.abs(dX) > 1) {
			return canTileMoveTo(name, x - (int) Math.signum(dX), y);
		}
		// same for y
		if (Math.abs(dY) > 1) {
			return canTileMoveTo(name, x, y - (int) Math.signum(dY));
		}

		return true;
	}
}
