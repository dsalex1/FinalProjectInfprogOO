package schimmler.architecture;

import java.util.List;
import java.util.ArrayList;

/** The generic model that represents this game. */
public abstract class Model {

	/** The current level being played. */
	protected Level level;

	/** The plugins currently loaded. */
	protected List<Plugin> plugins;

	/** Create a new Model and initialize it. */
	public Model() {
		plugins = new ArrayList<Plugin>();
		init();
	}

	/**
	 * A method for initializing this model by setting a start level.
	 */
	public abstract void init();

	/**
	 * Return the current level of this model.
	 * 
	 * @return the current level.
	 */
	public Level getLevel() {
		return level;
	}

	/**
	 * Set the current level of this model.
	 * 
	 * @param level the new level.
	 */
	public void setLevel(Level level) {
		this.level = level;
	}

	/**
	 * Return the list of currently loaded plugins.
	 * 
	 * @return the list of currently loaded and active plugins.
	 */
	public List<Plugin> getPlugins() {
		return plugins;
	}

	/**
	 * Register, initialize and load a plugin into this model.
	 * 
	 * @param plugin the plugin to register.
	 */
	public void registerPlugin(Plugin plugin) {
		plugins.add(plugin);
		plugin.init(this);
	}

}
