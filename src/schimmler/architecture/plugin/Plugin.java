package schimmler.architecture.plugin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import schimmler.architecture.Model;

/**
 * A plugin is an optional addition to an existing Model that may act on events
 * and/or instantiate them itself.
 */
public interface Plugin {

	// Important Note
	// All Plugins must provide an empty constructor.

	/**
	 * Initialize a plugin.
	 * 
	 * @param m the model this event is called from.
	 */
	public void init(Model m);

	/**
	 * Kill and deactive a plugin.
	 * 
	 * @param m the model this event is called from.
	 */
	default public void onKill(Model m) {
	}

	/**
	 * Call the {@link #onKill(Model)} event for all plugins that subscribed to it.
	 * Also stop the main game thead of the model.
	 * 
	 * @param m the model that triggered the event
	 */
	public static void kill(Model m) {
		m.call("onKill", Plugin.class, new Class[] { Model.class }, new Object[] { m });
		m.stop();
	}

	/**
	 * A new plugin has been loaded.
	 * 
	 * @param m the model this event is called from.
	 */
	default public void onPluginLoaded(Model m, Plugin p) {
	}

	/**
	 * Call the {@link #onPluginLoaded(Model,Plugin)} event for all plugins that
	 * subscribed to it.
	 * 
	 * @param m the model that triggered the event
	 */
	public static void pluginLoaded(Model m, Plugin p) {
		m.call("onPluginLoaded", Plugin.class, new Class[] { Model.class, Plugin.class }, new Object[] { m, p });
	}

	/**
	 * All plugins have been loaded.
	 * 
	 * @param m the model this event is called from.
	 */
	default public void onPluginsLoaded(Model m) {
	}

	/**
	 * Call the {@link #onPluginsLoaded(Model)} event for all plugins that
	 * subscribed to it.
	 * 
	 * @param m the model that triggered the event
	 */
	public static void pluginsLoaded(Model m) {
		m.call("onPluginsLoaded", Plugin.class, new Class[] { Model.class }, new Object[] { m });
	}

	/**
	 * Event fired when a new level is set
	 * 
	 * @param m the model this event is called from
	 */
	default public void onlevelSet(Model m) {
	}

	/**
	 * Call the {@link #onlevelSet(Model)} event for all plugins that subscribed to
	 * it.
	 * 
	 * @param m the model this event is called from.
	 */
	public static void levelSet(Model m) {
		m.call("onlevelSet", InputPlugin.class, new Class[] { Model.class }, new Object[] { m });
	}

	/**
	 * Event fired when the current level is won
	 * 
	 * @param m the model this event is called from
	 * @param x the x position of the cursor
	 * @param y the y position of the cursor
	 */
	default public void onWon(Model m) {
	}

	/**
	 * Call the {@link #onWon(Model)} event for all plugins that subscribed to it.
	 * 
	 * @param m the model this event is called from.
	 * @param x the x position of the cursor
	 * @param y the y position of the cursor
	 */
	public static void won(Model m) {
		m.call("onWon", InputPlugin.class, new Class[] { Model.class }, new Object[] { m });
	}

	/**
	 * Return the name of the plugin. This method *MUST* be implemented by all
	 * plugins.
	 * 
	 * @return the name or identifier of the plugin.
	 */
	public String getName();

	static final boolean LOG_TIME = true;

	/**
	 * Log a normal message from the plugin that invoked it.
	 * 
	 * @param msg the message to log
	 */
	default public void log(String msg) {
		if (!LOG_TIME)
			System.out.println("[" + getName() + "]: " + msg);
		else
			System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + "]["
					+ getName() + "]: " + msg);
	}

	/**
	 * Log an error message from the plugin that invoked it.
	 * 
	 * @param msg the error message to log
	 */
	default public void err(String msg) {
		if (!LOG_TIME)
			System.err.println("[" + getName() + "]: " + msg);
		else
			System.err.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + "]["
					+ getName() + "]: " + msg);

	}

}
