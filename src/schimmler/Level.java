package schimmler;

import java.util.ArrayList;

/** A level containing given tiles that are movable to reach a win condition. */
public abstract class Level {

	/** The width of this level. */
	private int width;

	/** The height of this level. */
	private int height;

	/** The tiles present in this level. */
	private ArrayList<Tile> tiles;

	/**
	 * Returns true if a level has been finished successfully.
	 * 
	 * @return return if the level was completed correctly.
	 */
	public abstract boolean won();
}
