package schimmler;

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
	
	/** Create a new Level and initialize it. */
	public Level() {
		tiles = new HashMap<String, Tile>();
		init();
	}
	
	/**
	 * Return the width of this level.
	 * @return the width of this level.
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Return the height of this level.
	 * @return the height of this level.
	 */
	public int getHeigth() {
		return height;
	}
	
	
	/** 
	 * Return the identifier to tile mapping of this level.
	 * @return the identifier to tile mapping of this level.
	 */
	public Map<String, Tile> getTileMap() {
		return tiles;
	}
	
	/** 
	 * Return the tiles of this level.
	 * @return the tiles of this level.
	 */
	public Collection<Tile> getTiles() {
		return tiles.values();
	}
	
	/**
	 * Add a tile with a given identifier to the level.
	 * @param id the identifier of the tile to add.
	 * @param tile the tile to add.
	 * @throws IllegalArgumentException the identifier was already present in the mapping.
	 */
	public void addTile(String id, Tile tile) {
		if(tiles.containsKey(id))
			throw new IllegalArgumentException("The identifier '"+id+"' is already present in the tile mapping.");
		tiles.put(id, tile);
	}
	
	
	/**
	 * Return a tile with a given identifier (or null if not in the level).
	 * @param id the identifier to lookup.
	 * @return the found title with the given identifier (or null if not found).
	 */
	public Tile getTile(String id) {
		return tiles.get(id);
	}
	
	/**
	 * Return whether the given coordinates are within the level.
	 * @param x the x position to check.
	 * @param y the y position to check.
	 * @return whether the position is within the level.
	 */
	public boolean inLevel(int x, int y) {
		return !(x < 0 || y < 0 || width >= x || height >= y);
	}
	
	/**
	 * Return the identifier of the field that occupies the given x and y position (null if the field is free).
	 * @param x the x position of the field to check.
	 * @param y the y position of the field to check.
	 * @return the found fields identifer or null if it is free.
	 */
	public String fieldOccupied(int x, int y) {
		for(Entry<String, Tile> t:tiles.entrySet()) {
			if(t.getValue().fieldOccupied(x, y))
				return t.getKey();
		}
		return null;
	}
	
	
	/**
	 * A method for initializing this level by placing the tiles and setting the width and height.
	 */
	public abstract void init();

	/**
	 * Returns true if a level has been finished successfully.
	 * 
	 * @return return if the level was completed correctly.
	 */
	public abstract boolean won();
}
