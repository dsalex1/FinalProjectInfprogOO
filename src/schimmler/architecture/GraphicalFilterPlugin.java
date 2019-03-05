package schimmler.architecture;


/** A Plugin Type for changing the image rendered by a GraphicalView. */
public interface GraphicalFilterPlugin extends Plugin {
	
	/**
	 * Event for filtering a given array of RGB values.
	 * 
	 * @param m the model this event is called from
	 * @param img the array of RGB values to filter
	 */
	public void onFilter(Model m, int[][] img);
	
	
	/**
	 * Event for filtering a given array of RGB values.
	 * 
	 * @param m the model this event is called from
	 * @param img the array of RGB values to filter
	 * @param priority the priority of filters meant to be reached by this
	 */
	default public void onFilter(Model m, int[][] img, int priority) {
		if(shouldFilter(priority))
			onFilter(m, img);
	}
	
	
	/**
	 * Return whether this filter should act/filter on the given priority.
	 * @param priority the priority to check
	 */
	default public boolean shouldFilter(int priority) {
		return priority == 2;
	}
	

	/**
	 * Call the {@link #onFilter(Model,int[][])} event for all plugins that
	 * subscribed to it.
	 * 
	 * @param m the model this event is called from.
	 * @param img the array of RGB values to filter
	 */
	public static void filter(Model m, int[][] img) {
		m.call("onFilter", GraphicalFilterPlugin.class, new Class[] { Model.class, int[][].class },
				new Object[] { m, img });
	}
	
	/**
	 * Call the {@link #onFilter(Model,int[][],int)} event for all plugins that
	 * subscribed to it.
	 * 
	 * @param m the model this event is called from.
	 * @param img the array of RGB values to filter
	 * @param priority the priority of this filter operation
	 */
	public static void filter(Model m, int[][] img, int priority) {
		m.call("onFilter", GraphicalFilterPlugin.class, new Class[] { Model.class, int[][].class, int.class },
				new Object[] { m, img, priority });
	}
	
	
}
