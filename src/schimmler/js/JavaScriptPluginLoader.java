package schimmler.js;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import schimmler.architecture.PluginLoader;
import schimmler.architecture.plugin.Plugin;

/**
 * A plugin loader for plugins written in JavaScript that are compatible with
 * Java 7+ Nashorn JavaScript Engine.
 */
public class JavaScriptPluginLoader extends PluginLoader {

	private File searchFolder;

	/**
	 * Create a new loader for plugins written in JavaScript, with a ".js" ending
	 * within the searchFolder.
	 * 
	 * @param searchFolder
	 *            the folder to search for plugins in
	 * @throws IllegalArgumentException
	 *             if the folder does not exist
	 */
	public JavaScriptPluginLoader(File searchFolder) {
		if (searchFolder == null || !searchFolder.exists())
			throw new IllegalArgumentException("The specified plugin folder does not exist");
		this.searchFolder = searchFolder;
	}

	@Override
	public List<Plugin> loadPlugins() {

		ArrayList<Plugin> pluginList = new ArrayList<Plugin>();

		ScriptEngineManager factory = new ScriptEngineManager(); // create a factory to get a engine instance
		ScriptEngine scriptEngine = factory.getEngineByName("javascript"); // get a javascript (more specific "nashorn" for Java 7+) engine

		try {
			// load the env.js as the first script into the script engine and evaluate it
			scriptEngine.eval(new FileReader(new File(JavaScriptPluginLoader.class.getResource("env.js").toURI())));

			File[] files = searchFolder.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return file.getName().toLowerCase().endsWith(".js");
				}
			});

			for (File file : files) {
				try {
					Object obj = scriptEngine.eval(new FileReader(file)); // save the object returned by the script
					if (obj != null && obj instanceof Plugin) { // only process the scripts that return Plugin instances further

						Plugin plugin = (Plugin) obj;
						try {
							/*
							 * this call is related to Nashorn specific behavior as it does not force a
							 * class to implement all method required by an interface. Instead it adds dummy
							 * methods to the instances that just throw an UnsupportedOperationException
							 * Exception.
							 * 
							 * As the getName() is the only method with a default implementation regularly used 
							 * independent of the subscripted events it is necessary to check for the existence of
							 * an actual implementation.
							 */
							plugin.getName();
						} catch (UnsupportedOperationException ns) {
							throw new IllegalArgumentException("Plugin object does not contain a 'getName' method in file '" + file.getName()+ "'");
						}
						pluginList.add(plugin);
					} else {
						throw new IllegalArgumentException("Script file didn't return a plugin-compatible object in file '" + file.getName()+ "'");
					}

				} catch (Exception e) {
					e.printStackTrace(); // print out the error messages produces by a faulty plugin, don't stop trying to load the next one though
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null; // a critical error occurred
		}

		return pluginList;
	}

}
