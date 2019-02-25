package schimmler.test;

import java.util.HashMap;

import schimmler.architecture.Model;
import schimmler.architecture.View;

public class TestView implements View {

	@Override
	public void init(Model m) {
		render(m);
	}

	@Override
	public String getName() {
		return "ASCIIView";
	}

	@Override
	public void update(Model m) {

	}

	// render the current frame as ASCII art
	private void render(Model m) {
		if (m.getLevel() == null)
			return;
		HashMap<String, Character> map = new HashMap<String, Character>();
		char c = '0';
		for (String s : m.getLevel().getTileMap().keySet())
			map.put(s, c++);

		map.put(null, ' ');

		System.out.print('|');
		for (int x = 0; x < m.getLevel().getWidth(); x++)
			System.out.print('=');
		System.out.print('|');
		System.out.println();
		for (int y = 0; y < m.getLevel().getHeigth(); y++) {
			System.out.print('|');
			for (int x = 0; x < m.getLevel().getWidth(); x++)
				System.out.print(map.get(m.getLevel().fieldOccupied(x, y)));
			System.out.print('|');
			System.out.println();
		}
		System.out.print('|');
		for (int x = 0; x < m.getLevel().getWidth(); x++)
			System.out.print('=');
		System.out.print('|');
		System.out.println();
	}

}
