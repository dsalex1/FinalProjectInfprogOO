package lightHouseSimulator;

import java.io.IOException;

import de.cau.infprogoo.lighthouse.LighthouseAccount;
import de.cau.infprogoo.lighthouse.LighthouseDisplay;

/**
 * Bridge between the LightHouseSimulator and the CAU LighthouseDisplay.
 */
public class LightHouseBridge {
	
	/** The display instance associated with this handler. */
	private LighthouseDisplay display;
	
	/**
	 * Try to log into the light house.
	 * @return whether logging in was successful.
	 */
	public boolean login() {
		display = new LighthouseDisplay(LighthouseAccount.USERNAME, LighthouseAccount.TOKEN);
		try {
		    display.connect();
		} catch (Exception e) {
		    System.out.println("Connection failed: " + e.getMessage());
		    display = null;
		    e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	/**
	 * Check whether a connection to the lighthouse is currently active.
	 * @return whether a connection is there
	 */
	public boolean isConnected() {
		return display != null && display.isConnected();
	}
	
	/**
	 * Try to send the canvas of the LightHouseSimulator to the light house.
	 * @param sim the simulator with the canvas to use
	 * @return whether sending it was successful.
	 */
	public boolean send(LightHouseSimulator sim) {
		if(display == null) {
			System.err.println("Display is not connected.");
			return false;
		}
		byte[] data = new byte[sim.getDirectData().length];
		for(int i=0;i<data.length;i++)
			data[i] = (byte)(sim.getDirectData()[i]&0xFF);
		
		try {
		    display.send(data);
		} catch (IOException e) {
		    System.err.println("Connection failed: " + e.getMessage());
		    display = null;
		    e.printStackTrace();
		    return false;
		}
		return true;
	}
	
	/**
	 * Close the connection to the light house.
	 */
	public void close() {
		if(display == null) return;
		display.close();
	}
}
