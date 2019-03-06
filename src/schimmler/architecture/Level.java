package schimmler.architecture;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/** A level containing given tiles that are movable to reach a win condition. */
public final class Level implements Cloneable {

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

	/** The type of this level. */
	protected LevelType type;

	/** Create a new Level and initialize it. */
	public Level(LevelType type) {
		tiles = new HashMap<String, Tile>();
		this.type = type;
	}

	/**
	 * Return the type of this level.
	 * 
	 * @return the type of this level.
	 */
	public LevelType getType() {
		return type;
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
	 * Set the width of this level.
	 * 
	 * @param width the new width of this level
	 */
	public void setWidth(int width) {
		this.width = width;
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
	 * Set the height of this level.
	 * 
	 * @param height the new height of this level
	 */
	public void setHeight(int height) {
		this.height = height;
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
	 * Return the identifier of the field that occupies the given x and y position
	 * and exclude a given identifier (null if the field is free).
	 * 
	 * @param x       the x position of the field to check.
	 * @param y       the y position of the field to check.
	 * @param exclude the identifier to exclude
	 * @return the found fields identifier or null if it is free.
	 */
	public String fieldOccupied(int x, int y, String exclude) {
		return type.fieldOccupied(this, x, y, exclude);
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
		return type.fieldOccupied(this, x, y, null);
	}

	/**
	 * Returns whether a tile can move in a straigt line to (x,y) wihtout colliding
	 * with other tiles
	 * 
	 * @param name name of the tile to check
	 * @param x    target x to check for
	 * @param y    target y to check for
	 * @return whether a tile may move to this coordinates without collision
	 */
	public boolean canTileMoveTo(String name, int x, int y) {
		return type.canTileMoveTo(this, name, x, y);
	}

	@Override
	public Level clone() {
		Level clone = new Level(this.getType());
		clone.setWidth(this.width);
		clone.setHeight(this.height);
		for (Entry<String, Tile> e : this.getTileMap().entrySet())
			clone.addTile(e.getKey(), e.getValue().clone());
		clone.setSelected(this.selected);
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() != this.getClass())
			return false;
		Level level = (Level) obj;
		if (level.getWidth() != this.getWidth() || level.getHeight() != this.getHeight())
			return false;
		if (!level.getSelected().equals(this.getSelected()))
			return false;
		if (level.getType() != this.getType())
			return false;
		if (!level.getTileMap().equals(this.getTileMap()))
			return false;
		return true;
	}

	/**
	 * @param tiles the tiles to set
	 */
	public void setTiles(Map<String, Tile> tiles) {
		this.tiles = tiles;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(LevelType type) {
		this.type = type;
	}
	
	/**
	 * Sets a Tile internal to a new position. only to be used to calculate things
	 * on levels, does not work in models.
	 * 
	 * @param id the identifier of the tile to be moved
	 * @param x  the new x position of the tile
	 * @param y  the new y position of the tile
	 * @throws IllegalArgumentException if an identifier not in this level is
	 *                                  supplied
	 */
	public void setTilePositionInternal(String id, int x, int y) {
		if (id != null && !this.tiles.containsKey(id))
			throw new IllegalArgumentException("The tile with the identifier '" + id + "' is not in this level.");
		Tile tile = this.getTile(id);
		tile.setX(x);
		tile.setY(y);
	}


}
