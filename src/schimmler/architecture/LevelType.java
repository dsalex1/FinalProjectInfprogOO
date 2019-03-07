package schimmler.architecture;

import java.util.Map.Entry;

/**
 * An abstract type of level providing the creation of it, a possible reload
 * handler and a win condition.
 */
public abstract class LevelType {

	
	public LevelType() {
		init();
	}
	
	/**
	 * Init function called when creating the level type.
	 */
	public void init() {
		
	}
	
	/**
	 * A method for initializing a level by placing the tiles and setting the width
	 * and height.
	 * @param model The model this level was initialized in.
	 * @param level The level to initialize
	 */
	public abstract void init(Model model, Level level);

	/**
	 * Returns true if a level has been finished successfully.
	 * 
	 * @return return if the level was completed correctly.
	 */
	public boolean won(Level level) {
		Level wLevel = getWonLevel();
		if(wLevel == null) return false;
		for(Entry<String, Tile> e:wLevel.getTileMap().entrySet()) {
			Tile t = level.getTile(e.getKey());
			if(t == null) return false;
			if(!t.getType().equals(e.getValue().getType())) return false;
			if(t.getX() != e.getValue().getX()) return false;
			if(t.getY() != e.getValue().getY()) return false;
		}
		return true;
	}
	
	
	/**
	 * Return a version of the level when won, will be displayed.
	 * @return the won level
	 */
	public abstract Level getWonLevel();
	
	/**
	 * A method used for optional update events of a level.
	 * @param model the model this is happening in.
	 * @param level the level that was updated.
	 */
	public void update(Model model, Level level) {
		
	}
	
	/**
	 * Return the identifier of the field that occupies the given x and y position
	 * and exclude a given identifier for a given level (null if the field is free).
	 * 
	 * @param level   the level to run this at
	 * @param x       the x position of the field to check.
	 * @param y       the y position of the field to check.
	 * @param exclude the identifier to exclude
	 * @return the found fields identifier or null if it is free.
	 */
	public String fieldOccupied(Level level, int x, int y, String exclude) {
		for (Entry<String, Tile> t : level.getTileMap().entrySet()) {
			if (exclude != null && t.getKey().equals(exclude))
				continue;
			if (t.getValue().fieldOccupied(x, y))
				return t.getKey();
		}
		return null;
	}

	/**
	 * Returns whether a tile can move in a straigt line to (x,y) wihtout colliding
	 * with other tiles
	 * 
	 * @param level the level to run this at
	 * @param name  name of the tile to check
	 * @param x     target x to check for
	 * @param y     target y to check for
	 * @return whether a tile may move to this coordinates without collision
	 */
	public boolean canTileMoveTo(Level level, String name, int x, int y) {
		Tile tile = level.getTile(name);
		int oX = tile.getX();
		int oY = tile.getY();

		int dX = x - oX;
		int dY = y - oY;

		// Position is outside of the map
		if (!level.inLevel(x, y)) {
			return false;
		}
		// may only move in x xor y direction
		if ((dY != 0) && (dX != 0)) {
			return false;
		}

		for (int i = 0; i < level.getHeight(); i++)
			for (int j = 0; j < level.getWidth(); j++) {
				if (!tile.fieldOccupiedRelative(i, j))
					continue;
				if (!level.inLevel(i + x, j + y)) {
					// out of map
					return false;
				}
				String f = fieldOccupied(level, i + x, j + y, name);
				if (f != null) {
					// field is occupied
					return false;
				}
			}

		// we need to check if the path is blocked. if we shall go more than 1 space in
		// x direction, we need to check whether we may go to the next narrower space
		// too, successivly,
		if (Math.abs(dX) > 1) {
			return canTileMoveTo(level, name, x - (int) Math.signum(dX), y);
		}
		// same for y
		if (Math.abs(dY) > 1) {
			return canTileMoveTo(level, name, x, y - (int) Math.signum(dY));
		}

		return true;
	}
}
