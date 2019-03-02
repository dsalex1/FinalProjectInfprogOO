package schimmler.architecture;

import java.util.HashMap;
import java.util.Map;

/** A Tile in a map */
public final class Tile implements Cloneable {

	/** The most left x position of this tile. (lowest x) */
	protected int x;

	/** The most top y position of this tile. (lowest y) */
	protected int y;
	
	/** A map containing custom data in string format. */
	protected Map<String, String> data;

	/** The type of this tile */
	protected TileType type;
	

	/**
	 * Create a new tile with given coordinates.
	 * 
	 * @param x the x position.
	 * @param y the y position.
	 */
	public Tile(int x, int y, TileType type) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.data = new HashMap<String, String>();
	}

	/**
	 * Set the x position of this tile.
	 * 
	 * @param x the new x position of this tile.
	 */
	protected void setX(int x) {
		this.x = x;
	}

	/**
	 * Set the y position of this tile.
	 * 
	 * @param y the new y position of this tile.
	 */
	protected void setY(int y) {
		this.y = y;
	}

	/**
	 * Return the x position of this tile.
	 * 
	 * @return the x position of this tile.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Return the y position of this tile.
	 * 
	 * @return the y position of this tile.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Return the tile type of this tile.
	 * 
	 * @return the type of tile this is.
	 */
	public TileType getType() {
		return type;
	}
	
	/**
	 * Return a map of tags to data for custom data in tiles.
	 * @return the map of tags to data
	 */
	public Map<String, String> getData() {
		return data;
	}

	/**
	 * Return whether a given relative position is taken up by this tile.
	 * 
	 * @param cx the relative x position to check.
	 * @param cy the relative y position to check.
	 * @return whether this tile is at the given position.
	 */
	public boolean fieldOccupiedRelative(int cx, int cy) {
		return type.fieldOccupiedRelative(this, cx, cy);
	}

	/**
	 * Return whether a given absolute position is taken up by this tile.
	 * 
	 * @param cx the x position to check.
	 * @param cy the y position to check.
	 * @return whether this tile is at the given position.
	 */
	public boolean fieldOccupied(int cx, int cy) {
		return type.fieldOccupiedRelative(this, cx - this.x, cy - this.y);
	}
	
	@Override
	public Tile clone() {
		Tile clone = new Tile(this.x, this.y, this.getType());
		clone.getData().putAll(this.getData());
		return clone;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj.getClass() != this.getClass()) return false;
		Tile tile = (Tile) obj;
		if(tile.getX() != this.getX() || tile.getY() != this.getY()) return false;
		if(tile.getType() != this.getType()) return false;
		if(!tile.getData().equals(this.getData())) return false;
		return true;
	}

}
