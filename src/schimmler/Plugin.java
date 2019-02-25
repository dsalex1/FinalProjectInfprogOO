package schimmler;

/** A plugin is an optional addition to an existing Model that may act on events and/or instantiate them itself. */
public interface Plugin {
	
	/**
	 * Initialize a plugin.
	 * @param m the model this event is called from.
	 */
	public void init(Model m);
	
	/**
	 * Return the name of the plugin.
	 * @return the name or identifier of the plugin.
	 */
	public String getName();
	
}
