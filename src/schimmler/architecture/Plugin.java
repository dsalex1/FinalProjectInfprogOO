package schimmler.architecture;

/**
 * A plugin is an optional addition to an existing Model that may act on events
 * and/or instantiate them itself.
 */
public interface Plugin {

	/**
	 * Initialize a plugin.
	 * @param m the model this event is called from.
	 */
	public void init(Model m);
	
	/**
	 * Kill and deactive a plugin.
	 * 
	 * @param m the model this event is called from.
	 */
	default public void onKill(Model m)  {}
	
	/**
	 * Call the {@link #onKill(Model)} event for all plugins that subscribed to it.
	 * @param m  the model that triggered the event
	 */
	public static void kill(Model m) {
		m.call("onKill", View.class, new Class[] {Model.class}, new Object[] {m});
	}

	/**
	 * Return the name of the plugin.
	 * This method *MUST* be implemented by all plugins.
	 * @return the name or identifier of the plugin.
	 */
	public String getName();
	
	/**
	 * Log a normal message from the plugin that invoked it.
	 * @param msg the message to log
	 */
	default public  void log(String msg) {
		System.out.println("["+getName()+"]: "+msg);
	}
	
	/**
	 * Log an error message from the plugin that invoked it.
	 * @param msg the error message to log
	 */
	default public  void err(String msg) {
		System.err.println("["+getName()+"]: "+msg);
	}

}
