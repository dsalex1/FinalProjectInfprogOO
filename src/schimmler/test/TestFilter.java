package schimmler.test;

import schimmler.architecture.GraphicalFilterPlugin;
import schimmler.architecture.Model;

public class TestFilter implements GraphicalFilterPlugin {

	@Override
	public void init(Model m) {
	}

	@Override
	public String getName() {
		return "TestFilter";
	}

	@Override
	public void onFilter(Model m, int[] img, int width, int height) {
		for(int x=0;x<width;x++) {
			for(int y=0;y<height;y++) {
				img[(x*width+y)] ^= 0xFFFFFF;
			}
		}
	}

}
