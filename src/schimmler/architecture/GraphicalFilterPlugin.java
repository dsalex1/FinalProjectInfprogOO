package schimmler.architecture;

/** A Plugin Type for changing the image rendered by a GraphicalView. */
public interface GraphicalFilterPlugin extends Plugin {

	/**
	 * Event for filtering a given array of RGB values.
	 * 
	 * @param m      the model this event is called from
	 * @param img    the array of RGB values to filter
	 * @param width  the width of the image
	 * @param height the height of the image
	 */
	public void onFilter(Model m, GraphicalView view, int[] img, int width, int height);

	/**
	 * Event for filtering a given array of RGB values.
	 * 
	 * @param m        the model this event is called from
	 * @param img      the array of RGB values to filter
	 * @param width    the width of the image
	 * @param height   the height of the image
	 * @param priority the priority of filters meant to be reached by this
	 */
	default public void onFilter(Model m, GraphicalView view, int[] img, int width, int height, int priority) {
		if (shouldFilter(priority))
			onFilter(m, view, img, width, height);
	}

	/**
	 * Return whether this filter should act/filter on the given priority.
	 * 
	 * @param priority the priority to check
	 */
	default public boolean shouldFilter(int priority) {
		return priority == 2;
	}

	/**
	 * Call the {@link #onFilter(Model,int[],int,int)} event for all plugins that
	 * subscribed to it.
	 * 
	 * @param m      the model this event is called from.
	 * @param img    the array of RGB values to filter
	 * @param width  the width of the image
	 * @param height the height of the image
	 */
	public static void filter(Model m, int[] img, int width, int height) {
		m.call("onFilter", GraphicalFilterPlugin.class, new Class[] { Model.class, int[].class, int.class, int.class },
				new Object[] { m, img, width, height });
	}

	/**
	 * Call the {@link #onFilter(Model,int[],int,int,int)} event for all plugins
	 * that subscribed to it.
	 * 
	 * @param m        the model this event is called from.
	 * @param img      the array of RGB values to filter
	 * @param width    the width of the image
	 * @param height   the height of the image
	 * @param priority the priority of this filter operation
	 */
	public static void filter(Model m, GraphicalView view, int[] img, int width, int height, int priority) {
		m.call("onFilter", GraphicalFilterPlugin.class,
				new Class[] { Model.class, GraphicalView.class, int[].class, int.class, int.class, int.class },
				new Object[] { m, view, img, width, height, priority });
	}

}
