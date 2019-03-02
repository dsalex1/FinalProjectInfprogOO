package schimmler.architecture;

/** A TileType to make tiles out of */
public abstract class TileType {
	
	/**
	 * A method for initializing a tile by setting custom data.
	 * @param tile the tile to initialize
	 */
	public void init(Tile tile) {
		
	}

	/**
	 * Return whether a given relative position is taken up by a given tile.
	 * 
	 * @param tile the tile to check
	 * @param cx the relative x position to check.
	 * @param cy the relative y position to check.
	 * @return whether this tile is at the given position.
	 */
	public abstract boolean fieldOccupiedRelative(Tile tile, int cx, int cy);
	

}
