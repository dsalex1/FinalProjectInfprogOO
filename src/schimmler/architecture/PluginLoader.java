package schimmler.architecture;

import java.util.List;

import schimmler.architecture.plugin.Plugin;

/** An abstract class for custom loaders of plugins that not directly known at compile-tile. */
public abstract class PluginLoader {
	
	/**
	 * Return a list of all plugins this loader can provide.
	 * @return a list of all plugins from this loader
	 */
	public abstract List<Plugin> loadPlugins();
}
