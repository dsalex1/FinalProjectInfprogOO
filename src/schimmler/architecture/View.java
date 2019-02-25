package schimmler.architecture;

/** A specific plugin meant to only display data to the end user. */
public interface View extends Plugin {

	/**
	 * A method that gets called regularry for frame updates.
	 * 
	 * @param m the model that triggered the event.
	 */
	public void update(Model m);
}
