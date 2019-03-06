package schimmler.architecture;

/**
 * A specific plugin meant to only display data graphically, pixel based, to the
 * end user.
 */
public interface GraphicalView extends View {
	/** returns the Coordinates of a given pixel in the level */
	public int[] getLvlCoordAt(int x, int y);

	/** returns scaling factor from tiles to pixels */
	public int[] getPixelsPerTile();
	
	
	/**
	 * Run all GraphicalFilterPlugins on a given image.
	 * @param model the model to operate on
	 * @param img the image to apply the filters on
	 * @param width the width of the image
	 * @param height the height of the image
	 */
	public static void filter(Model model, int[] img, int width, int height) {
		for(int i=0;i<5;i++)
			GraphicalFilterPlugin.filter(model, img, width, height,  i);
	}
	

	/**
	 * @param selectedOffset the offset of the selected tile to set
	 */
	public void setSelectedOffset(int x, int y);

}
