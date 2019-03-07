package schimmler.jar;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarFile;

import schimmler.architecture.PluginLoader;
import schimmler.architecture.plugin.Plugin;

/**
 * A plugin loader for plugins in the Jar File Format.
 */
public class JarFilePluginLoader extends PluginLoader {

	private File searchFolder;

	/**
	 * Create a new loader for plugins written in a JVM compatible language saved in the Jar File Format.
	 * 
	 * @param searchFolder
	 *            the folder to search for plugins in
	 * @throws IllegalArgumentException
	 *             if the folder does not exist
	 */
	public JarFilePluginLoader(File searchFolder) {
		if (searchFolder == null || !searchFolder.exists())
			throw new IllegalArgumentException("The specified plugin folder does not exist");
		this.searchFolder = searchFolder;
	}

	
	
	// https://www.oreilly.com/ideas/handling-checked-exceptions-in-java-streams
	// Pretty beautiful idea for generic wrapping of exception for usage in lambdas
	
	@FunctionalInterface
	public interface FunctionWithException<T, R, E extends Exception> {
	    R apply(T t) throws E;
	}
	private <T, R, E extends Exception> Function<T, R> mapEx(FunctionWithException<T, R, E> fe) {
        return arg -> {
            try {
                return fe.apply(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
	}
	private <T, R, E extends Exception> Predicate<T> filterEx(FunctionWithException<T, R, E> fe) {
        return arg -> {
            try {
                return (boolean) fe.apply(arg);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
	}
	

	private void loadPlugin(ArrayList<Plugin> pluginList, JarFile jarFile, File file) throws Exception {
		@SuppressWarnings("deprecation")
		URLClassLoader urlLoader = URLClassLoader.newInstance(new URL[] { file.toURL() }); // create a new class loader for the given jar file
		
		jarFile.stream()
		.filter(entry -> !entry.isDirectory()) // ignore all Jar File Entries that are directories
		.filter(entry -> entry.getName().endsWith(".class")) // ignore all files in the Jar File that don't have the typical class file ending
		.map(entry -> entry.getName()) // continue only processing the internal path (name) of the jar file entry
		.map(entryName -> entryName.substring(0, entryName.length()-6)) // remove the ".class" ending
		.map(entryName -> entryName.replace('/', '.')) // change the path to the java package format
		.map(mapEx(entryName -> urlLoader.loadClass(entryName))) // load the file using the class loader into the JVM
		.filter(c -> Plugin.class.isAssignableFrom(c)) // ignore all classes that are neither the Plugin interface nor extending or implementing it
		.filter(c -> !c.isInterface()) // ignore all interfaces
		.filter(filterEx(c -> c.getConstructor() != null)) // ignore all classes not providing an empty constructor
		.filter(filterEx(c -> !Modifier.isAbstract(c.getConstructor().getModifiers()))) // ignore all abstract classes
		.filter(filterEx(c -> Modifier.isPublic(c.getConstructor().getModifiers()))) // ignore all plugins with a non public empty constructor
		.map(mapEx(c -> ((Plugin)c.getConstructor().newInstance()))) // create a new instance of the plugin using the empty constructor
		.forEach(plugin -> pluginList.add(plugin)); // load the plugin into the list list
	}
	
	@Override
	public List<Plugin> loadPlugins() {

		ArrayList<Plugin> pluginList = new ArrayList<Plugin>();

		try {
			File[] files = searchFolder.listFiles(new FileFilter() {
				public boolean accept(File file) {
					return file.getName().toLowerCase().endsWith(".jar");
				}
			});
			JarFile jarFile = null;
			for (File file:files) {
				try {
					jarFile = new JarFile(file);
					loadPlugin(pluginList, jarFile, file);
				} catch (Exception e) {
					e.printStackTrace(); // print out the error messages produces by a faulty plugin, don't stop trying to load the next one though
				} finally {
					if(jarFile != null)
						jarFile.close(); // close the jar file
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null; // a critical error occurred
		}

		return pluginList;
	}

}
