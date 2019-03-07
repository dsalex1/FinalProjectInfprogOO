package schimmler.architecture.plugin;

import schimmler.architecture.Model;

/**  An interface for plugins that want to listen to direct mouse input from the user.*/
public interface MouseInputPlugin extends Plugin {
	
	/**
	 * Event for cursor movement.
	 * 
	 * @param m the model this event is called from
	 * @param x the x position of the cursor
	 * @param y the y position of the cursor
	 */
	public void onCursorMove(Model m, int x, int y);

	/**
	 * Call the {@link #onCursorMove(Model,int,int)} event for all plugins that
	 * subscribed to it.
	 * 
	 * @param m the model this event is called from.
	 * @param x the x position of the cursor
	 * @param y the y position of the cursor
	 */
	public static void cursorMove(Model m, int x, int y) {
		m.call("onCursorMove", MouseInputPlugin.class, new Class[] { Model.class, int.class, int.class },
				new Object[] { m, x, y });
	}
	
}
