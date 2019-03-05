package schimmler.architecture;

/**
 * A specific plugin meant to only display data graphically, pixel based, to the
 * end user.
 */
public interface GraphicalView extends View {
	/** returns the Coordinates of a given pixel in the level */
	public abstract int[] getLvlCoordAt(int x, int y);

	/** returns scaling factor from tiles to pixels */
	public abstract int[] getPixelsPerTile();

	/**
	 * @param selectedOffset the offset of the selected tile to set
	 */
	public void setSelectedOffset(int x, int y);

}
