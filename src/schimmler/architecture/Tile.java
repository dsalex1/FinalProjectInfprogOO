package schimmler.architecture;

/** A Tile in a map */
public abstract class Tile {

	/** The most left x position of this tile. (lowest x) */
	protected int x;

	/** The most top y position of this tile. (lowest y) */
	protected int y;
	
	/**
	 * Create a new tile with given coordinates.
	 * 
	 * @param x the x position.
	 * @param y the y position.
	 */
	public Tile(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Set the x position of this tile.
	 * 
	 * @param x the new x position of this tile.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Set the y position of this tile.
	 * 
	 * @param y the new y position of this tile.
	 */
	public void setY(int y) {
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
	 * Return whether a given relative position is taken up by this tile.
	 * 
	 * @param cx the relative x position to check.
	 * @param cy the relative y position to check.
	 * @return whether this tile is at the given position.
	 */
	public abstract boolean fieldOccupiedRelative(int cx, int cy);
	
	/**
	 * Return whether a given absolute position is taken up by this tile.
	 * 
	 * @param cx the x position to check.
	 * @param cy the y position to check.
	 * @return whether this tile is at the given position.
	 */
	public boolean fieldOccupied(int cx, int cy) {
		return fieldOccupiedRelative(cx-x, cy-y);
	}
	

}
