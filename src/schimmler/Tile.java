package schimmler;

/** A Tile in a map */
public abstract class Tile {
	
	/** The x position of this tile. */
	private int x;
	
	/** The y position of this tile. */
	private int y;
	
	/**
	 * Return whether this tile can move to the given coordinates from the current position given a level.
	 * @param level the level the tile is currently in.
	 * @param newX the new x position.
	 * @param newY the new y position.
	 * @return returns if the field at newX and newY is reachable and free.
	 */
	public abstract boolean canMoveTo(Level level, int newX, int newY);
}
