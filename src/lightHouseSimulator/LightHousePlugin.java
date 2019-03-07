package lightHouseSimulator;

import schimmler.architecture.Model;
import schimmler.architecture.plugin.GraphicalFilterPlugin;
import schimmler.architecture.plugin.GraphicalView;

public class LightHousePlugin implements GraphicalFilterPlugin {

	private LightHouseSimulator lightHouse;
	
	@Override
	public void init(Model m) {
		lightHouse = new LightHouseSimulator();
		lightHouse.setGapsRatio(1, 0);
	}

	@Override
	public void onFilter(Model m, GraphicalView view, int[] img, int width, int height) {
		lightHouse.setData(img, width, height);
	}
	
	public void onKill(Model m) {
		lightHouse.exit();
	}
	

	@Override
	public String getName() {
		return "LightHousePlugin";
	}
	
	
	public boolean shouldFilter(int priority) {
		return priority == GraphicalFilterPlugin.PRIORITY_LAST;
	}

}
