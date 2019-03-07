package schimmler.architecture.plugin;

import schimmler.architecture.Model;

/** A specific plugin meant to only display data to the end user. */
public interface View extends Plugin {

	/**
	 * A method that gets called regulary for frame updates.
	 * 
	 * @param m the model that triggered the event
	 */
	public void onUpdate(Model m);

	/**
	 * Call the {@link #onUpdate(Model)} event for all plugins that subscribed to
	 * it.
	 * 
	 * @param m the model that triggered the event
	 */
	public static void update(Model m) {
		m.call("onUpdate", View.class, new Class[] { Model.class }, new Object[] { m });
	}
}
