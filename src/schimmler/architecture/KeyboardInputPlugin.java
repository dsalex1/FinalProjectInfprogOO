package schimmler.architecture;

/**  An interface for plugins that want to listen to direct keyboard input from the user.*/
public interface KeyboardInputPlugin extends Plugin {
	

	/**
	 * Call the {@link #onKeyPressed(Model,char,boolean)} event for all
	 * plugins that subscribed to it.
	 * 
	 * @param m         the model this event is called from
	 * @param character the character that was pressed (always lowercaseed)
	 * @param shift      whether the a shift key was being pressed
	 */
	public static void keyPressed(Model m, int character, boolean shift) {
		m.call("onKeyPressed", KeyboardInputPlugin.class,
				new Class[] { Model.class, int.class, boolean.class },
				new Object[] { m, character, shift});
	}
	
	
	/**
	 * Event for a key being pressed.
	 * 
	 * @param m         the model this event is called from
	 * @param character the character that was pressed (always lowercaseed)
	 * @param shift      whether the a shift key was being pressed
	 */
	public void onKeyPressed(Model m, int character, boolean shift);
	
	/**
	 * Call the {@link #onKeyReleased(Model,char,boolean)} event for all
	 * plugins that subscribed to it.
	 * 
	 * @param m         the model this event is called from
	 * @param character the character that was pressed (always lowercaseed)
	 * @param shift      whether the a shift key was being pressed
	 */
	public static void keyReleased(Model m, int character, boolean shift) {
		m.call("onKeyReleased", KeyboardInputPlugin.class,
				new Class[] { Model.class, int.class, boolean.class },
				new Object[] { m, character, shift});
	}
	
	
	/**
	 * Event for a key being released.
	 * 
	 * @param m         the model this event is called from
	 * @param character the character that was pressed (always lowercaseed)
	 * @param shift      whether the a shift key was being pressed
	 */
	public void onKeyReleased(Model m, int character, boolean shift);
}
