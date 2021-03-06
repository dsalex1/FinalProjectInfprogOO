package lightHouseSimulator;

@SuppressWarnings("serial")
public class LightHouseOnline extends LightHouseSimulator {

	
	private LightHouseBridge bridge;
	
	// Don't init any images, resources or actual frame
	@Override
	protected void init() {
		bridge = new LightHouseBridge();
		if(!bridge.login())
			throw new RuntimeException("Loggin into Lighthouse failed.");
		
	}
	
	public LightHouseBridge getBridge() {
		return bridge;
	}
	
	@Override
	public void render() {
	//	if(!bridge.isConnected()) // this apparently does not work
	//		throw new RuntimeException("No connection established.");
		bridge.send(this);
	}
	
	@Override
	public void updateFrame() {}
	

	
	public void exit() {
		bridge.close();
	}
	
}
