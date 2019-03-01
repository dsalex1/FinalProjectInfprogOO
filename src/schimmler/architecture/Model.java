package schimmler.architecture;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

/** The generic model that represents this game. */
public abstract class Model {

	/** A map of tile type identifier to types of tiles. */
	protected Map<String, TileType> tileTypeDictionary;
	
	/** A map of level identifier to types of levels. */
	protected Map<String, LevelType> levelTypeDictionary;
	
	/** The current level being played. */
	protected Level level;

	/** The plugins currently loaded. */
	protected List<Plugin> plugins;
	
	/** A hashmap of lists for blacklisting plugins for specific events after they throw an exception. */
	private Map<String, List<Plugin>> blackList;
	
	/** A thread for sending events needed regullary. */
	private Thread gameLoop;

	/** Create a new Model and initialize it. */
	public Model() {
		plugins = new ArrayList<Plugin>();
		blackList = new HashMap<String, List<Plugin>>();
		tileTypeDictionary = new HashMap<String, TileType>();
		levelTypeDictionary = new HashMap<String, LevelType>();
		init();
	}

	/**
	 * A method for initializing this model by setting a start level.
	 */
	public abstract void init();
	
	/**
	 * A method meant to be executed after all plugins have been loaded into the Model.
	 */
	public void start() {
		Plugin.pluginsLoaded(this);
		gameLoop = new Thread(new Runnable() {
			public void run() {
				try {
					while(true) {
						View.update(Model.this);
						Thread.sleep(1000/30);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		gameLoop.start();
	}
	
	/**
	 * A methjod meant to be executed when the kill event has been triggered.
	 */
	@SuppressWarnings("deprecation")
	protected void stop() {
		try {
		if(gameLoop != null && gameLoop.isAlive())
			gameLoop.stop();
		}catch(Exception e) {}
	}

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
	 * Add a tile with a given identifier to the tile type dictionary.
	 * @param id   the identifier of the tile to add.
	 * @param tileType the tile type to add.
	 * @throws IllegalArgumentException the identifier was already present in the dictionary.
	 */
	public void registerTileType(String identifier, TileType tileType) {
		if (tileTypeDictionary.containsKey(identifier))
			throw new IllegalArgumentException("The identifier '" + identifier + "' is already present in the tile mapping.");
		tileTypeDictionary.put(identifier, tileType);
	}
	
	
	/**
	 * Return a tile type with a given identifier (or null if not in the model).
	 * 
	 * @param id the identifier to lookup.
	 * @return the found tile type with the given identifier (or null if not found).
	 */
	public TileType getTileType(String id) {
		return tileTypeDictionary.get(id);
	}
	
	
	/**
	 * Return the registered name of a TileType (or null). Meant for serialization.
	 * @param type the tile type to lookup
	 * @return the name of the TileType (or null if it is not registered)
	 */
	public String getTileTypeName(TileType type) {
		for(Entry<String, TileType> e:tileTypeDictionary.entrySet())
			if(type.equals(e.getValue())) return e.getKey();
		return null;
	}
	
	/**
	 * Return the identifier to tile type mapping currently existent on this model.
	 * @return the tile type mapping
	 */
	public Map<String, TileType> getTileTypes() {
		return tileTypeDictionary;
	}
	
	
	/**
	 * Create a tile out of a tile type identifier and a x and y position.
	 * @param identifier the name of the tile type
	 * @param x the x position of the tile
	 * @param y the y position of the tile
	 * @return the created tile
	 * @throes IllegalArgumentException if no tile type with the given identifier was found
	 */
	public Tile createTile(String identifier, int x, int y) {
		TileType type = getTileType(identifier);
		if(type == null) throw new IllegalArgumentException("No TileType with the identifier '"+identifier+"' was found.");
		Tile level = new Tile(x, y, type);;
		return level;
	}
	
	/**
	 * Add a tile with a given identifier to the level type dictionary.
	 * @param id   the identifier of the level type to add.
	 * @param levelType the level type to add.
	 * @throws IllegalArgumentException the identifier was already present in the dictionary.
	 */
	public void registerLevelType(String identifier, LevelType levelType) {
		if (levelTypeDictionary.containsKey(identifier))
			throw new IllegalArgumentException("The identifier '" + identifier + "' is already present in the level type mapping.");
		levelTypeDictionary.put(identifier, levelType);
	}
	
	
	/**
	 * Return a level type with a given identifier (or null if not in the model).
	 * 
	 * @param id the identifier to lookup.
	 * @return the found level type with the given identifier (or null if not found).
	 */
	public LevelType getLevelType(String id) {
		return levelTypeDictionary.get(id);
	}
	
	
	/**
	 * Return the registered name of a LevelType (or null). Meant for serialization.
	 * @param type the level type to lookup
	 * @return the name of the LevelType (or null if it is not registered)
	 */
	public String getLevelTypeName(LevelType type) {
		for(Entry<String, LevelType> e:levelTypeDictionary.entrySet())
			if(type.equals(e.getValue())) return e.getKey();
		return null;
	}
	
	/**
	 * Return the identifier to level type mapping currently existent on this model.
	 * @return the level type mapping
	 */
	public Map<String, LevelType> getLevelTypes() {
		return levelTypeDictionary;
	}
	
	/**
	 * Create a level out of a level type identifier.
	 * @param identifier the name of the level type
	 * @return the created level
	 * @throes IllegalArgumentException if no level type with the given identifier was found
	 */
	public Level createLevel(String identifier) {
		LevelType type = getLevelType(identifier);
		if(type == null) throw new IllegalArgumentException("No LevelType with the identifier '"+identifier+"' was found.");
		Level level = new Level(type);
		type.init(level);
		return level;
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
	 * Also trigger an event that the plugin has been loaded globally.
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
		Plugin.pluginLoaded(this, plugin);
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
