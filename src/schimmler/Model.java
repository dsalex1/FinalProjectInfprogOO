package schimmler;

import java.util.ArrayList;

/** The generic model that represents this game. */
public abstract class Model {
	
	/** The current level being played. */
	private Level level;
	
	/** The plugins currently loaded. */
	private ArrayList<Plugin> plugins;
	
}
