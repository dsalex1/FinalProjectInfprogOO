package schimmler.architecture;

import java.util.List;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

/** The generic model that represents this game. */
public abstract class Model {

	/** The current level being played. */
	protected Level level;

	/** The plugins currently loaded. */
	protected List<Plugin> plugins;
	
	/** A hashmap of lists for blacklisting plugins for specific events after they throw an exception. */
	private HashMap<String, List<Plugin>> blackList;

	/** Create a new Model and initialize it. */
	public Model() {
		plugins = new ArrayList<Plugin>();
		blackList = new HashMap<String, List<Plugin>>();
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
		try {
			plugin.init(this);
		}catch(UnsupportedOperationException uoe) {
			// Ignore UnsupportedOperationException's silently (most likely caused by a JavaScript plugin not having implemented this method)
		}catch(Exception e) {
			new RuntimeException(plugin.getName(),e).printStackTrace(); // print out the exception with plugin context
		}
	}
	
	/**
	 * Load all plugins a given loader can provide.
	 * @param loader the loader of the plugins to add
	 */
	public void loadPlugins(PluginLoader loader) {
		for(Plugin p:loader.loadPlugins())
			registerPlugin(p);
	}
	
	/**
	 * Call an event for a given plugin interface from all plugins.
	 * @param eventName the name of the event to call in all plugins
	 * @param from the interface this event is from
	 * @param cparams the class types of the parameters of this event
	 * @param params the parameters for this invocation
	 */
	public void call(String eventName, Class<? extends Plugin> from,  Class<?>[] cparams, Object[] params) {
		for(Plugin plugin:plugins) { // process each plugin for the event
			try {
				if(!from.isInstance(plugin)) continue; // don't process plugins that do not implement the origin or any extending class of it
				if(blackList.computeIfAbsent(eventName, k -> new ArrayList<Plugin>()).contains(plugin)) continue; // don't process plugins that have been blacklisted for this event (also create new array list if the event doesn't exist in the blacklist yet)
				plugin.getClass().getMethod(eventName, cparams).invoke(plugin, params); // call the event method through javas reflect framework
			}catch(InvocationTargetException ns) {
				if(ns.getTargetException() != null && ns.getTargetException() instanceof UnsupportedOperationException) { 
					blackList.get(eventName).add(plugin); // all plugins that are written in JavaScript and don't implement needed interface methods land here, other Plugins may use this as well
				}else
					new RuntimeException(plugin.getName(),ns.getTargetException()).printStackTrace(); // unhull the exception and output it with the associated plugin
			}catch(Exception e) {
				new RuntimeException(plugin.getName(),e).printStackTrace(); // all other exceptions should be send directly to the error stream
			}
		}
	}

}
